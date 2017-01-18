package org.abcmap.core.managers;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.wms.PredefinedWmsServer;
import org.abcmap.core.wms.ServerConstantsJson;
import org.abcmap.gui.components.map.CachedMapPane;
import org.apache.commons.io.IOUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(MapManager.class);

    private final EventNotificationManager notifm;
    public MainMapBinding mainmap;
    private ArrayList<PredefinedWmsServer> listOfWmsServers;

    public MapManager() {
        notifm = new EventNotificationManager(MapManager.this);
        mainmap = new MainMapBinding();
        listOfWmsServers = new ArrayList<PredefinedWmsServer>();
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
     * Return a list of predefined WMS server which can be used to add WMS layer
     *
     * @return
     */
    public ArrayList<PredefinedWmsServer> getListOfPredefinedWmsServers() {

        // nothing found in list, load local server list
        if (listOfWmsServers.isEmpty() == true) {
            listOfWmsServers.addAll(getLocaleListOfPredefinedWMSServers());
        }

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
    public PredefinedWmsServer getPredefinedWMSServer(String name, String url) {

        if (name == null && url == null) {
            throw new NullPointerException("At least one of parameter should not be null");
        }

        for (PredefinedWmsServer server : listOfWmsServers) {
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
     * Load local list of predefined WMS servers
     */
    public ArrayList<PredefinedWmsServer> getLocaleListOfPredefinedWMSServers() {

        ArrayList<PredefinedWmsServer> result = new ArrayList<>();

        try (BufferedInputStream res = new BufferedInputStream(
                MapManager.class.getResourceAsStream(ConfigurationConstants.LOCAL_WMS_SERVER_LIST))) {

            if (res == null) {
                throw new IOException("Unable to found locale list of servers: " + ConfigurationConstants.LOCAL_WMS_SERVER_LIST);
            }

            String rawListStr = IOUtils.toString(res);
            JSONArray jsonArray = new JSONArray(rawListStr);

            for (Object o : jsonArray) {

                // test if object is a wms server
                if (o instanceof JSONObject) {
                    JSONObject json = (JSONObject) o;
                    if (ServerConstantsJson.wms.equals(json.getString(ServerConstantsJson.type))) {
                        String name = json.getString(ServerConstantsJson.name);
                        String url = json.getString(ServerConstantsJson.url);
                        result.add(new PredefinedWmsServer(name, url));
                    }
                }
            }

        } catch (IOException e) {
            logger.error(e);
        }

        return result;
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
