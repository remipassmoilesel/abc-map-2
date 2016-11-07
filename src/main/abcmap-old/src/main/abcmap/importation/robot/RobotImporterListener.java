package abcmap.importation.robot;

import abcmap.events.ImportManagerEvent;

import java.awt.Dimension;

import abcmap.events.ImportEvent;
import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarTask;
import abcmap.importation.ImporterAdapter;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;

/**
 * Mettre à jour la barre de progression de la barre de statut
 * 
 * @author remipassmoilesel
 *
 */
public class RobotImporterListener extends ImporterAdapter {

	/** Le nom de la tache de barre de progression */
	private String taskLabel;

	/** Barre de progression de la fenetre principale */
	private ProgressbarManager mainWindowPbm;

	/** Barre de progression de la fenetre d'import automatique */
	private ProgressbarManager robotWindowPbm;

	private GuiManager guim;
	private ImportManager importm;
	private ConfigurationManager configm;

	private ProgressbarTask task1;
	private ProgressbarTask task2;

	public RobotImporterListener() {
		guim = MainManager.getGuiManager();
		importm = MainManager.getImportManager();
		configm = MainManager.getConfigurationManager();

		taskLabel = "Import automatique en cours";

		mainWindowPbm = guim.getStatusProgressBar();
		robotWindowPbm = guim.getRobotWindowProgressBar();
	}

	private void initializeProgressbars(ImportEvent event) {

		Dimension area = configm.getRobotImportCaptureArea();
		int max = area.width * area.height;

		if (task1 == null) {
			task1 = mainWindowPbm.addTask(taskLabel, false, 0, max, 0);
		}

		if (task2 == null) {
			task2 = robotWindowPbm.addTask(taskLabel, false, 0, max, 0);
		}

	}

	private void updateProgressbars(ImportEvent event) {

		if (task1 != null) {
			task1.setCurrentValue(event.getScreenCatchNumber());
			mainWindowPbm.updateProgressbarLater(task1);
		}

		if (task2 != null) {
			task2.setCurrentValue(event.getScreenCatchNumber());
			robotWindowPbm.updateProgressbarLater(task2);
		}
	}

	private void stopProgressbars() {

		if (task1 != null) {
			mainWindowPbm.removeTask(task1);
			task1 = null;
		}

		if (task2 != null) {
			robotWindowPbm.removeTask(task2);
			task2 = null;
		}
	}

	@Override
	public void importStarted(ImportEvent event) {
		initializeProgressbars(event);
		// pas d'evenement de départ, c'est le gestionnaire d'import qui
		// l'envoie
	}

	@Override
	public void waitingListChanged(ImportEvent event) {

		// ne pas réagir aux evenements en provenance de la fabrique de tuile
		if (event.getScreenCatchNumber() != 0) {
			updateProgressbars(event);
		}

	}

	@Override
	public void importAborted(ImportEvent event) {
		stopProgressbars();
		importm.fireEvent(ImportManagerEvent.ROBOT_IMPORT_ABORTED);
	}

	@Override
	public void importFinished(ImportEvent event) {
		stopProgressbars();
		importm.fireEvent(ImportManagerEvent.ROBOT_IMPORT_FINISHED);
	}

	@Override
	public void fatalExceptionHappened(ImportEvent ev) {
		stopProgressbars();
		// guim.showErrorInBox("L'import de document a échoué.");
		guim.showErrorInDialog(guim.getMainWindow(), "Erreur lors de l'importation automatique.",
				false);
		importm.fireEvent(ImportManagerEvent.ROBOT_IMPORT_ABORTED);
	}

	@Override
	public void exceptionHappened(ImportEvent ev) {
	}

}
