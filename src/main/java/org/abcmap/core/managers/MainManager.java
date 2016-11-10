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
    private static MainManager instance;

    // here it is the only logger directly instantiated
    private static final CustomLogger log = new CustomLogger(MainManager.class);
    private ConfigurationManager configurationManager;
    private LogManager logManager;
    private TempFilesManager tempFilesManager;

    /**
     * Initialize all managers
     */
    public static void init() throws IOException {
        if (instance != null) {
            log.warning("Main manager already initialized");
            return;
        }

        instance = new MainManager();
    }

    private MainManager() throws IOException {

        ThreadManager.init();
        configurationManager = new ConfigurationManager();
        tempFilesManager = new TempFilesManager();

    }

    public static boolean isDebugMode() {
        return DEBUG_MODE;
    }

    public static ConfigurationManager getConfigurationManager() {
        return instance.configurationManager;
    }

    public static TempFilesManager getTempFilesManager() {
        return instance.tempFilesManager;
    }


}
