package org.abcmap.core.managers;

import org.abcmap.core.log.CustomLogger;

import java.io.IOException;

/**
 * Main manager.
 * <p>
 * From here, all other managers can be reached.
 */
public class MainManager {

    private static final boolean DEBUG_MODE = true;

    // here it is the only logger directly instantiated
    private static final CustomLogger log = new CustomLogger(MainManager.class);
    private static ConfigurationManager configurationManager = null;
    private static LogManager logManager;

    /**
     * Initialize all managers
     */
    public static void init() throws IOException {

        if (logManager != null) {
            log.warning("Main manager already initialized");
            return;
        }


        logManager = LogManager.initialize();

        configurationManager = new ConfigurationManager();

    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public static LogManager getLogManager() {
        return logManager;
    }

}
