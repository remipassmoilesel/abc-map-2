package org.abcmap.core.managers;

import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class MapManager implements HasNotificationManager {

    private final NotificationManager notifm;

    public MapManager(){
        notifm = new NotificationManager(MapManager.this);
    }

    @Override
    public NotificationManager getNotificationManager() {
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
}
