package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class MapEvent extends Event {

    public static final String GEOSYSTEM_CHANGED = "GEOREFERENCES_CHANGED";
    public static final String DISPLAY_SCALE_CHANGED = "DISPLAY_SCALE_CHANGED";

    public MapEvent(String name, Object value) {
        super(name, value);
    }
}
