package abcmap.gui.ie.importation.data;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;
import abcmap.utils.gui.BrowsePathActionListener;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SelectDataToImport extends InteractionElement {

	private JTextField txtPath;
	private ImportManagerUpdater textFieldListener;

	public SelectDataToImport() {
		label = "Chemin du fichier à importer";
		help = "Sélectionnez ci-dessous le fichier de données à importer.";
	}

	@Override
	protected Component createPrimaryGUI() {

		// champs texte de saisie du chemin
		this.txtPath = new JTextField();

		// champs de saisie de chemin de répertoire
		this.textFieldListener = new ImportManagerUpdater();

		// mettre à jour le gestionnaire de configuration
		TextFieldDelayedAction.delayedActionFor(txtPath, textFieldListener,
				false);

		// panneau de selection
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// champs texte
		panel.add(txtPath, "width 200px!, wrap");

		// bouton parcourir
		JButton btn = new JButton("Parcourir");
		btn.addActionListener(new BrowsePathActionListener(txtPath, true, true));
		panel.add(btn, "align right, ");

		// mise à jour en fonction des changements de configuration
		TextFieldUpdater txtUpdater = new TextFieldUpdater();
		notifm.setDefaultUpdatableObject(txtUpdater);
		configm.getNotificationManager().addObserver(this);

		// premiere mise à jour
		txtUpdater.updateFields();

		return panel;

	}

	/**
	 * Mettre à jour le gestionnaire d'import en fonction de la saisie
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ImportManagerUpdater implements Runnable {

		@Override
		public void run() {

			String path = txtPath.getText();

			if (Utils.safeEquals(configm.getDataImportPath(), path) == false) {
				configm.setDataImportPath(path);
			}
		}

	}

	/**
	 * Mettre à jour le chmaps texte en fonction du gestionnaire d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldUpdater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			GuiUtils.changeText(txtPath, configm.getDataImportPath());
		}

	}

}
