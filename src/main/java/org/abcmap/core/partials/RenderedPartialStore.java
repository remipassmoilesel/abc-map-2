package org.abcmap.core.partials;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;
import org.abcmap.core.events.PartialStoreEvent;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Store partials in RAM and in database
 * <p>
 * Partials should contains only soft links to images, in order to free memory when needed
 */
public class RenderedPartialStore implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(RenderedPartialStore.class);

    /**
     * Precision used when search existing partials by area in database
     */
    private static final Double PRECISION = 0.001d;

    /**
     * Maximum number of partials which can be stored in RAM
     * <p>
     * This is a theorical value, only a few partials will be really loaded (with image) and
     * just the most recent partials are used
     */
    private static final int MAX_LOADED_LIST_SIZE = 1000;
    private final EventNotificationManager notifm;

    /**
     * List of partials already used. They can be complete (with an image loaded) or not.
     * <p>
     * This list is not synchronized because it is a bad idea so iterate only copies
     */
    private ArrayList<RenderedPartial> loadedPartials;

    private final Dao<SerializableRenderedPartial, ?> dao;
    private final JdbcPooledConnectionSource connectionSource;
    private static long addedInDatabase = 0;

    public RenderedPartialStore(Path databasePath) throws SQLException {

        this.loadedPartials = new ArrayList<>();

        this.connectionSource = new JdbcPooledConnectionSource("jdbc:h2:./" + databasePath, "", "");
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        connectionSource.setTestBeforeGet(true);
        connectionSource.initialize();

        // create tables
        TableUtils.createTableIfNotExists(connectionSource, SerializableRenderedPartial.class);

        // create dao object
        this.dao = DaoManager.createDao(connectionSource, SerializableRenderedPartial.class);

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
                        + "AND " + SerializableRenderedPartial.PARTIAL_CRS_FIELD_NAME + "=? "
                        + "AND " + SerializableRenderedPartial.PARTIAL_LAYERID_FIELD_NAME + "=? ",

                new SelectArg(SqlType.DOUBLE, area.getMinX()),
                new SelectArg(SqlType.DOUBLE, area.getMaxX()),
                new SelectArg(SqlType.DOUBLE, area.getMinY()),
                new SelectArg(SqlType.DOUBLE, area.getMaxY()),
                new SelectArg(SqlType.STRING, SerializableRenderedPartial.crsToId(area.getCoordinateReferenceSystem())),
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
        BufferedImage img = results.get(0).getImage();
        int w = img.getWidth();
        int h = img.getHeight();
        part.setImage(img, w, h);

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

        dao.create(new SerializableRenderedPartial(part));

        addInLoadedList(part);

        addedInDatabase++;

        fireNewPartialsAdded();
    }

    public static long getAddedInDatabase() {
        return addedInDatabase;
    }

    public ArrayList<RenderedPartial> getLoadedPartials() {
        return new ArrayList<>(loadedPartials);
    }

    public void deletePartialsForLayer(String layerId) {

        ThreadManager.runLater(() -> {
            try {
                Where<SerializableRenderedPartial, ?> statement = dao.deleteBuilder().where().raw(
                        SerializableRenderedPartial.PARTIAL_X2_FIELD_NAME + "=? ",

                        new SelectArg(SqlType.STRING, layerId)
                );

                statement.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
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

    public void close() throws IOException {

        if (connectionSource != null) {
            connectionSource.close();
        }

        if (dao != null) {
            dao.closeLastIterator();
        }

    }

    private void fireNewPartialsAdded() {
        notifm.fireNotification(new PartialStoreEvent(PartialStoreEvent.NEW_PARTIALS_ADDED, null));
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }
}
