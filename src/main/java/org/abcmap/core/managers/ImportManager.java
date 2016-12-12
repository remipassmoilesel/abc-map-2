package org.abcmap.core.managers;

import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class ImportManager implements HasNotificationManager {

    private final NotificationManager notifm;

    public ImportManager() {
        this.notifm = new NotificationManager(ImportManager.this);
    }

    public void stopCropConfiguration() {

    }

    @Override
    public NotificationManager getNotificationManager() {
        return notifm;
    }

    public ArrayList<String> getDataImportCurrentHeaders() {
        return new ArrayList<>();
    }


    public boolean isValidExtensionsForTile(String ext) {
        return false;
    }

    public BufferedImage catchScreen(ArrayList<Component> visibleFrames, boolean b) {
        return null;
    }
}
