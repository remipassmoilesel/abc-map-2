package org.abcmap.gui.ie.profiles;

import org.abcmap.gui.ie.InteractionElement;

public class CreateNewProfile extends InteractionElement {

    public CreateNewProfile() {
        this.label = "Créer un nouveau profil...";
        this.help = "Cliquez ici pour créer un nouveau profil de configuration.";
    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// pas de verification en mode debugage
		if (MainManager.isDebugMode() == false) {

			QuestionResult result = ClosingConfirmationDialog
					.showProfileConfirmationAndWait(guim.getMainWindow());

			// l'action a ete annulée
			if (result.isAnswerCancel())
				return;

			// l'utilisateur souhaite enregistrer
			if (result.isAnswerYes()) {
				new SaveProfile().run();
			}

		}

		// raz du profil courant
		configm.resetConfiguration();

		// raz du chemin du profil
		configm.getConfiguration().PROFILE_PATH = "";

		// sauvegarder sous
		new SaveAsProfile().run();

		*/
    }
}
