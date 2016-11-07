package abcmap.gui.comps.draw.layers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import abcmap.cancel.MapLayerCancelOp;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.dialogs.RenameLayerDialog;
import abcmap.managers.CancelManager;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;

public class LayerListButtonsAL implements ActionListener {

	public static final String NEW = "NEW";
	public static final String REMOVE = "REMOVE";

	public static final String RENAME = "RENAME";
	public static final String DUPLICATE = "DUPLICATE";
	public static final String CHANGE_VISIBILITY = "CHANGE_VISIBILITY";

	public static final String MOVE_UP = "MOVE_UP";
	public static final String MOVE_DOWN = "MOVE_DOWN";

	private String mode;
	private ProjectManager projectm;
	private CancelManager cancelm;
	private GuiManager guim;

	public LayerListButtonsAL(String mode) {
		this.mode = mode;
		this.projectm = MainManager.getProjectManager();
		this.cancelm = MainManager.getCancelManager();
		this.guim = MainManager.getGuiManager();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// projet non initialisé: retour
		if (projectm.isInitialized() == false)
			return;

		// recuperer le calque actif
		MapLayer lay;
		try {
			lay = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// changement de visiblité
		if (CHANGE_VISIBILITY.equals(mode)) {
			changeLayerVisibility(lay);
		}

		// creer un calque
		else if (NEW.equals(mode)) {
			createNewLayer();
		}

		// monter le calque
		else if (MOVE_UP.equals(mode)) {
			moveLayer(lay, +1);
		}

		// descendre le calque
		else if (MOVE_DOWN.equals(mode)) {
			moveLayer(lay, -1);
		}

		// descendre le calque
		else if (REMOVE.equals(mode)) {
			removeLayer(lay);
		}

		// descendre le calque
		else if (DUPLICATE.equals(mode)) {
			duplicateLayer(lay);
		}

		// descendre le calque
		else if (RENAME.equals(mode)) {
			renameLayer(lay);
		}

	}

	private void changeLayerVisibility(MapLayer lay) {

		lay.getMementoManager().saveStateToRestore();
		lay.setVisible(!lay.isVisible());
		lay.getMementoManager().saveStateToRedo();

		cancelm.addMapLayerOperation(lay);

		projectm.fireLayerListChanged();

	}

	private void moveLayer(MapLayer lay, int step) {

		// verifier l'index de deplacement
		ArrayList<MapLayer> layers = projectm.getLayers();
		int index = layers.indexOf(lay) + step;

		// l'index est correct
		if (index >= 0 && index < layers.size()) {

			// sauvegarde pour annulation
			projectm.getMementoManager().saveStateToRestore();

			// bouger sans notifications
			projectm.getProject().setNotificationsEnabled(false);

			try {
				projectm.removeLayer(lay);
				projectm.addLayer(lay, index);
			} catch (MapLayerException e) {
				Log.debug(e);
				// retour ou pas retour ? Attention au MementoManager
			}

			projectm.getProject().setNotificationsEnabled(true);

			// notifier du changement (1 seule fois)
			projectm.fireLayerListChanged();

			// a confirmer si necessaire
			// // activer le calque
			// try {
			// projectm.setActiveLayer(lay);
			// } catch (MapLayerException e) {
			// // pas de return ici
			// }

			// enregistrement pour refaire
			projectm.getMementoManager().saveStateToRedo();

			cancelm.addProjectListsOperation();
		}

	}

	private void removeLayer(MapLayer lay) {

		// enregistrer l'opération
		MapLayerCancelOp op = cancelm.addMapLayerOperation(lay);
		op.layerHaveBeenDeleted(true);

		// supprimer le calque puis choisir le prochain
		projectm.removeLayer(lay);

		// trouver un nouvel index de calque actif
		int index = projectm.getLayers().size() - 1;
		if (index < 0)
			index = 0;

		// changer le calque actif
		try {
			projectm.setActiveLayer(index);
		} catch (MapLayerException e) {
			Log.debug(e);
		}

	}

	private void createNewLayer() {

		// creer un nouveau calque
		MapLayer lay = projectm.addNewLayer();

		// activer le nouveau calque
		try {
			projectm.setActiveLayer(lay);
		} catch (MapLayerException e) {
			Log.debug(e);
		}

		// enregistrement de l'opération pour annulation
		MapLayerCancelOp op = cancelm.addMapLayerOperation(lay);
		op.layerHaveBeenAdded(true);

	}

	private void duplicateLayer(final MapLayer lay) {

		// execution dans un thread different, car possiblement long
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {

				// dupliquer le calque
				MapLayer newLay = lay.duplicate();

				// changer le nom
				newLay.setName(lay.getName() + " (copie)");

				// ajouter le nouveau calque
				projectm.addLayer(newLay);

				// activer le nouveau calque
				try {
					projectm.setActiveLayer(newLay);
				} catch (MapLayerException e) {
					Log.debug(e);
				}

				// enregistrement de l'opération pour annulation
				MapLayerCancelOp op = cancelm.addMapLayerOperation(newLay);
				op.layerHaveBeenAdded(true);

			}
		});
	}

	private void renameLayer(MapLayer lay) {

		// recuperer l'ancien nom
		String name = lay.getName();

		// afficher une boite dedialogue de renommage
		RenameLayerDialog dial = new RenameLayerDialog(guim.getMainWindow(), name);
		dial.setVisible(true);

		// renommer uniquement si l'utilisateur clqiue sur O.K.
		if (RenameLayerDialog.ACTION_VALIDATED.equals(dial.getAction())) {

			// enregistrement pour annulation
			lay.getMementoManager().saveStateToRestore();

			// renommer
			lay.setName(dial.getInput());

			// enregistrement de l'opération
			lay.getMementoManager().saveStateToRedo();
			cancelm.addMapLayerOperation(lay);

		}

	}

}
