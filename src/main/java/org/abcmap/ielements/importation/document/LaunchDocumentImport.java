package org.abcmap.ielements.importation.document;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class LaunchDocumentImport extends InteractionElement {

    private static final String START = "START";
    private static final String STOP = "STOP";
    private JButton btnLaunch;
    private JButton btnStop;

    public LaunchDocumentImport() {

        label = "Lancer l'import de document";
        help = "Cliquez ici pour lancer l'import de document.";

        displaySimplyInSearch = false;

    }

    @Override
    protected Component createPrimaryGUI() {

        // panneau support
        JPanel panel = new JPanel(new MigLayout("insets 5"));

        // bouton de lancement
        btnLaunch = new JButton("Lancer l'import");
        btnLaunch.addActionListener(this);
        btnLaunch.setActionCommand(START);
        panel.add(btnLaunch);

        // bouton d'arret
        btnStop = new JButton("Arrêter l'import");
        btnStop.addActionListener(this);
        btnStop.setActionCommand(STOP);
        panel.add(btnStop);

        return panel;
    }

    @Override
    public void run() {

        if (getLastActionCommand() == null) {
            return;
        }

		/*
        // lancer l'import
		if (Utils.safeEquals(START, getLastActionCommand())) {

			try {
				importm.startDocumentImport();

				guim.showMessageInBox("Début de l'import de document.");
			}

			// erreur lors du lancement
			catch (MapImportException e) {

				Log.error(e);

				if (Utils.safeEquals(MapImportException.INVALID_FILE,
						e.getMessage())) {
					guim.showErrorInBox("Document à imporer invalide.");
				}

				else if (Utils.safeEquals(
						MapImportException.NO_RENDERER_AVAILABLE,
						e.getMessage())) {
					guim.showErrorInBox("Ce type de document n'est pas pris en charge.");
				}

				else if (Utils.safeEquals(MapImportException.ALREADY_IMPORTING,
						e.getMessage())) {
					guim.showErrorInBox("Un autre import est déjà en cours.");
				}

				else {
					guim.showErrorInBox("Erreur lors de l'importation du document.");
				}
			}
		}

		// arreter l'import
		else {
			importm.stopDocumentImportLater();
			guim.showMessageInBox("L'import sera bientôt stoppé.");
		}

		*/

    }

    /**
     * Ne pas désactiver le bouton d'aret d'import. En cas d'erreur,
     * l'utilisateur pourra recreer un objet d'import.
     */
    @Deprecated
    private void chesckBoutonsEnabled() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
//                Boolean val = importm.isDocumentImporting();
//                btnLaunch.setEnabled(!val);
//                btnStop.setEnabled(val);
            }
        });

    }

}
