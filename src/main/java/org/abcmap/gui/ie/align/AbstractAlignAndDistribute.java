package org.abcmap.gui.ie.align;

import org.abcmap.core.draw.AlignConstants;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;

public abstract class AbstractAlignAndDistribute extends InteractionElement {

    private AlignConstants action;

    public AbstractAlignAndDistribute(AlignConstants action) {

        this.action = action;

        if (AlignConstants.ALIGN_TOP.equals(action)) {
            label = "Aligner vers le haut";
            help = "Cliquez ici pour aligner les objets sélectionnés vers le haut.";
            menuIcon = GuiIcons.ALIGN_TOP;
        } else if (AlignConstants.ALIGN_RIGHT.equals(action)) {
            label = "Aligner vers la droite";
            help = "Cliquez ici pour aligner les objets sélectionnés vers la droite.";
            menuIcon = GuiIcons.ALIGN_RIGHT;
        } else if (AlignConstants.ALIGN_BOTTOM.equals(action)) {
            label = "Aligner vers le bas";
            help = "Cliquez ici pour aligner les objets sélectionnés vers le bas.";
            menuIcon = GuiIcons.ALIGN_BOTTOM;
        } else if (AlignConstants.ALIGN_LEFT.equals(action)) {
            label = "Aligner vers la gauche";
            help = "Cliquez ici pour aligner les objets sélectionnés vers la gauche.";
            menuIcon = GuiIcons.ALIGN_LEFT;
        } else if (AlignConstants.ALIGN_MIDDLE_HORIZONTAL.equals(action)) {
            label = "Aligner sur un axe horizontal";
            help = "Cliquez ici pour centrer les objets sélectionnés sur un axe horizontal.";
            menuIcon = GuiIcons.ALIGN_MIDDLE_HORIZONTAL;
        } else if (AlignConstants.ALIGN_MIDDLE_VERTICAL.equals(action)) {
            label = "Aligner sur un axe vertical";
            help = "Cliquez ici pour centrer les objets sélectionnés sur un axe vertical.";
            menuIcon = GuiIcons.ALIGN_MIDDLE_VERTICAL;
        } else if (AlignConstants.DISTRIBUTE_HORIZONTAL.equals(action)) {
            label = "Distribuer horizontalement";
            help = "Cliquez ici pour distribuer horizontalement les objets sélectionnés.";
            menuIcon = GuiIcons.DISTRIBUTE_HORIZONTAL;
        } else if (AlignConstants.DISTRIBUTE_VERTICAL.equals(action)) {
            label = "Distribuer verticalement";
            help = "Cliquez ici pour distribuer verticalement les objets sélectionnés.";
            menuIcon = GuiIcons.DISTRIBUTE_VERTICAL;
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// projet non initialisé: arret
		if (checkProjectAndGetActiveLayer() == null) {
			return;
		}

		// récupérer les elements selectionnes
		ArrayList<LayerElement> elements = drawm.getSelectedElements();

		// sauvegarder pour annulation
		for (LayerElement elmt : elements) {
			elmt.getMementoManager().saveStateToRestore();
		}

		// modifier les elements
		ElementAligner ea = new ElementAligner(action);
		ea.setElements(elements);
		ea.run();

		// sauvegarder pour annulation
		for (LayerElement elmt : elements) {
			elmt.getMementoManager().saveStateToRedo();
		}

		try {
			cancelm.addDrawOperation(projectm.getActiveLayer(), elements);
		} catch (MapLayerException e) {
			Log.error(e);
		}

		// rafraichir et avertir
		projectm.fireElementsChanged();
		mapm.refreshMapComponent();
		*/

    }

}
