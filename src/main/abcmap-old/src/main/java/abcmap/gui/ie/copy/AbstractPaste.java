package abcmap.gui.ie.copy;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Tile;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.gui.GuiIcons;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;

public abstract class AbstractPaste extends InteractionElement {

	public enum Mode {
		PASTE_AS_TILE, PASTE_SIMPLE
	}

	private Mode mode;

	public AbstractPaste(Mode mode) {

		this.mode = mode;

		if (Mode.PASTE_AS_TILE.equals(mode)) {
			label = "Coller comme tuile";
			help = "Coller comme tuile";
			menuIcon = GuiIcons.SMALLICON_PASTEASTILE;
			accelerator = shortcuts.PASTE_AS_TILE;
		}

		else {
			label = "Coller";
			help = "Coller";
			menuIcon = GuiIcons.SMALLICON_PASTE;
			accelerator = shortcuts.PASTE;
		}

	}

	@Override
	public void run() {

		// pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// Verifier le projet et obtenir le calque actif, ou afficher un message
		// d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// demander le focus
		mapm.requestFocusOnMap();

		// liste des elements à coller
		ArrayList<LayerElement> toPaste = new ArrayList<LayerElement>();

		// récuperation des elements du presse papier
		// En tant que tuile
		if (Mode.PASTE_AS_TILE.equals(mode)) {

			try {
				Tile t = clipboardm.getClipboardContentAsTile();
				if (t != null)
					toPaste.add(t);
			} catch (IOException e) {
				guim.showErrorInBox("Erreur lors du collage des tuiles.");
				return;
			}
		}

		// En tant qu'elements
		else {
			try {
				ArrayList<LayerElement> elmts = clipboardm
						.getElementsFromClipboard();
				if (elmts != null)
					toPaste.addAll(elmts);
			} catch (IOException e) {
				guim.showErrorInBox("Erreur lors du collage des éléments.");
				return;
			}
		}

		// coller les elements
		if (toPaste.size() > 0) {

			// deselection de tous les elements
			projectm.setAllElementsSelected(false);

			// enlever les elements null
			for (LayerElement e : new ArrayList<LayerElement>(toPaste))
				if (e == null)
					toPaste.remove(e);

			// enregistrement de l'operation pour annulation
			ElementsCancelOp op = MainManager.getCancelManager().addDrawOperation(
					layer, toPaste);
			op.elementsHaveBeenAdded(true);

			// ajout des elements au calque actif
			for (LayerElement e : toPaste) {

				// selectionner l'element
				e.setSelected(true);

				// TODO: mettre au milieu de la carte
				e.setPosition(new Point(100, 100));

				// ajout au calque
				layer.addElement(e);

				// rafraichir la forme
				e.refreshShape();
			}

			// notification de changement
			projectm.fireElementsChanged();

			// changement d'outil en fonction du type de collage
			if (Mode.PASTE_AS_TILE.equals(mode)) {
				drawm.setCurrentTool(ToolLibrary.TILE_TOOL);
			} else {
				drawm.setCurrentTool(ToolLibrary.SELECTION_TOOL);
			}
		}

	}
}
