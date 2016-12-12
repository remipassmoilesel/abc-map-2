package org.abcmap.core.managers;

import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.project.layer.LayerIndexEntry;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.windows.DetachedWindow;
import org.abcmap.gui.windows.MainWindowMode;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 */
public class GuiManager implements HasNotificationManager {

    private final NotificationManager notifm;
    private ArrayList<Component> visibleWindows;

    public GuiManager() {
        notifm = new NotificationManager(GuiManager.this);
    }


    public void addInitialisationOperation(Runnable run) {
    }

    public void setWindowIconFor(Window windowIconFor) {

    }

    public void setMainWindowMode(MainWindowMode mainWindowMode) {

    }

    public Cursor getClickableCursor() {
        return GuiCursor.HAND_CURSOR;
    }

    public void showGroupInDock(Class clss) {

    }

    public DetachedWindow getWizardDetachedWindow() {
        return new DetachedWindow();
    }

    public Window getMainWindow() {
        return new JDialog();
    }

    public MainWindowMode getMainWindowMode() {
        return MainWindowMode.SHOW_MAP;
    }

    @Override
    public NotificationManager getNotificationManager() {
        return notifm;
    }

    public ArrayList<Component> getVisibleWindows() {
        return new ArrayList<>();
    }

    public void showCropWindow(BufferedImage finalBg) {

    }

    public Window getCropWindow() {
        return null;
    }

    public void showErrorInBox(String s) {

    }
}

