package abcmap.importation.directory;

import abcmap.events.ImportManagerEvent;
import abcmap.events.ImportEvent;
import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarTask;
import abcmap.importation.ImporterAdapter;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;

/**
 * Mettre à jour la barre de progression de la barre de statut
 * 
 * @author remipassmoilesel
 *
 */
public class DirectoryImporterListener extends ImporterAdapter {

	private String taskLabel;
	private GuiManager guim;
	private ProgressbarManager pbm;
	private ProgressbarTask task;
	private ImportManager importm;

	public DirectoryImporterListener() {
		guim = MainManager.getGuiManager();
		importm = MainManager.getImportManager();

		taskLabel = "Import de répertoire en cours";
		pbm = guim.getStatusProgressBar();
	}

	private void initializeProgressbar(ImportEvent event) {
		if (task == null) {
			task = pbm.addTask(taskLabel, false, 0, event.getTotalToImport(), event.getImported());
		}
	}

	private void updateProgressbar(ImportEvent event) {
		if (task != null) {
			task.setMaxValue(event.getTotalToImport());
			task.setCurrentValue(event.getImported());
			pbm.updateProgressbarLater(task);
		}
	}

	private void stopProgressbar() {
		if (task != null) {
			pbm.removeTask(task);
			task = null;
		}
	}

	@Override
	public void importStarted(ImportEvent event) {
		initializeProgressbar(event);
		// pas d'evenement de départ, c'est le gestionnaire d'import qui
		// l'envoie
	}

	@Override
	public void waitingListChanged(ImportEvent event) {
		updateProgressbar(event);
	}

	@Override
	public void importAborted(ImportEvent event) {
		stopProgressbar();
		importm.fireEvent(ImportManagerEvent.DIRECTORY_IMPORT_ABORTED);
	}

	@Override
	public void importFinished(ImportEvent event) {
		stopProgressbar();
		importm.fireEvent(ImportManagerEvent.DIRECTORY_IMPORT_FINISHED);
	}

	@Override
	public void fatalExceptionHappened(ImportEvent ev) {
		stopProgressbar();
		importm.fireEvent(ImportManagerEvent.DOCUMENT_IMPORT_ABORTED);
		guim.showErrorInBox("L'import de répertoire s'est interrompu.");
	}

	@Override
	public void exceptionHappened(ImportEvent event) {

	}

}
