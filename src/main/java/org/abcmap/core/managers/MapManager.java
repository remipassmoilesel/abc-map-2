package org.abcmap.core.managers;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.gui.components.map.CachedMapPane;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager implements HasEventNotificationManager {

    private final EventNotificationManager notifm;
    private final GuiManager guim;

    public MapManager() {
        guim = MainManager.getGuiManager();
        notifm = new EventNotificationManager(MapManager.this);
    }

    public JPanel getMapComponent() {
        return new JPanel();
    }

    public boolean isGeoreferencementEnabled() {
        return false;
    }

    public int getDisplayScale() {
        return 0;
    }

    public Point getScaledPoint(Point point) {
        return point;
    }

    public CoordinateReferenceSystem getCRS(String code) {
        return DefaultGeographicCRS.WGS84;
    }

    public static String getEpsgCode(CoordinateReferenceSystem system) {
        return "";
    }

    /**
     * Return main map panel of software.
     * <p>
     * Can be null, and change at least every time project change
     *
     * @return
     */
    public CachedMapPane getMainMap() {
        return guim.getMainWindow().getMap();
    }

    public void zoomInMainMap() {

        if (getMainMap() == null) {
            return;
        }

        getMainMap().zoomIn();
        refreshMainMap();
    }

    public void zoomOutMainMap() {

        if (getMainMap() == null) {
            return;
        }

        getMainMap().zoomOut();
        refreshMainMap();
    }

    public void resetDisplay() {
        if (getMainMap() == null) {
            return;
        }

        CachedMapPane map = getMainMap();
        map.resetDisplay();
        map.repaint();
    }

    public void refreshMainMap() {

        CachedMapPane map = getMainMap();
        if (map == null) {
            return;
        }

        map.refreshMap();
        map.repaint();
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }
}
