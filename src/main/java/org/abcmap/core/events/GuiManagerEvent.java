package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class GuiManagerEvent extends Event {

    public static final String WINDOW_MODE_CHANGED = "WINDOW_MODE_CHANGED";

    public GuiManagerEvent(String event, Object value) {
        super(event, value);
    }

    public static boolean isWindowModeNotification(Event arg) {

        if (arg instanceof GuiManagerEvent == false) {
            return false;
        }

        if (arg.getName().equals(WINDOW_MODE_CHANGED) == false) {
            return false;
        }

        return true;
    }

}