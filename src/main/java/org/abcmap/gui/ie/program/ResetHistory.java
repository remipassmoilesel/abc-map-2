package org.abcmap.gui.ie.program;

import org.abcmap.gui.ie.InteractionElement;

public class ResetHistory extends InteractionElement {

    public ResetHistory() {
        label = "Effacer l'historique";
        help = "Cliquez ici pour effacer l'historique des projets et des profils de configuration utilisés.";
    }

    @Override
    public void run() {

		/*
        // effacement de l'historique
		recentsm.clearHistory();

		// sauvegarde d'un nouveau fichier
		try {
			recentsm.saveHistory();
			guim.showMessageInBox("Historique effacé avec succés.");
		}

		catch (IOException e) {
			guim.showErrorInBox("Erreur lors de l'effacement de l'historique.");
			Log.error(e);
		}
		*/

    }

}
