package org.abcmap.core.events;

import org.abcmap.core.events.manager.Event;

public class ImportManagerEvent extends Event {

    public static final String CROP_AREA_CONFIGURATION_START = "CROP_AREA_CONFIGURATION_START";
    public static final String CROP_AREA_CONFIGURATION_STOP = "CROP_AREA_CONFIGURATION_STOP";

    public static final String DOCUMENT_IMPORT_STARTED = "DOCUMENT_IMPORT_STARTED";
    public static final String DOCUMENT_IMPORT_FINISHED = "DOCUMENT_IMPORT_FINISHED";
    public static final String DOCUMENT_IMPORT_ABORTED = "DOCUMENT_IMPORT_ABORTED";

    public static final String DIRECTORY_IMPORT_STARTED = "DIRECTORY_IMPORT_STARTED";
    public static final String DIRECTORY_IMPORT_FINISHED = "DIRECTORY_IMPORT_FINISHED";
    public static final String DIRECTORY_IMPORT_ABORTED = "DIRECTORY_IMPORT_ABORTED";

    public static final String ROBOT_IMPORT_STARTED = "ROBOT_IMPORT_STARTED";
    public static final String ROBOT_IMPORT_ABORTED = "ROBOT_IMPORT_ABORTED";
    public static final String ROBOT_IMPORT_FINISHED = "ROBOT_IMPORT_FINISHED";

    public static final String PARAMETERS_CHANGED = "PARAMETERS_CHANGED";

    public static final String MANUAL_IMPORT_STARTED = "MANUAL_IMPORT_STARTED";
    public static final String MANUAL_IMPORT_FINISHED = "MANUAL_IMPORT_FINISHED";
    public static final String MANUAL_IMPORT_ABORTED = "MANUAL_IMPORT_ABORTED";

    public static final String DATA_HEADERS_CHANGED = "WAITING_LIST_CHANGED";

    public ImportManagerEvent(String event, Object value) {
        super(event, value);
    }
}
