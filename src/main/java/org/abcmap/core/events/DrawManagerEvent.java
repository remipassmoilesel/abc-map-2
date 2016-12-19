package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class DrawManagerEvent extends Event {
    public static final String TOOL_CHANGED = "TOOL_CHANGED";
    public static final String DRAW_STROKE_CHANGED = "DRAW_STROKE_CHANGED";
    public static final String TOOL_MODE_CHANGED = "TOOL_MODE_CHANGED";
    public static final String WITNESSES_CHANGED = "WITNESSES_CHANGED";

    public DrawManagerEvent(String event, Object value) {
        super(event, value);
    }

    public DrawManagerEvent(String name) {
        super(name, null);
    }
}