package abcmap.gui.ie.draw;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.comps.draw.MapDimensionsPanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;

public class SelectMapDimensions extends InteractionElement {

	private MapDimensionsPanel mapDimensionsPanel;

	public SelectMapDimensions() {

		label = "Dimensions de la carte";
		help = "Dimensions de la carte";

		displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// tableau de dimensions de carte
		mapDimensionsPanel = new MapDimensionsPanel();

		// ecouter les saisies numériques
		mapDimensionsPanel.addDelayedAction(new TextFieldListener(), false);

		// ecouter l'activation des dims dynamqiues
		mapDimensionsPanel.getChkDynamicDimensions().addActionListener(
				new DynamicDimsAL());

		// ecouter les changements de projet
		ManagerListener mlistener = new ManagerListener();
		notifm.setDefaultUpdatableObject(mlistener);
		projectm.getNotificationManager().addObserver(this);

		mlistener.run();

		// composant
		return mapDimensionsPanel;
	}

	/**
	 * Mise a jour de l'etat des dimensions de la carte: Fixe / Dynamique
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class DynamicDimsAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// Verifier le projet et obtenir le calque actif, ou afficher un
			// message d'erreur
			MapLayer layer = checkProjectAndGetActiveLayer();
			if (layer == null) {
				return;
			}

			boolean fixedDimensions = !mapDimensionsPanel
					.getChkDynamicDimensions().isSelected();

			if (Utils.safeEquals(projectm.isDimensionsFixed(), fixedDimensions) == false) {
				projectm.setDimensionsFixed(fixedDimensions);
				projectm.fireMetadatasChanged();
			}

		}

	}

	/**
	 * Mise à jour des dimensions de la carte.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldListener implements Runnable {

		@Override
		public void run() {

			// Verifier le projet et obtenir le calque actif, ou afficher un
			// message d'erreur
			MapLayer layer = checkProjectAndGetActiveLayer();
			if (layer == null) {
				return;
			}

			// recuperer les dimensions
			Dimension values;
			try {
				values = mapDimensionsPanel.getValues();
			} catch (InvalidInputException e) {
				return;
			}

			if (Utils.safeEquals(projectm.getMapDimensions(), values) == false) {
				projectm.proposeMapDimensions(values, true);
				projectm.fireMetadatasChanged();
			}

		}

	}

	/**
	 * Mise à jour du formulaire lors de la reception d'un evenement
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ManagerListener extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			if (projectm.isInitialized() == false) {
				mapDimensionsPanel.updateValuesWithoutFire(new Dimension());
				mapDimensionsPanel.updateDynamicDimsCheckBoxWithoutFire(false);
				return;
			}

			// recuperer les dimensions du projet
			Dimension dims = projectm.getMapDimensions();
			mapDimensionsPanel.updateValuesWithoutFire(dims);

			// recuperer la valeur des dim automatiques
			boolean dynamicsDimensions = !projectm.isDimensionsFixed();
			mapDimensionsPanel
					.updateDynamicDimsCheckBoxWithoutFire(dynamicsDimensions);

		}
	}
}
