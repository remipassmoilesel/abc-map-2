package org.abcmap.core.managers;

import com.j256.ormlite.logger.LocalLog;
import org.abcmap.core.log.CustomLogger;

import java.io.IOException;

/**
 * Main manager.
 * <p>
 * From here, all other managers can be reached.
 */
public class MainManager {

    private static final CustomLogger logger = LogManager.getLogger(MainManager.class);

    private static boolean debugMode = false;
    private static boolean initialized = false;

    private static ConfigurationManager configurationManager;
    private static TempFilesManager tempFilesManager;
    private static ProjectManager projectManager;
    private static DrawManager drawManager;
    private static CancelManager cancelManager;
    private static GuiManager guiManager;
    private static MapManager mapManager;
    private static ShortcutManager shortcutManager;
    private static RecentManager recentManager;
    private static ClipboardManager clipboardManager;
    private static ImportManager importManager;
    private static DialogManager dialogManager;
    private static LayoutManager layoutManager;

    /**
     * Initialize all managers
     */
    public static void init() throws IOException {

        if (isInitialized() != false) {
            logger.warning("Main manager already initialized");
            return;
        }

        configureLibraries();

        configurationManager = new ConfigurationManager();
        tempFilesManager = new TempFilesManager();
        projectManager = new ProjectManager();
        drawManager = new DrawManager();
        guiManager = new GuiManager();
        cancelManager = new CancelManager();
        mapManager = new MapManager();
        shortcutManager = new ShortcutManager();
        recentManager = new RecentManager();
        clipboardManager = new ClipboardManager();
        importManager = new ImportManager();
        dialogManager = new DialogManager();
        layoutManager = new LayoutManager();

        setInitialized(true);
    }

    private static void configureLibraries() {

        // deacrease database log
        System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
        System.setProperty(LocalLog.LOCAL_LOG_PROPERTIES_FILE, "ormlite-log.txt");

    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean val) {
        debugMode = val;
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

    private static void setInitialized(boolean initialized) {
        MainManager.initialized = initialized;
    }

    public static CancelManager getCancelManager() {
        return cancelManager;
    }

    public static GuiManager getGuiManager() {
        return guiManager;
    }

    public static MapManager getMapManager() {
        return mapManager;
    }

    public static ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    public static RecentManager getRecentManager() {
        return recentManager;
    }

    public static ClipboardManager getClipboardManager() {
        return clipboardManager;
    }

    public static ImportManager getImportManager() {
        return importManager;
    }

    public static DialogManager getDialogManager() {
        return dialogManager;
    }

    public static LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Return true if the main manager is initialized
     *
     * @return
     */
    public static boolean isInitialized() {
        return initialized;
    }

    public static void enableBackgroundWorker(boolean b) {

    }
}
