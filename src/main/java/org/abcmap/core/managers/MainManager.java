package org.abcmap.core.managers;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.threads.ThreadManager;

import java.io.IOException;

/**
 * Main manager.
 * <p>
 * From here, all other managers can be reached.
 */
public class MainManager {

    private static final boolean DEBUG_MODE = true;
    private static boolean initialized = false;

    /**
     * Here it is the only logger directly instantiated
     * Use LogManager.getLogger() instead.
     */
    private static final CustomLogger log = new CustomLogger(MainManager.class);
    private static ConfigurationManager configurationManager;
    private static TempFilesManager tempFilesManager;
    private static ProjectManager projectManager;

    /**
     * Initialize all managers
     */
    public static void init() throws IOException {

        if (initialized != false) {
            log.warning("Main manager already initialized");
            return;
        }

        ThreadManager.init();
        configurationManager = new ConfigurationManager();
        tempFilesManager = new TempFilesManager();
        projectManager = new ProjectManager();

        initialized = true;
    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public static TempFilesManager getTempFilesManager() {
        return tempFilesManager;
    }

    public static ProjectManager getProjectManager() {
        return projectManager;
    }
}
