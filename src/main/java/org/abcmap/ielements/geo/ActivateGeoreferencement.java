package org.abcmap.ielements.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class ActivateGeoreferencement extends InteractionElement {

    private HtmlCheckbox chkActivateGeoref;

    public ActivateGeoreferencement() {

        this.label = "Activer le géoréférencement";
        this.help = "Activez ou désactivez ici le géoréférencement pour pouvoir utiliser un système de coordonnées sur votre carte.";

        displaySimplyInSearch = false;

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0"));
        chkActivateGeoref = new HtmlCheckbox("Activer le géoréférencement");
        panel.add(chkActivateGeoref);

        /*
        chkActivateGeoref.addActionListener(new ManagerUpdater());

        CheckBoxUpdater formUpdater = new CheckBoxUpdater();
        notifm.setDefaultUpdatableObject(formUpdater);
        mapm.getNotificationManager().addObserver(this);
        */

        return panel;
    }

	/*

	private class ManagerUpdater implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer la valeur du manager
			boolean managerValue = mapm.isGeoreferencementEnabled();

			// recuperer la valeur du composant graphique
			boolean buttonValue = chkActivateGeoref.isSelected();

			// mise à jour si necessaire
			if (Utils.safeEquals(buttonValue, managerValue) == false) {
				mapm.setGeorefencementEnabled(buttonValue);
			}

		}

	}

	private class CheckBoxUpdater extends FormUpdater {
		@Override
		protected void updateFields() {

			// recuperer la valeur du gestionnaire
			boolean managerValue = mapm.isGeoreferencementEnabled();

			// mise à jour si necessaire
			if (Utils.safeEquals(chkActivateGeoref.isSelected(), managerValue) == false) {
				chkActivateGeoref.setSelected(managerValue);
			}

		}
	}

	*/

}
