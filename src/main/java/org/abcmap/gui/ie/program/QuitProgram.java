package org.abcmap.gui.ie.program;

import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public class QuitProgram extends InteractionElement {

    public QuitProgram() {

        this.label = "Quitter";
        this.help = "Cliquez ici pour quitter le programme.";
        this.menuIcon = GuiIcons.QUIT_PROGRAM;
        this.accelerator = MainManager.getShortcutManager().QUIT_PROGRAM;
    }

    @Override
    public void run() {

		/*
        // eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// verifier si le projet doit être enregistré
		if (MainManager.isDebugMode() == false && projectm.isInitialized()) {

			QuestionResult cc = ClosingConfirmationDialog
					.showProjectConfirmationAndWait(guim.getMainWindow());

			// cas ou l'utilisateur annule
			if (cc.isAnswerCancel()) {
				threadAccess.releaseAccess();
				return;
			}

			else if (cc.isAnswerYes()) {
				SaveProject saver = new SaveProject();
				saver.run();
			}
		}

		// montrer le dialog de support du projet
		if (MainManager.isDebugMode() == false) {
			guim.showSupportDialogAndWait(guim.getMainWindow());
		}

		// masquer les fenêtres
		try {
			MainManager.getGuiManager().setAllWindowVisibles(false);
		} catch (InvocationTargetException | InterruptedException e2) {
			Log.error(e2);
		}

		// femeture du projet
		try {
			projectm.closeProject();
		} catch (IOException e1) {
			Log.error(e1);
		}

		// sauvegarde de l'historique
		try {
			MainManager.getRecentManager().saveHistory();
		} catch (IOException e) {
			Log.error(e);
		}

		// sauvegarde du profil de conf
		if (configm.isSaveProfileWhenQuit()) {
			try {
				configm.saveProfile();
			} catch (IOException e) {
				Log.error(e);
			}
		}

		// arret de l'enregistrement de fond
		MainManager.enableBackgroundWorker(false);

		// attente avant fermeture
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.error(e);
		}

		// quitter
		System.exit(0);

		*/

    }

}
