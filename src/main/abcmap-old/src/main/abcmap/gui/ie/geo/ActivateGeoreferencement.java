package abcmap.gui.ie.geo;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;

public class ActivateGeoreferencement extends InteractionElement {

	private HtmlCheckbox chkActivateGeoref;

	public ActivateGeoreferencement() {

		this.label = "Activer le géoréférencement";
		this.help = "Activez ou désactivez ici le géoréférencement pour pouvoir utiliser un système de coordonnées sur votre carte.";

		displaySimplyInSearch = false;

	}

	@Override
	protected Component createPrimaryGUI() {

		// le composant graphique
		JPanel panel = new JPanel(new MigLayout("insets 0"));
		chkActivateGeoref = new HtmlCheckbox("Activer le géoréférencement");
		panel.add(chkActivateGeoref);

		// ecouter la saisie de l'utilisateur
		chkActivateGeoref.addActionListener(new ManagerUpdater());

		// ecouter les changements du gestionnaire de carte
		CheckBoxUpdater formUpdater = new CheckBoxUpdater();
		notifm.setDefaultUpdatableObject(formUpdater);
		mapm.getNotificationManager().addObserver(this);

		// premiere mise à jour
		formUpdater.run();

		return panel;
	}

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

	/**
	 * Mettre à jour le checkbox en fonction des changements du manager
	 * 
	 * @author remipassmoilesel
	 *
	 */
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

}
