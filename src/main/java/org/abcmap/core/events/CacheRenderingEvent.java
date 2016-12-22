package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

/**
 * Created by remipassmoilesel on 15/12/16.
 */
public class CacheRenderingEvent extends Event {

    public static final String NEW_PARTIALS_AVAILABLE = "NEW_PARTIALS_AVAILABLE";
    public static final String PARTIALS_DELETED = "PARTIALS_DELETED";

    public CacheRenderingEvent(String name) {
        super(name);
    }

    public CacheRenderingEvent(String name, Object value) {
        super(name, value);
    }

    public static boolean isPartialsDeletedEvent(Event ev) {
        return testEvent(CacheRenderingEvent.class, PARTIALS_DELETED, ev);
    }

    public static boolean isNewPartialsEvent(Event ev) {
        return testEvent(CacheRenderingEvent.class, NEW_PARTIALS_AVAILABLE, ev);
    }


}