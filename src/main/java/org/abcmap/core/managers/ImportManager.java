package org.abcmap.core.managers;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class ImportManager implements HasEventNotificationManager {

    private final EventNotificationManager notifm;

    public ImportManager() {
        this.notifm = new EventNotificationManager(ImportManager.this);
    }

    public void stopCropConfiguration() {

    }

    @Override
    public EventNotificationManager getNotificationManager() {
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
