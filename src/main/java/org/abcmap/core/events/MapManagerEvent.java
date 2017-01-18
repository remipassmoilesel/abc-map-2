package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class MapManagerEvent extends Event {

    public static final String PREDEFINED_WMS_LIST_CHANGED = "PREDEFINED_WMS_LIST_CHANGED";

    public MapManagerEvent(String name) {
        this(name, null);
    }

    public MapManagerEvent(String name, Object value) {
        super(name, value);
    }
}
