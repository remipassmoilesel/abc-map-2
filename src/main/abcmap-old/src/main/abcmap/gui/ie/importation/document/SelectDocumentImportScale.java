package abcmap.gui.ie.importation.document;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import abcmap.gui.comps.importation.ImageMemoryChargePanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.Utils;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

public class SelectDocumentImportScale extends InteractionElement {

	private static final Integer[] predefinedFactors = new Integer[] { 20, 60,
			100, 120, 160, 200, 220, 260, 300 };

	private JComboBox<Integer> cbFactor;
	private JTextField txtOriginalDimensions;
	private JTextField txtFinalDimensions;

	private ImageMemoryChargePanel memPanel;
	private ImageDimensionsUpdater imageDimensionsUpdater;

	private ParameterListener paramListener;
	private Updater formUpdater;

	public SelectDocumentImportScale() {
		this.label = "Facteur d'agrandissment du document";
		this.help = "Sélectionnez ici le facteur d'agrandissement qu sera appliqué pendant l'import. "
				+ "'1' importe le document tel quel, '0.8' l'importera à 80% de sa tailled'origine, '1.20' l'importera "
				+ "à 120% de sa taille d'origine. ";

		this.displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// champs texte de facteur de grossissement
		this.cbFactor = new JComboBox<Integer>(predefinedFactors);
		cbFactor.setEditable(true);
		// mettre à jour le gestionnaire d'import
		this.paramListener = new ParameterListener();
		cbFactor.addActionListener(paramListener);

		// mettre à jour le panneau de charge memoire
		this.imageDimensionsUpdater = new ImageDimensionsUpdater();
		cbFactor.addActionListener(imageDimensionsUpdater);

		// panneau de selection
		JPanel panel = new JPanel(new MigLayout("insets 0"));

		// dimensions originales
		GuiUtils.addLabel("Dimensions du document (pixels, MP): ", panel,
				"wrap");

		txtOriginalDimensions = new JTextField();
		txtOriginalDimensions.setEditable(false);
		panel.add(txtOriginalDimensions, "width 170px!, " + gapLeft + wrap15);

		// facteur de grossissement
		GuiUtils.addLabel("Echelle de reproduction: ", panel, "wrap");
		panel.add(cbFactor, "width 50px!, split 2," + gapLeft);
		GuiUtils.addLabel("%", panel, wrap15);

		// dimensions finales
		GuiUtils.addLabel("Dimensions finales de l'image (pixels, MP): ",
				panel, "wrap");
		txtFinalDimensions = new JTextField();
		txtFinalDimensions.setEditable(false);
		panel.add(txtFinalDimensions, "width 170px!, " + gapLeft + wrap15);

		// panneau d'indication de charge
		memPanel = new ImageMemoryChargePanel();
		panel.add(memPanel, "width 200px!, wrap");

		// ecouter les changements du gestionnaire d'import
		formUpdater = new Updater();
		notifm.setDefaultUpdatableObject(formUpdater);
		importm.getNotificationManager().addObserver(this);
		configm.getNotificationManager().addObserver(this);

		// premiere maj
		formUpdater.run();
		imageDimensionsUpdater.run();

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
			float factor = configm.getDocumentImportFactor();
			Float selectedFactor;
			try {
				selectedFactor = Float.parseFloat(cbFactor.getSelectedItem()
						.toString());
			} catch (Exception e) {
				Log.error(e);
				return;
			}

			if (Utils.safeEquals(factor, selectedFactor / 100) == false) {
				GuiUtils.changeWithoutFire(cbFactor, Math.round(factor * 100));
			}

		}

	}

	/**
	 * Mettre à jour le gestionnaire d'import en fonction de la saisie dans
	 * facteur et chemin d'import
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ParameterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {

			// mettre à jour le facteur
			float factor;
			try {
				factor = Float.valueOf(cbFactor.getSelectedItem().toString()) / 100;
			} catch (Exception e) {
				Log.error(e);
				return;
			}

			if (Utils.safeEquals(configm.getDocumentImportFactor(), factor) == false) {
				configm.setDocumentImportFactor(factor);
			}

		}

	}

	/**
	 * Mettre à jour le panneau d'indication de charges et les dimensions en
	 * fonction de la saisie de l'utilisateur.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ImageDimensionsUpdater implements ActionListener, Runnable {

		@Override
		public void run() {

			// recuperer les dimensions du document
			Dimension[] orignals = importm.getDocumentImportSize();

			// dimensions impossibles à déterminer
			if (orignals == null) {
				setMemoryValues(0);
				setTextFieldValues(0, 0, 0, 0, 0, 0);
				return;
			}

			Dimension totalDimensions = new Dimension();
			for (Dimension dim : orignals) {
				totalDimensions.width += dim.width;
				totalDimensions.height += dim.height;
			}

			// facteur de grossissement
			float factor = configm.getDocumentImportFactor();

			// dimensions finales
			int finalWidth = (int) (totalDimensions.width * factor);
			int finalHeight = (int) (totalDimensions.height * factor);

			// tailles en MP
			float originalSizeMp = Utils.round(totalDimensions.width
					* totalDimensions.height / 1000000f, 1);
			float finalSizeMp = Utils.round(
					finalWidth * finalHeight / 1000000f, 1);

			// indicateur de charge mémoire
			setMemoryValues(finalSizeMp);

			// champs texte
			setTextFieldValues(totalDimensions.width, totalDimensions.height,
					originalSizeMp, finalWidth, finalHeight, finalSizeMp);

		}

		/**
		 * Mettre à jour les champs de texte en fonction du gestionnaire
		 * d'import
		 * 
		 */
		private void setTextFieldValues(final int originWidth,
				final int originHeight, final float originMp,
				final int finalWidth, final int finalHeight,
				final float finalSizeMp) {

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					// taille originale du document
					txtOriginalDimensions.setText(originWidth + " x "
							+ originHeight + " (" + originMp + " MP)");

					// taille finale du document
					txtFinalDimensions.setText(finalWidth + " x " + finalHeight
							+ " (" + finalSizeMp + " MP)");

				}
			});

		}

		private void setMemoryValues(final double val) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					memPanel.setIndicationFor(val);
					memPanel.reconstruct();
				}
			});
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ThreadManager.runLater(this);
		}

	}

}
