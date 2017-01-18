package org.abcmap.core.managers;

/**
 * Utility to extend when creating a manager (alias service)
 * <p>
 * /!\ Warning: be careful if you use this methods in constructors: if you use a manager before it is created, say hello to nullpointerex !
 */
public class ManagerTreeAccessUtil {

    /**
     * Shortcut for getConfigurationManager()
     *
     * @return
     */
    public static ConfigurationManager configm() {
        return Main.getConfigurationManager();
    }

    /**
     * Shortcut for getTempFilesManager()
     *
     * @return
     */
    public static TempFilesManager tempm() {
        return Main.getTempFilesManager();
    }

    /**
     * Shortcut for getProjectManager()
     *
     * @return
     */
    public static ProjectManager projectm() {
        return Main.getProjectManager();
    }


    /**
     * Shortcut for getDrawManager()
     *
     * @return
     */
    public static DrawManager drawm() {
        return Main.getDrawManager();
    }

    /**
     * Shortcut for getCancelManager()
     *
     * @return
     */
    public static UndoManager undom() {
        return Main.getUndoManager();
    }

    /**
     * Shortcut for getGuiManager()
     *
     * @return
     */
    public static GuiManager guim() {
        return Main.getGuiManager();
    }

    /**
     * Shortcut for getMapManager()
     *
     * @return
     */
    public static MapManager mapm() {
        return Main.getMapManager();
    }

    /**
     * Shortcut for getShortcutManager()
     *
     * @return
     */
    public static KeyboardManager shortcutm() {
        return Main.getKeyboardManager();
    }

    /**
     * Shortcut for getRecentManager()
     *
     * @return
     */
    public static RecentManager recentm() {
        return Main.getRecentManager();
    }

    /**
     * Shortcut for getClipboardManager()
     *
     * @return
     */
    public static ClipboardManager clipbm() {
        return Main.getClipboardManager();
    }

    /**
     * Shorcut for getImportManager()
     *
     * @return
     */
    public static ImportManager importm() {
        return Main.getImportManager();
    }

    /**
     * Shorcut for getDialogManager()
     *
     * @return
     */
    public static DialogManager dialm() {
        return Main.getDialogManager();
    }

    /**
     * Shortcut for getLayoutManager()
     *
     * @return
     */
    public static LayoutManager layoutm() {
        return Main.getLayoutManager();
    }


}
