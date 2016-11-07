package abcmap.gui.ie.importation.document;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;
import abcmap.utils.gui.BrowsePathActionListener;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SelectDocumentImportPath extends InteractionElement {

	private JTextField txtPath;
	private ParameterListener paramListener;
	private Updater formUpdater;

	public SelectDocumentImportPath() {
		this.label = "Chemin du document à importer";
		this.help = "Sélectionnez ici le chemin d'un document à importer.";

		this.displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// champs texte de saisie du chemin
		this.txtPath = new JTextField();

		// ecouter les saisies
		paramListener = new ParameterListener();
		TextFieldDelayedAction.delayedActionFor(txtPath, paramListener, false);

		// panneau de selection
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// champs texte
		panel.add(txtPath, "width 200px!, wrap");

		// bouton parcourir
		JButton btn = new JButton("Parcourir");
		btn.addActionListener(new BrowsePathActionListener(txtPath, true, true));
		panel.add(btn, "align right,");

		// ecouter les changements du gestionnaire d'import
		formUpdater = new Updater();
		notifm.setDefaultUpdatableObject(formUpdater);
		importm.getNotificationManager().addObserver(this);
		configm.getNotificationManager().addObserver(this);

		// premiere maj
		formUpdater.run();

		return panel;
	}

	/**
	 * Mettre à jour les champs de texte en fonction du gestionnaire d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class Updater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// mettre à jour le chemin du document
			GuiUtils.changeText(txtPath, configm.getDocumentImportPath());

		}

	}

	/**
	 * Mettre à jour le gestionnaire d'import en fonction de la saisie dans
	 * facteur et chemin d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ParameterListener implements Runnable {

		@Override
		public void run() {

			// mettre à jour le chemin
			String path = txtPath.getText();

			if (Utils.safeEquals(configm.getDocumentImportPath(), path) == false) {
				configm.setDocumentImportPath(path);
			}

		}

	}

}
