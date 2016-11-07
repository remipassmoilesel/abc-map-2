package abcmap.gui.ie.geo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.gui.comps.geo.CoordinatesPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;

public class SelectGeoReferences extends InteractionElement {

	private CoordinatesPanel coordinatesPanel;
	private JComboBox<String> cbActiveReference;

	public SelectGeoReferences() {
		this.label = "Choisir les références";
		this.help = "Choisir les références";
	}

	@Override
	protected Component createPrimaryGUI() {

		// le panneau conteneur
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// combo de selection de la reference
		cbActiveReference = new JComboBox<String>(new String[] {
				"Première référence", "Deuxième référence" });
		panel.add(cbActiveReference);

		cbActiveReference.addActionListener(new ReferenceComboListener());

		// bouton de pointage
		JButton btnPointer = new JButton("Pointer");
		btnPointer.addActionListener(new PointButtonListener());
		panel.add(btnPointer, "wrap");

		// panneau de saisie de coordonnées
		coordinatesPanel = new CoordinatesPanel();
		coordinatesPanel.setDegreesConversionEnabled(true);
		panel.add(coordinatesPanel, "span, wrap");

		// ecouter les changements de projet
		MapManagerListener mapmListener = new MapManagerListener();
		mapm.getNotificationManager().addObserver(this);

		mapmListener.run();

		return panel;
	}

	/**
	 * Ecouter les clics sur le bouton "Pointer" pour activer l'outil de
	 * géoréférencement.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class PointButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			drawm.setCurrentTool(ToolLibrary.GEOREF_TOOL);
		}

	}

	/**
	 * Met à jour le formulaire en fonction des évenements reçu du gestionnaire
	 * de carte.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MapManagerListener extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			if (projectm.isInitialized() == false) {
				coordinatesPanel.setCoordinates(new Coordinate());
				return;
			}

			// récupérer l'index de la ref active
			int manIndex = mapm.getActiveReferenceIndex();
			manIndex = manIndex == -1 ? 0 : manIndex;

			// récupérer l'index de la liste
			int cbIndex = cbActiveReference.getSelectedIndex();

			// modifier le formulaire si necessaire
			if (manIndex != cbIndex) {
				GuiUtils.changeIndexWithoutFire(cbActiveReference, manIndex);
			}

			// récupérer la référence active
			Coordinate activeRef = mapm.getActiveReference();
			activeRef = activeRef == null ? new Coordinate() : activeRef;

			coordinatesPanel.setCoordinates(activeRef);

		}

	}

	/**
	 * Modifier la reference active du gestionnaire de carte en fonction de la
	 * selection dans la liste déroulante de choix de reference
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ReferenceComboListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {

			// valeur du combo
			int comboIndex = cbActiveReference.getSelectedIndex();

			// valeur du gestionnaire
			int managerIndex = mapm.getActiveReferenceIndex();

			// mettre à jour le manager si nécéssaire
			if (managerIndex != comboIndex) {

				try {
					mapm.setActiveReferenceIndex(comboIndex);
				} catch (MapManagerException e) {
					Log.error(e);
				}

			}

			// mettre à jour le panneau de coordonnées
			Coordinate activeRef = mapm.getActiveReference();

			// les coordonnées sont valides: afficher les valeurs
			if (activeRef != null) {
				coordinatesPanel.setCoordinates(activeRef);
			}

			// les coordonnées sont invalides: afficher des valeurs par défaut
			else {
				coordinatesPanel.setCoordinates(new Coordinate());
			}

		}

	}
}
