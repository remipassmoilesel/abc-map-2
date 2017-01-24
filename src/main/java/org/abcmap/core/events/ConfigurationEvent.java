package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class ConfigurationEvent extends Event {

    public static final String CONFIGURATION_UPDATED = "CONFIGURATION_UPDATED";

    public ConfigurationEvent(String event) {
        super(event, null);
    }

    public ConfigurationEvent(String event, Object value) {
        super(event, value);
    }

}