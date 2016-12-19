package org.abcmap.ielements.position;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;

public abstract class AbstractChangeZOrder extends InteractionElement {

    public enum Direction {
        UP, DOWN, TOP, BOTTOM
    }

    private Direction mode;

    public AbstractChangeZOrder(Direction dir) {

        mode = dir;

        if (Direction.UP.equals(dir)) {
            label = "Monter";
            help = "Monter";
            menuIcon = GuiIcons.SMALLICON_UP;
        } else if (Direction.DOWN.equals(dir)) {
            label = "Descendre";
            help = "Descendre";
            menuIcon = GuiIcons.SMALLICON_DOWN;
        } else if (Direction.TOP.equals(dir)) {
            label = "Tout au dessus";
            help = "Tout au dessus";
            menuIcon = GuiIcons.SMALLICON_TOP;
        } else if (Direction.BOTTOM.equals(dir)) {
            label = "Tout en bas";
            help = "Tout en bas";
            menuIcon = GuiIcons.SMALLICON_BOTTOM;
        }

    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// un seul thread à la fois
		if (ThreadAccessControl.get(1).askAccess() == false) {
			return;
		}

		// Verifier le projet et obtenir le calque actif, ou afficher un
		// message d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// sauvegarde de l'etat du calque
		layer.getMementoManager().saveStateToRestore();

		// chercher les tuiles à déplacer
		ListenableContainer<LayerElement> tiles = layer.getTiles();
		moving: for (LayerElement elmt : tiles) {

			// prendre en compte seulement les elmts selectionnes
			if (elmt.isSelected() == false)
				continue moving;

			// tuile a deplacer
			Tile t = (Tile) elmt;

			// calcul du nouvel index
			int newIndex = tiles.indexOf(t);
			if (Direction.UP.equals(mode)) {
				newIndex++;
			} else if (Direction.DOWN.equals(mode)) {
				newIndex--;
			} else if (Direction.TOP.equals(mode)) {
				newIndex = tiles.size() - 1;
			} else if (Direction.BOTTOM.equals(mode)) {
				newIndex = 0;
			}

			// controles
			if (newIndex < 0)
				newIndex = 0;
			if (newIndex > tiles.size() - 1)
				newIndex = tiles.size() - 1;

			// deplacement
			tiles.moveElement(t, newIndex);

		}

		// cas des autres objets
		ListenableContainer<LayerElement> shapes = layer.getDrawShapes();
		moving: for (LayerElement elmt : shapes.getCopy()) {

			// utiliser seulement les objets selectionnes
			if (elmt.isSelected() == false)
				continue moving;

			// calcul du nouvel index
			int newIndex = shapes.indexOf(elmt);
			if (Direction.UP.equals(mode)) {
				newIndex++;
			} else if (Direction.DOWN.equals(mode)) {
				newIndex--;
			} else if (Direction.TOP.equals(mode)) {
				newIndex = shapes.size() - 1;
			} else if (Direction.BOTTOM.equals(mode)) {
				newIndex = 0;
			}

			// controles
			if (newIndex < 0)
				newIndex = 0;
			if (newIndex > shapes.size() - 1)
				newIndex = shapes.size() - 1;

			// deplacement
			shapes.moveElement(elmt, newIndex);

		}

		// enregistrement de l'operation pour annulation
		layer.getMementoManager().saveStateToRedo();
		cancelm.addMapLayerOperation(layer);

		mapm.refreshMapComponent();

		// un seul thread à la fois
		ThreadAccessControl.get(1).releaseAccess();

		*/
    }

}
