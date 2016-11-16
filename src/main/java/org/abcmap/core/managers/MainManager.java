package org.abcmap.core.managers;

import com.j256.ormlite.logger.LocalLog;
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
    private static DrawManager drawManager;

    /**
     * Initialize all managers
     */
    public static void init() throws IOException {

        if (isInitialized() != false) {
            log.warning("Main manager already initialized");
            return;
        }

        configureTierceLibraries();

        ThreadManager.init();

        configurationManager = new ConfigurationManager();
        tempFilesManager = new TempFilesManager();
        projectManager = new ProjectManager();
        drawManager = new DrawManager();

        setInitialized(true);
    }

    private static void configureTierceLibraries() {

        // deacrease database log
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty(LocalLog.LOCAL_LOG_PROPERTIES_FILE, "ormlite-log.txt");

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

    public static DrawManager getDrawManager() {
        return drawManager;
    }

    /**
     * Return true if the main manager is initialized
     * @return
     */
    public static boolean isInitialized() {
        return initialized;
    }

    private static void setInitialized(boolean initialized) {
        MainManager.initialized = initialized;
    }
}
