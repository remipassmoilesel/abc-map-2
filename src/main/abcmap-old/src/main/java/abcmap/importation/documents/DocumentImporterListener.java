package abcmap.importation.documents;

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
public class DocumentImporterListener extends ImporterAdapter {

	private String taskLabel;
	private GuiManager guim;
	private ProgressbarManager pbm;
	private ProgressbarTask task;
	private ImportManager importm;

	public DocumentImporterListener() {
		guim = MainManager.getGuiManager();
		importm = MainManager.getImportManager();

		taskLabel = "Import de document en cours";
		pbm = guim.getStatusProgressBar();
	}

	private void initializeProgressbar(ImportEvent event) {
		if (task == null) {
			task = pbm.addTask(taskLabel, true, 0, 1, 0);
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
	public void importAborted(ImportEvent event) {
		stopProgressbar();
		importm.fireEvent(ImportManagerEvent.DOCUMENT_IMPORT_ABORTED);
	}

	@Override
	public void importFinished(ImportEvent event) {
		stopProgressbar();
		importm.fireEvent(ImportManagerEvent.DOCUMENT_IMPORT_FINISHED);
	}

	@Override
	public void fatalExceptionHappened(ImportEvent ev) {
		stopProgressbar();
		guim.showErrorInBox("L'import de document a échoué.");
		importm.fireEvent(ImportManagerEvent.DOCUMENT_IMPORT_ABORTED);
	}

	@Override
	public void exceptionHappened(ImportEvent ev) {
	}

}
