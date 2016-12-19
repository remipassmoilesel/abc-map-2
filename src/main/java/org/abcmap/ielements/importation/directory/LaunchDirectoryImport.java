package org.abcmap.ielements.importation.directory;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class LaunchDirectoryImport extends InteractionElement {

    private static final String START = "START";
    private static final String STOP = "STOP";
    private JButton btnLaunch;
    private JButton btnStop;

    public LaunchDirectoryImport() {

        label = "Lancer l'import de répertoire";
        help = "Cliquez ici pour lancer l'import de répertoire d'images.";

        displaySimplyInSearch = false;

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 5"));

        btnLaunch = new JButton("Lancer l'import");
        btnLaunch.addActionListener(this);
        btnLaunch.setActionCommand(START);
        panel.add(btnLaunch);

        btnStop = new JButton("Arrêter l'import");
        btnStop.addActionListener(this);
        btnStop.setActionCommand(STOP);
        panel.add(btnStop);

        return panel;
    }

    @Override
    public void run() {

		/*
        if (getLastActionCommand() == null) {
			return;
		}

		// lancer l'import
		if (Utils.safeEquals(START, getLastActionCommand())) {

			try {
				importm.startDirectoryImport();

				guim.showMessageInBox("Début de l'import de répertoire.");
			}

			// erreur lors du lancement
			catch (MapImportException e) {

				Log.error(e);

				if (Utils.safeEquals(MapImportException.INVALID_DIRECTORY,
						e.getMessage())) {
					guim.showErrorInBox("Dossier d'import invalide.");
				}

				else if (Utils.safeEquals(
						MapImportException.NO_FILES_TO_IMPORT, e.getMessage())) {
					guim.showErrorInBox("Aucun fichier à importer dans le dossier sélectionné.");
				}

				else if (Utils.safeEquals(MapImportException.ALREADY_IMPORTING,
						e.getMessage())) {
					guim.showErrorInBox("Un autre import est déjà en cours.");
				}

				else {
					guim.showErrorInBox("Erreur lors de l'importation de dossier.");
				}
			}
		}

		// arreter l'import
		else {
			importm.stopDirectoryImportLater();
			guim.showMessageInBox("L'import sera bientôt stoppé.");
		}

		*/
    }

    /**
     * Ne pas désactiver le bouton d'arret d'import. En cas d'erreur,
     * l'utilisateur pourra recreer un objet d'import.
     *
     @Deprecated private void checkBoutonsEnabled() {
     SwingUtilities.invokeLater(new Runnable() {
     @Override public void run() {
     Boolean val = importm.isDirectoryImporting();
     btnLaunch.setEnabled(!val);
     btnStop.setEnabled(val);
     }
     });

     }

     */

}
