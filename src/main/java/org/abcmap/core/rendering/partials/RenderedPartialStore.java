package org.abcmap.core.rendering.partials;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import org.abcmap.core.events.CacheRenderingEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.SQLUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Store partials in RAM and in database
 * <p>
 * Partials should contains only soft links to images, in order to free memory when needed
 * <p>
 * Partial store use Coordinate Reference System of project
 */
public class RenderedPartialStore implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(RenderedPartialStore.class);

    private static final FilterFactory2 ff = FeatureUtils.getFilterFactory();

    /**
     * Precision used when search existing partials by area in database
     */
    private static final Double PRECISION = 0.0001d;

    /**
     * Maximum number of partials which can be stored in RAM
     * <p>
     * This is a theoretical value, only a few partials will be really loaded (with image) and
     * just the most recent partials are used
     */
    private static final int MAX_LOADED_LIST_SIZE = 1000;

    /**
     * List of partials already used. They can be complete (with an image loaded) or not.
     * <p>
     * This list is not synchronized because it is a bad idea so iterate only copies
     */
    private ArrayList<RenderedPartial> loadedPartials;

    /**
     * We need to store outlines of features, in order to eventually delete them by area.
     * <p>
     * Each time a partial is created, an outline feature is reated too and stored in this store.
     */
    private final FeatureStore outlineFeatureStore;

    /**
     * Datastore associated with feature store
     */
    private final JDBCDataStore datastore;

    /**
     * Outline feature builder
     */
    private final PartialOutlineFeatureBuilder outlineFeatureBuilder;

    /**
     * Database object used to serialize partials, with images
     */
    private final Dao<SerializableRenderedPartial, Long> dao;

    /**
     * Connection used by dao
     */
    private final JdbcPooledConnectionSource connectionSource;

    /**
     * Coordinate reference system of store, should be the same as the project
     */
    private CoordinateReferenceSystem crs;

    private final EventNotificationManager notifm;
    private static long addedInDatabase = 0;

    public RenderedPartialStore(Path databasePath, CoordinateReferenceSystem system) throws SQLException {

        this.loadedPartials = new ArrayList<>();
        this.crs = system;
        this.connectionSource = SQLUtils.getH2OrmliteConnectionPool(databasePath);

        // create tables
        TableUtils.createTableIfNotExists(connectionSource, SerializableRenderedPartial.class);

        // create dao object
        this.dao = DaoManager.createDao(connectionSource, SerializableRenderedPartial.class);

        // open a data store to store outlines of partials
        try {

            // create a feature builder to store outlines
            outlineFeatureBuilder = new PartialOutlineFeatureBuilder(crs);
            SimpleFeatureType type = outlineFeatureBuilder.getType();

            // open data store and create a feature scheme
            datastore = SQLUtils.getGeotoolsDatastoreFromH2(databasePath);
            datastore.createSchema(type);

            // open a feature store
            outlineFeatureStore = (FeatureStore) datastore.getFeatureSource(type.getTypeName());

        } catch (IOException e) {
            throw new SQLException("Cannot create datastore from: " + databasePath);
        }

        this.notifm = new EventNotificationManager(this);
    }

    /**
     * Return a corresponding rendered partial
     *
     * @param env
     * @return
     */
    public RenderedPartial searchInLoadedList(String layerId, ReferencedEnvelope env) {

        if (layerId == null) {
            throw new NullPointerException("Layer id is null");
        }

        if (env == null) {
            throw new NullPointerException("Envelope is null");
        }

        // iterate a copy to avoid Concurrent modification exception
        for (RenderedPartial part : new ArrayList<>(loadedPartials)) {
            if (GeoUtils.compareEnvelopes(part.getEnvelope(), env, PRECISION) && part.getLayerId().equals(layerId)) {
                return part;
            }
        }

        return null;
    }

    /**
     * Update a partial by adding rendered image. If a valid image is found, return true, if not return false.
     *
     * @param part
     * @return
     * @throws SQLException
     */
    public boolean updatePartialFromDatabase(RenderedPartial part) throws SQLException {

        // check if partial is in database
        ReferencedEnvelope area = part.getEnvelope();
        Where<SerializableRenderedPartial, ?> statement = dao.queryBuilder().where().raw(
                "ABS(" + SerializableRenderedPartial.PARTIAL_X1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_X2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND " + SerializableRenderedPartial.PARTIAL_LAYERID_FIELD_NAME + "=? ",

                new SelectArg(SqlType.DOUBLE, area.getMinX()),
                new SelectArg(SqlType.DOUBLE, area.getMaxX()),
                new SelectArg(SqlType.DOUBLE, area.getMinY()),
                new SelectArg(SqlType.DOUBLE, area.getMaxY()),
                new SelectArg(SqlType.STRING, part.getLayerId())
        );
        List<SerializableRenderedPartial> results = statement.query();

        // no results found
        if (results.size() < 1) {
            return false;
        }

        // too much results, show error
        // several partials covering the same area can be added from different threads
        // and unfortunately, ORM lite doesn't support multiple column primary key.
        if (results.size() > 1) {
            new SQLException("More than one result found: " + results.size()).printStackTrace();
        }

        // one result found, prepare it and return it
        SerializableRenderedPartial serializ = results.get(0);

        BufferedImage img = serializ.getImage();
        int w = img.getWidth();
        int h = img.getHeight();
        part.setImage(img, w, h);

        part.setDatabaseId(serializ.getId());

        // update in memory partial
        addInLoadedList(part);

        return true;

    }

    /**
     * Add partial only in loaded list (RAM)
     *
     * @param part
     */
    public void addInLoadedList(RenderedPartial part) {

        loadedPartials.add(part);

        // limit size of loaded list in order to avoid too much research time
        int toRemove = loadedPartials.size() - MAX_LOADED_LIST_SIZE;
        if (toRemove > 0) {
            for (int i = 0; i < toRemove; i++) {
                loadedPartials.remove(0);
            }
        }

    }

    /**
     * Add partial in loaded list and in database
     *
     * @param part
     * @throws SQLException
     */
    public void addPartial(RenderedPartial part) throws SQLException {

        if (part.getImage() == null) {
            throw new NullPointerException("Image is null");
        }

        // serialize partial in database
        SerializableRenderedPartial serializable = new SerializableRenderedPartial(part);
        dao.create(serializable);

        // create outline of partial, clockwise round from lower right corner
        ReferencedEnvelope env = part.getEnvelope();

        // Serialize outline. Be sure to assicate the good ID ! (from db)
        try {
            Polygon outline = JTS.toGeometry(part.getEnvelope());
            SimpleFeature feature = outlineFeatureBuilder.build(outline, serializable.getId(), part.getLayerId());
            outlineFeatureStore.addFeatures(FeatureUtils.asList(feature));
        } catch (IOException e) {
            throw new SQLException("Unable to insert this partial outline: " + part + " / " + env);
        }

        // add partial in loaded list
        addInLoadedList(part);

        addedInDatabase++;

        fireNewPartialsAdded();
    }

    public static long getAddedInDatabase() {
        return addedInDatabase;
    }

    /**
     * Return a shallow copy of partial list
     *
     * @return
     */
    public ArrayList<RenderedPartial> getLoadedPartials() {
        return new ArrayList<>(loadedPartials);
    }

    /**
     * Delete partials associated with layer id in memory and in database
     * <p>
     * All is done on this thread
     *
     * @param layerId
     */
    public void deletePartialsForLayer(String layerId) {
        deletePartialsForLayer(layerId, null);
    }

    /**
     * Delete partials associated with layer id in memory and in database, but only if they intersect the specified referenced envelope.
     * <p>
     * Envelope can be null, in this case whole layer will be removed
     *
     * @param layerId
     * @param boundsToDelete
     */
    public void deletePartialsForLayer(String layerId, ReferencedEnvelope boundsToDelete) {

        if (layerId == null) {
            throw new NullPointerException("Layer id cannot be null");
        }

        if (boundsToDelete != null && boundsToDelete.getCoordinateReferenceSystem().equals(crs) == false) {
            throw new IllegalArgumentException("Invalid crs: " + crs + " / " + boundsToDelete.getCoordinateReferenceSystem());
        }

        // Force update of in memory partials
        // Here we do not remove partials from list to avoid flickering.
        // Existing part stay in memory until new one replace it, to prevent paint 'null' images
        for (RenderedPartial part : new ArrayList<>(loadedPartials)) {

            // check layer id of partial
            if (Utils.safeEquals(part.getLayerId(), layerId)) {

                // all layer
                if (boundsToDelete == null) {
                    part.setOutdated(true);
                    //loadedPartials.remove(part);
                }

                // on specified area
                else {
                    if (part.getEnvelope().intersects((com.vividsolutions.jts.geom.Envelope) boundsToDelete) == true) {
                        part.setOutdated(true);
                        //loadedPartials.remove(part);
                    }
                }

            }
        }

        // delete in database
        try {

            // delete from whole layer
            if (boundsToDelete == null) {

                // delete partials
                DeleteBuilder<SerializableRenderedPartial, ?> db = dao.deleteBuilder();
                db.where().raw(SerializableRenderedPartial.PARTIAL_LAYERID_FIELD_NAME + "=? ", new SelectArg(SqlType.STRING, layerId));
                db.delete();

                // delete outlines
                Filter filter = PartialOutlineFeatureBuilder.getLayerIdFilter(layerId);
                outlineFeatureStore.removeFeatures(filter);
            }

            // or just some
            else {

                // get all id to delete, filter them by intersection
                Collection<Long> partialIds = new ArrayList<>();
                HashSet<Identifier> outlineIds = new HashSet<>();

                Filter filter = PartialOutlineFeatureBuilder.getAreaFilter(boundsToDelete);
                filter = ff.and(filter, PartialOutlineFeatureBuilder.getLayerIdFilter(layerId));

                ArrayList<ReferencedEnvelope> envs = new ArrayList<>();

                FeatureIterator features = outlineFeatureStore.getFeatures(filter).features();
                while (features.hasNext()) {
                    Feature feat = features.next();
                    partialIds.add(PartialOutlineFeatureBuilder.getId(feat));
                    outlineIds.add(feat.getIdentifier());
                    envs.add(JTS.toEnvelope((Geometry) feat.getDefaultGeometryProperty().getValue()));
                }
                features.close();

                // remove partials first
                dao.deleteIds(partialIds);

                // remove outlines
                outlineFeatureStore.removeFeatures(ff.id(outlineIds));

            }

        } catch (Exception e) {
            logger.error(e);
        }

        firePartialsDeleted();

    }

    /**
     * Get store where outlines of partials are saved
     * <p>
     * Use it for debug purposes only
     *
     * @return
     */
    public FeatureStore getOutlineFeatureStore() {
        return outlineFeatureStore;
    }

    /**
     * Get builder able to construct features representing outline of partials
     *
     * @return
     */
    public PartialOutlineFeatureBuilder getOutlineFeatureBuilder() {
        return outlineFeatureBuilder;
    }

    /**
     * Close resource if needed
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        if (connectionSource != null) {
            logger.debug("Database connection was not closed: " + this + " / " + connectionSource);
            close();
        }

    }

    /**
     * Close this store and all internal connections
     *
     * @throws IOException
     */
    public void close() throws IOException {

        if (connectionSource != null) {
            connectionSource.close();
        }

        if (dao != null) {
            dao.closeLastIterator();
        }

        if (datastore != null) {
            datastore.dispose();
        }

    }

    private void fireNewPartialsAdded() {
        notifm.fireEvent(new CacheRenderingEvent(CacheRenderingEvent.NEW_PARTIALS_AVAILABLE, null));
    }

    private void firePartialsDeleted() {
        notifm.fireEvent(new CacheRenderingEvent(CacheRenderingEvent.PARTIALS_DELETED, null));
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public CoordinateReferenceSystem getCrs() {
        return crs;
    }
}
