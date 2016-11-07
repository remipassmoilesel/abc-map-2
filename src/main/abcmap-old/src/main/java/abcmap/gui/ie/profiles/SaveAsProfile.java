package abcmap.gui.ie.profiles;

import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;

public class SaveAsProfile extends InteractionElement {

	public SaveAsProfile() {
		label = Lng.get("save as configuration profile");
		help = Lng.get("save as configuration profile help");
	}

	@Override
	public void run() {

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

	}
}
