package org.abcmap.gui.ie.project;

import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

import java.io.File;

public class OpenProject extends InteractionElement {

    public OpenProject() {
        this.label = "Ouvrir un projet";
        this.help = "...";
        this.accelerator = MainManager.getShortcutManager().OPEN_PROJECT;
        this.menuIcon = GuiIcons.SMALLICON_OPENPROJECT;
    }

    @Override
    public void run() {

		/*
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
		*/
    }

    public void openProject(File file) {
		/*
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
		*/
    }

}
