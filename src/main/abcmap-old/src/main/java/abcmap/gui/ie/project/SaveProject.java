package abcmap.gui.ie.project;

import java.io.IOException;

import abcmap.exceptions.ProjectException;
import abcmap.gui.GuiIcons;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class SaveProject extends InteractionElement {

	public SaveProject() {
		this.label = "Enregistrer";
		this.help = "Cliquez ici pour enregistrer le projet";
		this.menuIcon = GuiIcons.SMALLICON_SAVE;
		this.accelerator = shortcuts.SAVE_PROJECT;
	}

	@Override
	public void run() {

		// a utiliser en dehors de l'EDT
		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// message de confirmation
		guim.showMessageInBox("Le projet est en cours d'enregistrement.");

		// nettoyer le projet
		// projectc.cleanCurrentProject();

		// enregistrement
		try {
			projectm.save();
		}

		// le projet n'a encore jamais ete enregistre
		catch (ProjectException e) {
			// proposition de sauvegarder sous
			SaveAsProject sap = new SaveAsProject();
			sap.run();
		}

		// Erreur lors de l'enregistrement du projet
		catch (IOException e) {
			guim.showProjectWritingError();
			Log.error(e);
		}

		threadAccess.releaseAccess();
	}

}
