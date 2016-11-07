package abcmap.gui.comps.importation.data;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

/**
 * Combo de sélection des entetes du fichier courant à importer.
 * 
 * @author remipassmoilesel
 *
 */
public class DataHeadersCombo extends JComboBox<String> implements HasNotificationManager {

	private NotificationManager notifm;
	private ImportManager importm;

	public DataHeadersCombo() {
		super();

		importm = MainManager.getImportManager();

		// non editable
		setEditable(false);

		// ecouter les changements dans le gestionnaire d'import
		ComboUpdater cbUpdater = new ComboUpdater();
		notifm = new NotificationManager(this);
		notifm.setDefaultUpdatableObject(cbUpdater);
		importm.getNotificationManager().addObserver(this);

		// premiere mise à jour des champs
		cbUpdater.updateFields();

	}

	private class ComboUpdater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recupérer toutes les valeurs du combo
			List<String> currents = GuiUtils.getAllValuesFrom(DataHeadersCombo.this);

			// recuperer les valeurs du manager
			ArrayList<String> importHeaders = importm.getDataImportCurrentHeaders();
			if (importHeaders.size() < 1) {
				importHeaders.add("Aucun en-tête");
			}

			// changer les valeurs si necessaire
			if (Utils.safeEquals(importHeaders, currents) == false) {

				// creer un nouveau modele
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(
						importHeaders.toArray(new String[importHeaders.size()]));

				// affecter le modele
				DataHeadersCombo.this.setModel(model);

			}

		}

	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

}
