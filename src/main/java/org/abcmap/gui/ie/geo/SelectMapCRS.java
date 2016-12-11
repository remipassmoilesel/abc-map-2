package org.abcmap.gui.ie.geo;

import org.abcmap.gui.components.geo.CrsSelectionPanel;
import org.abcmap.gui.ie.InteractionElement;

import java.awt.*;

public class SelectMapCRS extends InteractionElement {

    private CrsSelectionPanel crsSelector;

    public SelectMapCRS() {
        this.label = "Système de coordonnées";
        this.help = "Choisissez ici votre système de coordonnées.";

        displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        crsSelector = new CrsSelectionPanel();

        //crsSelector.getListenerHandler().add(new MapManagerUpdater());

        /*
        CRSFormUpdater formUpdater = new CRSFormUpdater();
        notifm.setDefaultUpdatableObject(formUpdater);
        mapm.getNotificationManager().addObserver(this);

        formUpdater.updateFields();
        */

        return crsSelector;
    }

	/*
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

	*/

}
