package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class RecentManagerEvent extends Event {

    public static final String HISTORY_CHANGED = "HISTORY_CHANGED";

    public RecentManagerEvent(String event) {
        super(event, null);
    }

    public RecentManagerEvent(String event, Object value) {
        super(event, value);
    }

}