package org.abcmap.gui.ie.analyse;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public class AnalyseSelectedTiles extends InteractionElement {

    public AnalyseSelectedTiles() {

        this.label = "Analyser les tuiles sélectionnées";
        this.help = "Cliquez ici pour analyser les tuiles sélectionnée et les repositionner sur la carte"
                + "en fonction de leurs caractéristiques.";
        this.menuIcon = GuiIcons.SMALLICON_REANALYSE;
    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// Verifier le projet et obtenir le calque actif, ou afficher un message
		// d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// eviter les appels intempesrifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// liste de tuile à analyser une deuxieme fois
		// les tuiles sont analysées une premièrere fois puis si aucun point
		// commun
		// une deuxieme fois a la fin des ajouts
		ArrayList<Tile> toAddAgain = new ArrayList<Tile>();

		// liste de tuiles à traiter
		ArrayList<LayerElement> toSave = new ArrayList<LayerElement>();

		// retirer les tuiles concernees
		for (LayerElement elmt : layer.getTilesReversed()) {

			// seulement les tuiles sélectionnées
			if (elmt.isSelected()) {

				// sauvegarde des parametres pour annulation
				Tile t = (Tile) elmt;
				t.getMementoManager().saveStateToRestore();

				// retirer la tuile
				layer.removeElement(t);

				toAddAgain.add(t);
				toSave.add(elmt);
			}
		}

		// ajout des tuiles avec analyse
		int i = 0;
		for (Tile t : new ArrayList<Tile>(toAddAgain)) {

			// Boolean successfulAnalysis = layer.addTile(t);
			Boolean successfulAnalysis = true;

			if (successfulAnalysis) {

				// reussite de l'ajout, pas besoin de deuxieme analyse
				toAddAgain.remove(t);

				// enregistrement pour annulation
				cancelm.addDrawOperation(layer, t).elementsHaveBeenAdded(true);
			}

			i++;
		}

		// deuxieme tentative d'ajout
		if (toAddAgain.size() > 0) {

			Collections.reverse(toAddAgain);
			for (Tile t : toAddAgain) {
				// if (layer.addTile(t)) {
				if (true) {
					// enregistrement pour annulation
					cancelm.addDrawOperation(layer, t).elementsHaveBeenAdded(
							true);
				}
			}

		}

		// enregistrement de l'operation pour annulation
		for (LayerElement e : toSave) {
			e.getMementoManager().saveStateToRedo();
		}

		cancelm.addDrawOperation(layer, toSave);

		// fin
		threadAccess.releaseAccess();

		*/
    }

}
