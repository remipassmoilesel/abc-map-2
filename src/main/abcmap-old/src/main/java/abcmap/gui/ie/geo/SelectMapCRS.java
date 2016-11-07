package abcmap.gui.ie.geo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.exceptions.MapManagerException;
import abcmap.gui.comps.geo.CrsSelectionPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;

public class SelectMapCRS extends InteractionElement {

	private CrsSelectionPanel crsSelector;

	public SelectMapCRS() {
		this.label = "Système de coordonnées";
		this.help = "Choisissez ici votre système de coordonnées.";

		displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// le panneau de sélection de CRS
		crsSelector = new CrsSelectionPanel();

		// mettre à jour le manager en fonction des selections utilisateur
		crsSelector.getListenerHandler().add(new MapManagerUpdater());

		// ecouter les changements de la carte
		CRSFormUpdater formUpdater = new CRSFormUpdater();
		notifm.setDefaultUpdatableObject(formUpdater);
		mapm.getNotificationManager().addObserver(this);

		// premiere mise à jour
		formUpdater.updateFields();

		// creer et retourner l'element
		return crsSelector;
	}

	/**
	 * Met à jour le manager en fonction de la selection de l'utilisateur
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MapManagerUpdater implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer le system selectionne
			CoordinateReferenceSystem selectedCRS = crsSelector
					.getSelectedSystem();

			// recupere le systeme du gestionnaire
			CoordinateReferenceSystem managerCrs = mapm.getMapCRS();

			if (Utils.safeEquals(selectedCRS, managerCrs) == false) {
				mapm.setMapCRS(selectedCRS);
			}
		}

	}

	/**
	 * Mettre à jour le panneau de sélection à partir du manager
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CRSFormUpdater extends FormUpdater {

		@Override
		protected void updateFields() {

			// Crs sélectionné
			CoordinateReferenceSystem selectedSystem = crsSelector
					.getSelectedSystem();

			// recuperer le CRS du manager
			CoordinateReferenceSystem managerCrs = mapm.getMapCRS();

			// mise à jour si necessaire
			if (Utils.safeEquals(selectedSystem, managerCrs) == false) {
				crsSelector.updateSystemWithoutFire(selectedSystem);
			}

		}

	}

}
