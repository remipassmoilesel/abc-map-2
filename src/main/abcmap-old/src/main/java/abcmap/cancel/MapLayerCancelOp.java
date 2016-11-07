package abcmap.cancel;

import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;

public class MapLayerCancelOp implements CancelOperation {

	private MapLayer layer;
	private boolean layerHaveBeenAdded;
	private boolean layerHaveBeenDeleted;
	private int index;
	private ProjectManager projectc;

	public MapLayerCancelOp(MapLayer lay) {

		this.projectc = MainManager.getProjectManager();
		this.layer = lay;

		if (projectc.isInitialized() == false)
			throw new IllegalStateException("Non initialized project");

		this.index = projectc.getLayers().indexOf(lay);

		if (index == -1)
			throw new IllegalStateException("Unknown element");

		this.layerHaveBeenAdded = false;
		this.layerHaveBeenDeleted = false;
	}

	@Override
	public void cancel() {

		if (projectc.isInitialized() == false)
			return;

		// le calque viens juste d'etre ajoute : retrait
		if (layerHaveBeenAdded) {

			if (projectc.getLayers().contains(layer) == false)
				throw new IllegalStateException("Unknown element");

			projectc.removeLayer(layer);
			try {
				projectc.setActiveLayer(0);
			} catch (MapLayerException e) {
				Log.debug(e);
			}
		}

		// le calque viens juste d'être retiré: ajout
		else if (layerHaveBeenDeleted) {
			try {
				projectc.addLayer(layer, index);
			} catch (MapLayerException e) {
				projectc.addLayer(layer);
			}
			try {
				projectc.setActiveLayer(layer);
			} catch (MapLayerException e) {
				Log.debug(e);
			}
		}

		// changement d'état
		else {
			if (projectc.getLayers().contains(layer) == false)
				throw new IllegalStateException("Unknown element");

			layer.getMementoManager().restore();
			try {
				projectc.setActiveLayer(layer);
			} catch (MapLayerException e) {
				Log.debug(e);
			}
		}

		projectc.fireLayerListChanged();
	}

	@Override
	public void redo() {

		if (projectc.isInitialized() == false)
			return;

		// le calque viens juste d'etre ajoute : retrait
		if (layerHaveBeenAdded) {

			try {
				projectc.addLayer(layer, index);
			} catch (MapLayerException e) {
				Log.debug(e);
				projectc.addLayer(layer);
			}
			try {
				projectc.setActiveLayer(layer);
			} catch (MapLayerException e) {
				Log.debug(e);
			}

		}

		// le calque viens juste d'être retiré: ajout
		else if (layerHaveBeenDeleted) {
			if (projectc.getLayers().contains(layer) == false)
				throw new IllegalStateException("Unknown element");

			projectc.removeLayer(layer);
			try {
				projectc.setActiveLayer(0);
			} catch (MapLayerException e) {
				Log.debug(e);
			}
		}

		// changement d'état
		else {

			if (projectc.getLayers().contains(layer) == false)
				throw new IllegalStateException("Unknown element");

			layer.getMementoManager().redo();
			try {
				projectc.setActiveLayer(layer);
			} catch (MapLayerException e) {
				Log.debug(e);
			}
		}

		projectc.fireLayerListChanged();

	}

	public void layerHaveBeenAdded(boolean val) {
		this.layerHaveBeenAdded = val;
	}

	public void layerHaveBeenDeleted(boolean val) {
		this.layerHaveBeenDeleted = val;
	}

}
