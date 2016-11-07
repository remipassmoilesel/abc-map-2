package abcmap.gui.ie.importation.directory;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import abcmap.gui.comps.importation.ImageMemoryChargePanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.Utils;
import abcmap.utils.gui.BrowsePathActionListener;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.TextFieldDelayedAction;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

public class SelectDirectoryToImport extends InteractionElement {

	private JTextField txtPath;
	private ImportManagerUpdater textFieldListener;
	private TextFieldUpdater textfieldUpdater;
	private ImageMemoryChargePanel memPanel;
	private Runnable memoryPanelUpdater;

	public SelectDirectoryToImport() {
		this.label = "Chemin du dossier d'images";
		this.help = "Entrez le chemin d'un dossier où importer des images.";
	}

	@Override
	protected Component createPrimaryGUI() {

		// champs texte de saisie du chemin
		this.txtPath = new JTextField();

		// champs de saisie de chemin de répertoire
		this.textFieldListener = new ImportManagerUpdater();

		// panneau indicateur de charge memoire
		this.memoryPanelUpdater = new MemoryPanelUpdater();

		// mettre à jour le panneau d'indications de charge mémoire
		TextFieldDelayedAction.delayedActionFor(txtPath, memoryPanelUpdater,
				false);

		// mettre à jour le gestionnaire de configuration
		TextFieldDelayedAction.delayedActionFor(txtPath, textFieldListener,
				false);

		// panneau de selection
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// champs texte
		panel.add(txtPath, "width 200px!, wrap");

		// bouton parcourir
		JButton btn = new JButton("Parcourir");
		btn.addActionListener(new BrowsePathActionListener(txtPath, false,
				true));
		panel.add(btn, "align right, " + wrap15);

		// panneau d'indication de charge
		memPanel = new ImageMemoryChargePanel();
		panel.add(memPanel, "width 200px!, wrap");

		// ecouter les changements du gestionnaire d'import et de configuration
		textfieldUpdater = new TextFieldUpdater();
		notifm.setDefaultUpdatableObject(textfieldUpdater);
		configm.getNotificationManager().addObserver(this);
		importm.getNotificationManager().addObserver(this);

		// premiere maj des champs de texte
		textfieldUpdater.run();

		// premiere maj du panneau
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {
				memoryPanelUpdater.run();
			}
		});

		return panel;
	}

	/**
	 * Mettre à jour le chmaps texte en fonction du gestionnaire d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldUpdater implements UpdatableByNotificationManager,
			Runnable {

		@Override
		public void notificationReceived(Notification arg) {
			SwingUtilities.invokeLater(this);
		}

		@Override
		public void run() {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfNotOnEDT();

			GuiUtils.changeText(txtPath, configm.getDirectoryImportPath());
		}

	}

	/**
	 * Mettre à jour le panneau d'indicateur de charge de mémoire
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MemoryPanelUpdater implements Runnable {

		/** Nombre d'images max à analyser pour déduire la taille totale */
		private static final int FILES_TO_ANALYSE_AS_SAMPLE = 3;

		/** Taux de recouvrement / 100 */
		private static final float COVERING_PERCENT = 0.1f;

		/**
		 * Evalue la taille totale d'import et met à jour le panneau
		 * 
		 * @param path
		 */

		@Override
		public void run() {

			// pas d'action hors de l'EDT
			GuiUtils.throwIfOnEDT();

			// fichier du repertoire
			File directory = new File(txtPath.getText());

			// le fichier est invalide, retour
			if (directory.isDirectory() == false) {
				setMemoryValues(0);
				return;
			}

			// lister les fichiers disponibles
			ArrayList<File> files;
			try {
				files = importm.getAllValidPicturesFrom(directory);
			} catch (IOException e) {
				setMemoryValues(0);
				return;
			}

			// compter les fichiers disponibles
			int fileNbr = files.size();

			// dimensions pour une seule image
			double estimatedWidth = 0d;
			double estimatedHeight = 0d;

			// le recadrage est activé, prendre en compte la taille de recadrage
			if (configm.isCroppingEnabled()) {

				Rectangle rect = configm.getCropRectangle();
				estimatedWidth = (int) (rect.width - rect.width
						* COVERING_PERCENT);
				estimatedHeight = (int) (rect.height - rect.height
						* COVERING_PERCENT);
			}

			// le recadrage est desactivé, prendre en compte les n premieres
			// images
			else {

				int i = 0;
				int sumW = 0;
				int sumH = 0;
				for (; i < FILES_TO_ANALYSE_AS_SAMPLE && i < files.size(); i++) {
					try {
						Dimension dim = Utils.getImageDimensions(files.get(i));
						sumW += dim.width;
						sumH += dim.height;
					} catch (IOException e) {
						Log.error(e);
					}
				}

				int avgW = sumW / i;
				int avgH = sumH / i;

				estimatedWidth = (int) (avgW - avgW * COVERING_PERCENT);
				estimatedHeight = (int) (avgH - avgH * COVERING_PERCENT);
			}

			// calculer la taille totale
			double valueMp = estimatedWidth * estimatedHeight * fileNbr
					/ 1000000d;

			setMemoryValues(valueMp);
		}

		private void setMemoryValues(final double val) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					memPanel.setIndicationFor(val);
					memPanel.reconstruct();
				}
			});
		}
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

			if (Utils.safeEquals(configm.getDirectoryImportPath(), path) == false) {
				configm.setDirectoryImportPath(path);
			}
		}

	}

}
