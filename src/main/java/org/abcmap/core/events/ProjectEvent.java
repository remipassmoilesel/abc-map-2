package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class ProjectEvent extends Event {

    public static final String NEW_PROJECT_LOADED = "NEW_PROJECT_LOADED";
    public static final String PROJECT_CLOSED = "PROJECT_CLOSED";

    public static final String LAYER_CHANGED = "LAYER_CHANGED";
    public static final String LAYERS_LIST_CHANGED = "LAYERS_LIST_CHANGED";

    public static final String LAYOUTS_LIST_CHANGED = "LAYOUTS_CHANGED";

    public static final String METADATA_CHANGED = "METADATA_CHANGED";


    public ProjectEvent(String name) {
        this(name, null);
    }

    public ProjectEvent(String name, Object value) {
        super(name, value);
    }

    /**
     * Return true if this event is a new project event
     *
     * @param arg
     * @return
     */
    public static boolean isNewProjectLoadedEvent(Event arg) {
        return testEvent(ProjectEvent.class, NEW_PROJECT_LOADED, arg);
    }

    /**
     * Return true if this event is a close event
     *
     * @param arg
     * @return
     */
    public static boolean isCloseProjectEvent(Event arg) {
        return testEvent(ProjectEvent.class, PROJECT_CLOSED, arg);
    }

}
