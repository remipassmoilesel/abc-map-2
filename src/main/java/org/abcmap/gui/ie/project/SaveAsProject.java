package org.abcmap.gui.ie.project;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public class SaveAsProject extends InteractionElement {

    public SaveAsProject() {
        this.label = "Enregistrer sous ...";
        this.help = "Cliquez ici pour enregistrer le projet sous...";
        this.menuIcon = GuiIcons.SMALLICON_SAVEAS;
        this.accelerator = shortcuts.SAVE_PROJECT_AS;
    }

    @Override
    public void run() {

		/*

		// appeler hor de l'EDT
		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// nettoyer le projet
		// projectc.cleanCurrentProject();

		// afficher une boite parcourir
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog
				.browseProjectToOpen(parent);

		// en cas d'annulation
		if (result.isActionCanceled() == true) {
			threadAccess.releaseAccess();
			return;
		}

		// fentre d'attente
		// dialogManager.launch(this);

		// enregistrement du fichier
		try {
			AbmProjectWriter pw = new AbmProjectWriter();
			pw.setOverwriting(true);

			projectm.setRealPath(result.getFile());

			projectm.save(pw);

			// dialogManager.stopAtNextLoop(this);
		}

		catch (IOException e) {

			// message d'erreur
			guim.showProjectWritingError();
			Log.error(e);

			threadAccess.releaseAccess();

			// dialogManager.stopAtNextLoop(this);
			return;
		}

		// enregistrement dans les projets récents
		try {
			recentsm.addProject(projectm.getRealPath());
			recentsm.saveHistory();
		} catch (IOException e) {
			Log.error(e);
		}

		threadAccess.releaseAccess();

		*/

    }

}
