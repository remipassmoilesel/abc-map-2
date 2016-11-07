package abcmap.gui.ie.profiles;

import java.io.IOException;

import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class SaveProfile extends InteractionElement {

	public SaveProfile() {
		this.label = "Sauvegarder le profil courant";
		this.help = "Cliquez ici pour sauvegarder le profil courant. Si le profil n'a jamais été enregistré,"
				+ " une boite de dialogue vous demandera un emplacement d'enregistrement.";
	}

	@Override
	public void run() {

		// pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// chemin d'enregistrement du profil
		String path = configm.getConfiguration().PROFILE_PATH;

		// le profil n'a jamais ete sauvegarde > commande enregistrer sous
		if (path.equals("")) {
			new SaveAsProfile().run();
			return;
		}

		// le profil a deja ete enregistre
		try {
			configm.saveProfile();

			guim.showMessageInBox("Le profil a été enregistré");
		}

		catch (IOException e) {
			guim.showProfileWritingError();
			Log.error(e);
		}
	}
}
