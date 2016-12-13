package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class ConfigurationEvent extends Event {

    public static final String NEW_CONFIGURATION_LOADED = "NEW_CONFIGURATION_LOADED";
    public static final String CONFIGURATION_SAVED = "CONFIGURATION_SAVED";
    public static final String CONFIGURATION_RESETED = "CONFIGURATION_RESETED";
    public static final String CONFIGURATION_UPDATED = "CONFIGURATION_UPDATED";

    public ConfigurationEvent(String event, Object value) {
        super(event, value);
    }

}