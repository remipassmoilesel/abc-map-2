package org.abcmap.gui.ie.project;

import org.abcmap.gui.ie.InteractionElement;

import java.io.File;

public class RecoverProjects extends InteractionElement {

    private boolean alertIfNoProjectsFound;
    private String action;
    private File dirWhereSave;

    public RecoverProjects() {
        this.label = "Récupérer un/des projets";
        this.help = "Cliquez ici pour récupérer un ou des projets dans les fichiers temporaires du programme.";

        this.alertIfNoProjectsFound = true;
    }

    @Override
    public void run() {

		/*

		// pas d'appels dans l'EDT
		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// verifier si le dossier temporaire présent
		File tmp = ConfigurationConstants.TEMP_PGRM_DIRECTORY;
		if (tmp.isDirectory() == false) {
			guim.showErrorInDialog(guim.getMainWindow(),
					"Le dossier des fichiers temporaires est invalide.", false);
			threadAccess.releaseAccess();
			return;
		}

		// liste des projets à récupérer
		ArrayList<File> toRecover = new ArrayList<File>();

		// itérer tous les fichiers du dossier temporaire
		for (File f : tmp.listFiles()) {

			// continuer si non dossier ou dossier courant
			if (f.isDirectory() == false)
				continue;

			// si repertoire du projet en cours, ne pas inclure
			if (projectm.isInitialized()) {
				if (Utils.safeEquals(f.getAbsolutePath(), projectm
						.getTempDirectoryFile().getAbsolutePath())){
					continue;
				}
			}

			// verifier si le descripteur existe
			File dsc = new File(f.getAbsolutePath() + File.separator
					+ ConfigurationConstants.DESCRIPTOR_NAME);

			// ajout dans la liste a recuperer
			if (dsc.exists() && dsc.isFile())
				toRecover.add(f);

		}

		// la liste est vide, alerter si demandé
		if (toRecover.size() <= 0) {
			if (alertIfNoProjectsFound) {
				guim.showMessageInBox("Aucun projet à récupérer.");
			}
		}

		// des projets sont disponibles à la récupération
		else {

			// l'action que l'utilisateur decidera de mener
			this.action = null;
			// le repertoire ou seront recupere les projets
			this.dirWhereSave = null;

			final Window parent = guim.getMainWindow();

			// demande d'actions a effectuer
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						RecoverProjectsDialog rpd = new RecoverProjectsDialog(
								parent);
						rpd.setVisible(true);

						action = rpd.getUserAction();
						dirWhereSave = rpd.getDirectory();
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				Log.error(e);
			}

			// l'utilisateur ne veut pas recuperer les projets
			if (RecoverProjectsDialog.CONTINUE.equals(action)) {
				threadAccess.releaseAccess();
				return;
			}

			int exception = 0;

			// l'utilisateur veut recuperer les projets
			if (RecoverProjectsDialog.RECOVERY.equals(action)) {

				int i = 1;
				for (File f : toRecover) {

					// nom du projet récupéré
					File newPath = new File(dirWhereSave + File.separator
							+ "Project " + i + "."
							+ ConfigurationConstants.PROJECT_EXTENSION);

					// zipper le projet
					ZipDirectory zd = new ZipDirectory();
					try {
						zd.zipDirectory(f.getAbsoluteFile(), newPath, true);
					} catch (IOException e) {
						exception++;
						Log.error(e);
					}
					i++;
				}

			}

			// suppression des projets
			if (RecoverProjectsDialog.DELETE.equals(action)
					|| RecoverProjectsDialog.RECOVERY.equals(action)) {

				for (File f : toRecover) {
					try {
						Utils.deleteRecursively(f);
					} catch (IOException e) {
						Log.error(e);
						exception++;
					}
				}
			}

			// montrer un message d'erreur au besoin
			if (exception > 0) {
				SimpleErrorDialog.showLater(guim.getMainWindow(), exception
						+ " erreurs de récupération.");
			}
		}

		threadAccess.releaseAccess();

		*/
    }

    public void alertIfNoProjectsFound(boolean alertIfNoProjectsFound) {
        this.alertIfNoProjectsFound = alertIfNoProjectsFound;
    }

}
