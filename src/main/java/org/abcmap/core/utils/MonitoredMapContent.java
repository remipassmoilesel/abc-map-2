package org.abcmap.core.utils;

import org.geotools.map.MapContent;

/**
 * Utility used to monitor how much map content are created
 */
public class MonitoredMapContent extends MapContent {

    private static final boolean debugMode = false;

    public static int mapContentNumber = 0;
    public int mapContentId = 0;

    public MonitoredMapContent() {
        super();

        mapContentId = mapContentNumber;
        mapContentNumber++;

        if (debugMode == true) {
            new Exception("Map content stack trace " + mapContentId + " " + this).printStackTrace();
        }
    }

    @Override
    public void finalize() throws Throwable {

        // /!\ Warning: this method will be not necessary used, maybe only super will be called
        if (debugMode == true) {
            System.err.println("Finalize stack trace " + mapContentId + " " + this);
        }

        super.finalize();
    }
}
