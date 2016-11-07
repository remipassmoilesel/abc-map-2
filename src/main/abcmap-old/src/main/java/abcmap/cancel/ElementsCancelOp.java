package abcmap.cancel;

import java.util.ArrayList;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;

public class ElementsCancelOp implements CancelOperation {

	// calque propritaire
	private MapLayer layOwner;

	// before, after, de meme type
	private ArrayList<LayerElement> elements;

	private ProjectManager projectc;

	private boolean elementsHaveBeenAdded;
	private boolean elementsHaveBeenDeleted;

	public ElementsCancelOp(MapLayer owner, ArrayList<LayerElement> elmts) {

		this.projectc = MainManager.getProjectManager();

		if (projectc.isInitialized() == false)
			throw new IllegalStateException("Non initialized project");

		this.layOwner = owner;

		this.elements = new ArrayList<LayerElement>(elmts);

		this.elementsHaveBeenAdded = false;
		this.elementsHaveBeenDeleted = false;

	}

	@Override
	public void cancel() {

		if (projectc.isInitialized() == false)
			return;

		// activer le calque propritaire
		try {
			projectc.setActiveLayer(layOwner);
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// dselectionner tout
		projectc.setAllElementsSelected(false);

		// l'element viens juste d'etre ajoute
		if (elementsHaveBeenAdded) {
			for (LayerElement e : elements) {
				layOwner.removeElement(e);
			}
		}

		else if (elementsHaveBeenDeleted) {
			for (LayerElement e : elements) {
				layOwner.addElement(e);
				e.setSelected(true);
			}
		}

		// l'element change de proprietes
		else {

			for (LayerElement e : elements) {
				e.getMementoManager().restore();
				e.setSelected(true);
				e.refreshShape();
			}

		}

		// notifier le changement
		projectc.fireElementsChanged();
	}

	@Override
	public void redo() {

		if (projectc.isInitialized() == false)
			return;

		if (projectc.getLayers().contains(layOwner) == false)
			return;

		// activer le calque propritaire
		try {
			projectc.setActiveLayer(layOwner);
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// dselectionner tout
		projectc.setAllElementsSelected(false);

		// l'lment viens juste d'tre ajout
		if (elementsHaveBeenAdded) {

			for (LayerElement e : elements) {
				layOwner.addElement(e);
				e.setSelected(true);
			}
		}

		else if (elementsHaveBeenDeleted) {

			for (LayerElement e : elements) {
				layOwner.removeElement(e);
			}
		}

		// l'lment chang de proprits
		else {

			for (LayerElement e : elements) {
				e.getMementoManager().redo();
				e.setSelected(true);
				e.refreshShape();
			}
		}

		// notifier le changement
		projectc.fireElementsChanged();

	}

	public void elementsHaveBeenAdded(boolean v) {
		elementsHaveBeenAdded = v;
	}

	public void elementsHaveBeenDeleted(boolean v) {
		elementsHaveBeenDeleted = v;
	}

	@Override
	public String toString() {
		
		Object[] values = new Object[]{
				layOwner,
				elements.size(),
				elementsHaveBeenAdded,
				elementsHaveBeenDeleted
		};
		Object[] keys = new Object[]{
				"layOwner",
				"elements.size()",
				"elementsHaveBeenAdded",
				"elementsHaveBeenDeleted"
		};
		
		return Utils.toString(this, keys, values);

	}

	public ArrayList<LayerElement> getElements() {
		return new ArrayList<LayerElement>(elements);
	}

}