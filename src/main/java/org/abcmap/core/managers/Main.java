package org.abcmap.core.managers;

import org.abcmap.core.log.CustomLogger;

import java.io.IOException;

/**
 * Main manager.
 * <p>
 * From here, all other managers can be reached.
 */
public class Main extends ManagerTreeAccessUtil {

    private static final CustomLogger logger = LogManager.getLogger(Main.class);

    private static boolean debugMode = false;
    private static boolean initialized = false;

    private static ConfigurationManager configurationManager;
    private static TempFilesManager tempFilesManager;
    private static ProjectManager projectManager;
    private static DrawManager drawManager;
    private static CancelManager cancelManager;
    private static GuiManager guiManager;
    private static MapManager mapManager;
    private static KeyboardManager keyboardManager;
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

        configurationManager = new ConfigurationManager();
        tempFilesManager = new TempFilesManager();
        projectManager = new ProjectManager();
        drawManager = new DrawManager();
        guiManager = new GuiManager();
        cancelManager = new CancelManager();
        mapManager = new MapManager();
        keyboardManager = new KeyboardManager();
        recentManager = new RecentManager();
        clipboardManager = new ClipboardManager();
        importManager = new ImportManager();
        dialogManager = new DialogManager();
        layoutManager = new LayoutManager();

        setInitialized(true);
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

    public static CancelManager getCancelManager() {
        return cancelManager;
    }

    public static GuiManager getGuiManager() {
        return guiManager;
    }

    public static MapManager getMapManager() {
        return mapManager;
    }

    public static KeyboardManager getKeyboardManager() {
        return keyboardManager;
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
     * If set to true, initialization should not be called anymore
     *
     * @param initialized
     */
    private static void setInitialized(boolean initialized) {
        Main.initialized = initialized;
    }

    /**
     * Return true if the main manager is initialized
     *
     * @return
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * If set to true, more information should be displayed
     *
     * @return
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * If set to true, more information should be displayed
     *
     * @return
     */
    public static void setDebugMode(boolean val) {
        debugMode = val;
    }

}
