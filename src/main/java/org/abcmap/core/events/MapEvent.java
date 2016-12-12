package org.abcmap.core.events;

import org.abcmap.core.notifications.Notification;

public class MapEvent extends Notification {

    public static final String GEOSYSTEM_CHANGED = "GEOREFERENCES_CHANGED";
    public static final String DISPLAY_SCALE_CHANGED = "DISPLAY_SCALE_CHANGED";

    public MapEvent(String name, Object value) {
        super(name, value);
    }
}
