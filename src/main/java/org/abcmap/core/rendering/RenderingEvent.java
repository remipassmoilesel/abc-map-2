package org.abcmap.core.rendering;

import org.abcmap.core.events.manager.Event;

/**
 * Created by remipassmoilesel on 15/12/16.
 */
public class RenderingEvent extends Event {

    public static final String NEW_PARTIAL_LOADED = "NEW_PARTIAL_LOADED";

    public RenderingEvent(String name) {
        super(name);
    }

    public RenderingEvent(String name, Object value) {
        super(name, value);
    }
}
