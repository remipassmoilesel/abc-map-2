package abcmap.gui.ie.project;

import java.awt.Window;
import java.io.File;
import java.io.IOException;

import abcmap.gui.GuiIcons;
import abcmap.gui.dialogs.ClosingConfirmationDialog;
import abcmap.gui.dialogs.QuestionResult;
import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;

public class OpenProject extends InteractionElement {

	public OpenProject() {
		this.label = Lng.get("open project");
		this.help = Lng.get("open project help");
		this.accelerator = MainManager.getShortcutManager().OPEN_PROJECT;
		this.menuIcon = GuiIcons.SMALLICON_OPENPROJECT;
	}

	@Override
	public void run() {

		// pas d'appels sur l'EDT
		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// confirmer la fermeture du projet courant
		if (MainManager.isDebugMode() == false && projectm.isInitialized()) {
			QuestionResult cc = ClosingConfirmationDialog
					.showProjectConfirmationAndWait(guim.getMainWindow());
			if (cc.isAnswerYes() == false) {
				threadAccess.releaseAccess();
				return;
			}
		}

		// boite de dialogue parcourir
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog
				.browseProjectToOpenAndWait(parent);

		// operation annulée par l'utilisateur
		if (result.isActionCanceled() == true) {
			threadAccess.releaseAccess();
			return;
		}

		// ouvrir le projet
		openProject(result.getFile());

		threadAccess.releaseAccess();
	}

	public void openProject(File file) {
		// fermer le projet
		try {
			projectm.closeProject();
		} catch (IOException e1) {
			guim.showErrorInBox("Erreur lors de la fermeture du projet.");
			Log.error(e1);
		}

		// ouverture du fichier choisi
		try {
			projectm.openProject(file);

			guim.showErrorInBox("Le projet est chargé.");

			// conservation dans les recents
			recentsm.addProject(file);
			recentsm.saveHistory();

		}

		// erreur lors de l'ouverture
		catch (Exception e) {
			guim.showErrorInBox("Erreur lors de l'ouverture du projet.");
			Log.error(e);
		}
	}

}
