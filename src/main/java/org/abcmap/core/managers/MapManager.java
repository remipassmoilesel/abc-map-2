package org.abcmap.core.managers;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.gui.components.map.CachedMapPane;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private final EventNotificationManager notifm;
    public MainMapBinding mainmap;

    public MapManager() {
        notifm = new EventNotificationManager(MapManager.this);
        mainmap = new MainMapBinding();
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
         * Zoomin main map
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

            if(projectm().isInitialized() == false){
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
