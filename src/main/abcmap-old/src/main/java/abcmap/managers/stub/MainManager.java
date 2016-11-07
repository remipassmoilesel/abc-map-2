package abcmap.managers.stub;

import java.io.IOException;

import abcmap.managers.BackgroundTasksManager;
import abcmap.managers.CancelManager;
import abcmap.managers.ClipboardManager;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.RecentManager;
import abcmap.managers.ShortcutManager;

/**
 * Accés aux différents controlleurs
 * 
 * @author remipassmoilesel
 *
 */
public class MainManager {

	private static boolean devMode = false;
	private static ProjectManager projectManager;
	private static DrawManager drawManager;
	private static GuiManager guiManager;
	private static MapManager mapManager;
	private static ConfigurationManager configManager;
	private static ShortcutManager shortcutManager;
	private static CancelManager cancelManager;
	private static RecentManager recentManager;
	private static BackgroundTasksManager worker;
	private static ImportManager importManager;
	private static ClipboardManager clipboardManager;

	public static void init() throws IOException {

		projectManager = new ProjectManager();
		guiManager = new GuiManager();
		drawManager = new DrawManager();
		recentManager = new RecentManager();
		mapManager = new MapManager();
		configManager = new ConfigurationManager();
		shortcutManager = new ShortcutManager();
		cancelManager = new CancelManager();
		clipboardManager = new ClipboardManager();
		importManager = new ImportManager();

	}

	public static boolean isDebugMode() {
		return MainManager.devMode;
	}

	public static void setDeveloppementMode(boolean devMode) {
		MainManager.devMode = devMode;
	}

	public static ProjectManager getProjectManager() {
		return projectManager;
	}

	public static ClipboardManager getClipboardManager() {
		return clipboardManager;
	}

	public static GuiManager getGuiManager() {
		return guiManager;
	}

	public static DrawManager getDrawManager() {
		return drawManager;
	}

	public static MapManager getMapManager() {
		return mapManager;
	}

	public static ConfigurationManager getConfigurationManager() {
		return configManager;
	}

	public static ShortcutManager getShortcutManager() {
		return shortcutManager;
	}

	public static CancelManager getCancelManager() {
		return cancelManager;
	}

	public static RecentManager getRecentManager() {
		return recentManager;
	}

	public static ImportManager getImportManager() {
		return importManager;
	}

	public static void enableBackgroundWorker(boolean state) {

		if (state) {
			if (worker != null) {
				enableBackgroundWorker(false);
			}
			worker = new BackgroundTasksManager();
			worker.setEnabled(true);
		}

		else {
			if (worker == null) {
				return;
			}
			worker.setEnabled(false);
			worker = null;
		}

	}

}
