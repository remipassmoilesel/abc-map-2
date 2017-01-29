package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class ClipboardEvent extends Event {

    public static final String NEW_IMAGE = "NEW_IMAGE";

    public ClipboardEvent(String event, Object value) {
        super(event, value);
    }

}