package org.abcmap.core.managers;

import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.windows.DetachedWindow;
import org.abcmap.gui.windows.MainWindowMode;

import java.awt.*;

/**
 *
 */
public class GuiManager {


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
}
