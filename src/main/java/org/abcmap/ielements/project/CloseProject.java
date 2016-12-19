package org.abcmap.ielements.project;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;

public class CloseProject extends InteractionElement {

    public CloseProject() {
        this.label = "Fermer le projet";
        this.help = "...";
        this.menuIcon = GuiIcons.SMALLICON_CLOSEPROJECT;
    }

    @Override
    public void run() {

		/*
        GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// confirmation d'enregistrement de projet uniquement hors debug mode
		if (MainManager.isDebugMode() == false && projectm.isInitialized()) {

			QuestionResult cc = ClosingConfirmationDialog
					.showProjectConfirmationAndWait(guim.getMainWindow());

			// cas l'utilisateur annule, retour
			if (cc.isAnswerCancel()) {
				threadAccess.releaseAccess();
				return;
			}

			// l'utilisateur souhaite sauvegarder
			else if (cc.isAnswerYes()) {
				SaveProject saver = new SaveProject();
				saver.run();
			}

		}

		// fermer le programme
		closeProject();

		threadAccess.releaseAccess();
		*/
    }

    public void closeProject() {
		/*
		// fermeture du projet
		try {
			projectm.closeProject();
		}

		catch (IOException e) {
			Log.error(e);
		}
		*/
    }

}
