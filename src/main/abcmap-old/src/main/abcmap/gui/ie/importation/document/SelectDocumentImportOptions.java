package abcmap.gui.ie.importation.document;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.importation.documents.DocumentImporter;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.TextFieldDelayedAction;
import net.miginfocom.swing.MigLayout;

public class SelectDocumentImportOptions extends InteractionElement {

	/** Valeur par defaut du champs de selection de page lorsqu'il est activé */
	private static final String DEFAULT_TXT_VALUE = "1";

	private Updater formUpdater;
	private JRadioButton rdImage;
	private JRadioButton rdTile;
	private JRadioButton rdAllPages;
	private JRadioButton rdNotAllPages;
	private JTextField txtPages;

	public SelectDocumentImportOptions() {
		this.label = "Options d'import de document";
		this.help = "Sélectionnez ici les options pour l'import de document.";

		this.displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// panneau de selection
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// choix image ou tuile
		GuiUtils.addLabel("Importer en tant que: ", panel, "wrap");

		rdTile = new JRadioButton("Tuile");
		rdImage = new JRadioButton("Image");

		rdTile.setSelected(true);
		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(rdTile);
		bg1.add(rdImage);

		rdTile.addActionListener(new TypeImportListener());
		rdImage.addActionListener(new TypeImportListener());

		panel.add(rdTile, "split 2, " + gapLeft);
		panel.add(rdImage, gapLeft + wrap15);

		// choix de la page
		GuiUtils.addLabel("Pages à importer: ", panel, "wrap");

		rdAllPages = new JRadioButton("Toutes les pages");
		rdNotAllPages = new JRadioButton("Seulement les pages: ");
		txtPages = new JTextField();
		TextFieldDelayedAction.delayedActionFor(txtPages, new PageTextListener(), false);

		rdAllPages.setSelected(true);
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(rdAllPages);
		bg2.add(rdNotAllPages);

		rdAllPages.addActionListener(new PagesCheckBoxListener());
		rdNotAllPages.addActionListener(new PagesCheckBoxListener());

		panel.add(rdAllPages, gapLeft + "wrap");
		panel.add(rdNotAllPages, gapLeft + "split 2");
		panel.add(txtPages, "gapleft 5px, width 50px," + wrap15);

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

			// mettre à jour les boutons radio de type
			String type = configm.getDocumentImportType();
			String formType = rdTile.isSelected() ? DocumentImporter.IMPORT_AS_TILE
					: DocumentImporter.IMPORT_AS_IMAGE;

			if (Utils.safeEquals(type, formType) == false) {
				if (type.equals(DocumentImporter.IMPORT_AS_TILE)) {
					updateComponentWithoutFire(rdTile, true);
				}

				else {
					updateComponentWithoutFire(rdImage, true);
				}
			}

			// mettre à jour les pages à importer
			String value = configm.getDocumentImportPages();

			// toutes les pages
			if (value.equals(DocumentImporter.ALL_PAGES)) {

				// bouton radio
				updateComponentWithoutFire(rdAllPages, true);

				// champs de texte
				txtPages.setEnabled(false);
				updateComponentWithoutFire(txtPages, "");
			}

			// quelques pages seulement
			else {

				// bouton radio
				updateComponentWithoutFire(rdNotAllPages, true);

				// champs de texte
				txtPages.setEnabled(true);
				updateComponentWithoutFire(txtPages, value);
			}

		}

	}

	/**
	 * Mettre à jour le manager en fontion des boutons radio de type d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TypeImportListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer le parametre en cours
			String oldType = configm.getDocumentImportType();

			// nouveau parametre
			String newType = e.getSource().equals(rdTile) ? DocumentImporter.IMPORT_AS_TILE
					: DocumentImporter.IMPORT_AS_IMAGE;

			// changer si différents
			if (newType.equalsIgnoreCase(oldType) == false) {
				configm.setDocumentImportType(newType);
			}
		}

	}

	/**
	 * Mettre à jour le manager en fontion des boutons radio de pages d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class PagesCheckBoxListener implements ActionListener {

		/**
		 * Mettre à jour le manager en fonctions des boutons radio de sélection
		 * de page
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			// importer toutes les pages
			if (rdAllPages.isSelected() == true) {

				if (configm.getDocumentImportPages().equals(DocumentImporter.ALL_PAGES) == false) {
					// changer le parametre
					configm.setDocumentImportPages(DocumentImporter.ALL_PAGES);
				}

				// vider et desactiver le champ
				txtPages.setEnabled(false);
				GuiUtils.changeText(txtPages, "");
			}

			// importer seulement celles sélectionnées
			else {
				txtPages.setEnabled(true);
				GuiUtils.changeText(txtPages, DEFAULT_TXT_VALUE);

				// changer le parametre
				if (configm.getDocumentImportPages().equals(DEFAULT_TXT_VALUE) == false) {
					configm.setDocumentImportPages(DEFAULT_TXT_VALUE);
				}
			}

		}

	}

	private class PageTextListener implements Runnable {

		@Override
		public void run() {

			// recuperer la valeur
			String value = txtPages.getText();

			// tester son format
			if (Utils.isStringIntArray(value) == false) {
				return;
			}

			if (configm.getDocumentImportPages().equals(value) == false) {
				configm.setDocumentImportPages(value);
			}

		}

	}

}
