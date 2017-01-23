package org.abcmap.core.managers;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.events.MapManagerEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmShapefileLayer;
import org.abcmap.core.resources.*;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.utils.GuiUtils;
import org.apache.commons.io.IOUtils;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(MapManager.class);

    private final EventNotificationManager notifm;
    public MainMapBinding mainmap;
    private ArrayList<WmsResource> listOfWmsServers;

    public MapManager() {
        notifm = new EventNotificationManager(MapManager.this);
        mainmap = new MainMapBinding();
        listOfWmsServers = new ArrayList<>();

        listOfWmsServers.addAll(getLocaleListOfPredefinedWmsServers());
    }

    public ArrayList<DistantResource> getResourceIndexFromRepo(String repoUrl) throws IOException {
        String indexContent = Utils.getHttpResourceAsString(getIndexUrlForRepo(repoUrl), 2000);
        return DistantResourceReader.parseResourceIndex(indexContent, repoUrl);
    }



    /**
     * Return main map panel of software.
     * <p>
     * Can be null, and change at least every time project change
     *
     * @return
     */
    public CachedMapPane getMainMap() {
        return guim().getMainWindow().getMap();
    }

    /**
     * Try to download and parse distant list of WMS servers
     * <p>
     * If nothing is found or if an error occur, return an empty list
     *
     * @return
     */
    public ArrayList<WmsResource> getDistantListOfPredefinedWmsServers() throws IOException {

        ArrayList<DistantResource> resources = getMainResourceIndex();
        ArrayList<WmsResource> result = new ArrayList<>();
        for (DistantResource r : resources) {
            if (r instanceof WmsResource) {
                result.add((WmsResource) r);
            }
        }

        return result;
    }

    public ArrayList<DistantResource> getMainResourceIndex() throws IOException {
        return getResourceIndexFromRepo(ConfigurationConstants.DISTANT_RESOURCE_INDEX);
    }

    public String getIndexUrlForRepo(String repoUrl) {

        if (repoUrl.substring(repoUrl.length() - 2).equals("/") == false) {
            repoUrl += "/";
        }
        repoUrl += ConfigurationConstants.DEFAULT_INDEX_NAME;

        return repoUrl;

    }

    /**
     * Return the current list of predefined WMS server which can be used to add WMS layer.
     * <p>
     * This list should contains locale list and distant list if any.
     *
     * @return
     */
    public ArrayList<WmsResource> getListOfPredefinedWmsServers() {
        return new ArrayList<>(listOfWmsServers);
    }

    /**
     * Search in predefined WMS server if one match provided name OR provided url
     * <p>
     * Provided name OR url can be null.
     *
     * @param name
     * @param url
     * @return
     */
    public WmsResource getPredefinedWmsServer(String name, String url) {

        if (name == null && url == null) {
            throw new NullPointerException("At least one of parameter should not be null");
        }

        for (WmsResource server : listOfWmsServers) {
            if (name != null && server.getName().equals(name)) {
                return server;
            }
            if (url != null && server.getUrl().equals(url)) {
                return server;
            }
        }

        return null;
    }

    /**
     * Load the locale list of predefined WMS servers
     * <p>
     * Return an empty list if an error occur, or if nothing found
     */
    public ArrayList<WmsResource> getLocaleListOfPredefinedWmsServers() {

        try (BufferedInputStream res = new BufferedInputStream(
                MapManager.class.getResourceAsStream(ConfigurationConstants.LOCAL_WMS_SERVER_LIST))) {

            if (res == null) {
                throw new IOException("Unable to found locale list of servers: " + ConfigurationConstants.LOCAL_WMS_SERVER_LIST);
            }

            String rawListStr = IOUtils.toString(res);

            ArrayList<DistantResource> resources = DistantResourceReader.parseResourceIndex(rawListStr, "");
            ArrayList<WmsResource> result = new ArrayList<>();
            for (DistantResource r : resources) {
                if (r instanceof WmsResource) {
                    result.add((WmsResource) r);
                }
            }

            return result;
        } catch (IOException e) {
            logger.error(e);
        }

        return new ArrayList<>();
    }

    /**
     * Fire an event meaning that list of predefined servers changed
     * <p>
     * Return the number of updates found
     */
    public int updateListOfPredefinedWmsServers() throws IOException {

        GuiUtils.throwIfOnEDT();

        // get distant list of WMS servers
        ArrayList<WmsResource> distantList = getDistantListOfPredefinedWmsServers();

        // update list with elements which are not already in list
        int updates = 0;
        for (WmsResource server : distantList) {
            if (listOfWmsServers.contains(server) == false) {
                listOfWmsServers.add(server);
                updates++;
            }
        }

        // fire an event
        notifm.fireEvent(new MapManagerEvent(MapManagerEvent.PREDEFINED_WMS_LIST_CHANGED));

        return updates;
    }

    /**
     * Copy and reproject a shape file in data folder
     * <p>
     * And then add it in project. If "replace" is set to true, reprojected
     * <p>
     * layer will replace the previous shape file layer
     *
     * @param layer
     */
    public void openReprojectedShapefile(AbmShapefileLayer layer, boolean replace) {

        dialm().showReprojectShapeFileDialogAndWait(layer,

                // user want reproject
                () -> {

                    dialm().showMessageInBox("Début de reprojection, cette opération peut prendre un moment...");

                    CoordinateReferenceSystem worldCrs = projectm().getProject().getCrs();
                    Path source = Paths.get(layer.getShapefileEntry().getPath());
                    Path destination = ConfigurationConstants.DATA_DIR_PATH.resolve(source.getFileName());
                    try {

                        if (Files.isDirectory(destination)) {
                            destination = Paths.get(destination.toAbsolutePath() + "_" + System.currentTimeMillis());
                        }

                        Files.createDirectories(destination);

                        destination = destination.resolve(source.getFileName());
                        reprojectShapeFile(source, worldCrs, destination);

                        Project project = projectm().getProject();
                        if (replace == true) {
                            project.removeLayer(layer);
                        }

                        AbmShapefileLayer newLayer = project.addNewShapeFileLayer(destination);
                        project.setActiveLayer(newLayer);

                        projectm().fireLayerListChanged();

                    } catch (IOException e) {
                        logger.error(e);
                        dialm().showErrorInBox("Erreur lors de la reprojection");
                        return;
                    }


                    dialm().showMessageInBox("Fin de la reprojection");
                },

                // user want to add layer as is
                () -> {

                    Path source = Paths.get(layer.getShapefileEntry().getPath());

                    Project project = projectm().getProject();
                    AbmShapefileLayer newLayer = null;
                    try {
                        newLayer = project.addNewShapeFileLayer(source);
                        project.setActiveLayer(newLayer);
                        projectm().fireLayerListChanged();
                    } catch (IOException e) {
                        logger.error(e);
                    }

                },

                //user decline
                () -> {
                    dialm().showMessageInBox("Ouverture en cours...");
                });

    }

    /**
     * Reproject a shapefile and save it in a new place
     *
     * @param source
     * @param worldCRS
     * @param destination
     * @throws IOException
     */
    public void reprojectShapeFile(Path source, CoordinateReferenceSystem worldCRS, Path destination) throws IOException {

        GuiUtils.throwIfOnEDT();

        FileDataStore store = FileDataStoreFinder.getDataStore(source.toFile());
        SimpleFeatureSource featureSource = store.getFeatureSource();

        SimpleFeatureType schema = featureSource.getSchema();
        if (source.toAbsolutePath().equals(destination.toAbsolutePath())) {
            throw new IOException("Cannot replace files: " + destination + " > " + source);
        }

        CoordinateReferenceSystem dataCRS = schema.getCoordinateReferenceSystem();
        boolean lenient = true; // allow for some error due to different datums
        MathTransform transform = null;
        try {
            transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
        } catch (FactoryException e) {
            throw new IOException(e);
        }

        SimpleFeatureCollection featureCollection = featureSource.getFeatures();

        DataStoreFactorySpi factory = new ShapefileDataStoreFactory();

        // create a new shapefile
        Map<String, Serializable> create = new HashMap<>();
        create.put("url", destination.toFile().toURI().toURL());
        create.put("create spatial index", Boolean.TRUE);
        DataStore dataStore = factory.createNewDataStore(create);
        SimpleFeatureType featureType = SimpleFeatureTypeBuilder.retype(schema, worldCRS);
        dataStore.createSchema(featureType);

        // reproject
        Transaction transaction = new DefaultTransaction("Reproject");
        FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                dataStore.getFeatureWriterAppend(featureType.getTypeName(), transaction);
        SimpleFeatureIterator iterator = featureCollection.features();
        try {
            while (iterator.hasNext()) {
                // copy the contents of each feature and transform the geometry
                SimpleFeature feature = iterator.next();
                SimpleFeature copy = writer.next();
                copy.setAttributes(feature.getAttributes());

                Geometry geometry = (Geometry) feature.getDefaultGeometry();
                Geometry geometry2 = JTS.transform(geometry, transform);

                copy.setDefaultGeometry(geometry2);
                writer.write();
            }
            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            throw new IOException(e);
        } finally {
            writer.close();
            iterator.close();
            transaction.close();
        }
    }

    /**
     * Sub name space grouping method working on main map only
     * <p>
     * All methods here should work without throwing exceptions
     */
    public class MainMapBinding {

        public Point2D screenToWorld(Point point) {

            if (getMainMap() == null || getMainMap().getScreenToWorldTransform() == null) {
                return null;
            }

            return getMainMap().getScreenToWorldTransform().transform(point, null);
        }

        /**
         * Zoom in main map
         */
        public void zoomIn() {

            if (getMainMap() == null) {
                return;
            }

            getMainMap().zoomIn();
            refresh();
        }

        /**
         * Zoom out main map
         */
        public void zoomOut() {

            if (getMainMap() == null) {
                return;
            }

            getMainMap().zoomOut();
            refresh();
        }

        /**
         * Reset scale of display
         */
        public void resetDisplay() {
            if (getMainMap() == null) {
                return;
            }

            CachedMapPane map = getMainMap();
            map.resetDisplay();
            map.repaint();
        }

        /**
         * Refresh current map
         */
        public void refresh() {

            CachedMapPane map = getMainMap();
            if (map == null) {
                return;
            }

            map.refreshMap();
            map.repaint();
        }

        public void deleteCache(String id, ReferencedEnvelope bounds) {

            if (projectm().isInitialized() == false) {
                throw new IllegalStateException("Project non initialized");
            }

            projectm().getProject().deleteCacheForLayer(id, bounds);
        }
    }

    public boolean isGeoreferencementEnabled() {
        return false;
    }

    public CoordinateReferenceSystem getCRS(String code) {
        return DefaultGeographicCRS.WGS84;
    }

    public static String getEpsgCode(CoordinateReferenceSystem system) {
        return "";
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }
}
