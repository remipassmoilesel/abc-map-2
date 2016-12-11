package org.abcmap.core.managers;

import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.windows.DetachedWindow;
import org.abcmap.gui.windows.MainWindowMode;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class GuiManager implements HasNotificationManager {

    private final NotificationManager notifm;

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
}

