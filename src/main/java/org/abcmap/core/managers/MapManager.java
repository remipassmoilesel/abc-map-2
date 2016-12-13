package org.abcmap.core.managers;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager implements HasEventNotificationManager {

    private final EventNotificationManager notifm;

    public MapManager() {
        notifm = new EventNotificationManager(MapManager.this);
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
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
}
