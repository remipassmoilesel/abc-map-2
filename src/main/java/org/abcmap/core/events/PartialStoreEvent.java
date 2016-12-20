package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class PartialStoreEvent extends Event {

    public static final String NEW_PARTIALS_AVAILABLE = "NEW_PARTIALS_AVAILABLE";

    public PartialStoreEvent(String name, Object value) {
        super(name, value);
    }

}
