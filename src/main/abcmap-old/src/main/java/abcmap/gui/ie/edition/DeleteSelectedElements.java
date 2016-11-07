package abcmap.gui.ie.edition;

import java.util.ArrayList;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.gui.GuiIcons;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;

public class DeleteSelectedElements extends InteractionElement {
	public DeleteSelectedElements() {
		label = "Supprimer les élements sélectionnés";
		help = "Cliquez ici pour supprimer les éléments de la carte qui sont sélectionnés.";
		accelerator = shortcuts.DELETE_SELECTED_ELEMENTS;
		menuIcon = GuiIcons.SMALLICON_DELETE;
	}

	@Override
	public void run() {

		// pas d'execution dans l'EDT
		GuiUtils.throwIfOnEDT();

		// Verifier le projet et obtenir le calque actif, ou afficher un
		// message d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// lister les elements a supprimer
		ArrayList<LayerElement> elementsToDelete = new ArrayList<LayerElement>(
				20);
		for (LayerElement e : new ArrayList<LayerElement>(
				layer.getAllElementsReversed())) {
			if (e.isSelected()) {
				elementsToDelete.add(e);
			}
		}

		// suppression des elements listes
		for (LayerElement e : elementsToDelete) {
			layer.removeElement(e);
		}

		// enregistrement pour annulation
		ElementsCancelOp op = MainManager.getCancelManager().addDrawOperation(
				layer, elementsToDelete);
		op.elementsHaveBeenDeleted(true);

		// rafraichir la carte
		projectm.fireElementsChanged();

	}

}
