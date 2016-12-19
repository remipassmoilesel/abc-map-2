package org.abcmap.ielements.profiles;

import org.abcmap.ielements.InteractionElement;

public class SaveAsProfile extends InteractionElement {

    public SaveAsProfile() {
        label = "Enregistrez sous le profil de configuration";
        help = "...";
    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// Boite parcourir de sauvegarde
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog
				.browseProfileToSaveAndWait(parent);

		// l'action a ete annulée
		if (result.isActionCanceled()) {
			threadAccess.releaseAccess();
			return;
		}

		try {

			File file = result.getFile();

			// enregistrement du profil
			configm.saveProfile(file.getAbsolutePath(), true);

			// conservation dans les rcents
			recentsm.addProfile(file);
			recentsm.saveHistory();

			guim.showMessageInBox("Le profil a été enregistré");

		} catch (IOException e) {
			guim.showProfileWritingError();
			Log.error(e);
		}

		threadAccess.releaseAccess();

		*/

    }
}
