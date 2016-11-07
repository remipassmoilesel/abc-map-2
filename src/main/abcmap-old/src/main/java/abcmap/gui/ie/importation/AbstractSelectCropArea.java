package abcmap.gui.ie.importation;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.event.DocumentEvent;

import abcmap.exceptions.InvalidInputException;
import abcmap.exceptions.MapImportException;
import abcmap.gui.comps.importation.CropDimensionsPanel;
import abcmap.gui.comps.importation.CropDimensionsPanel.Mode;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.importation.CropConfigurator;
import abcmap.utils.Utils;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.FormUpdater;
import abcmap.utils.gui.Lng;
import abcmap.utils.threads.ThreadManager;

/**
 * Selection de zone de recadrage. Accepte comme mode les chaines
 * CropConfigurator.FOR_DIRECTOY_IMPORT et CropConfigurator.FOR_SCREEN_IMPORT
 * 
 * @author remipassmoilesel
 *
 */
public abstract class AbstractSelectCropArea extends InteractionElement {

	private CropDimensionsPanel cropPanel;
	private TextFieldsUpdater textfieldsUpdater;
	private String mode;

	public AbstractSelectCropArea(String mode) {

		this.mode = mode;

		this.label = "Recadrage des images";
		this.help = "Sléectionnez ci-dessous l'aire qui sera recadrée lors des imports.";

		this.displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		// panneau de selection
		cropPanel = new CropDimensionsPanel(Mode.WITH_VISUAL_CONFIG_BUTTON);
		cropPanel.addListener(new TextfieldListener());

		// action de configuration visuelle de recadrage
		cropPanel.getBtnVisualConfig().addActionListener(
				new VisualCropConfigLauncher());

		// bouton d'activation du recadrage
		cropPanel.activateCroppingListener(true);

		// ecouter les changements de projets et de managers
		textfieldsUpdater = new TextFieldsUpdater();

		notifm.setDefaultUpdatableObject(textfieldsUpdater);
		configm.getNotificationManager().addObserver(this);
		projectm.getNotificationManager().addObserver(this);

		// mettre à jour
		textfieldsUpdater.run();

		return cropPanel;

	}

	private class TextFieldsUpdater extends FormUpdater {

		@Override
		protected void updateFields() {
			super.updateFields();

			// recuperer les nouvelles données
			Rectangle newRect = configm.getCropRectangle();

			// recuperer les données du formulaire
			Rectangle rect = new Rectangle();
			try {
				rect = cropPanel.getRectangle();
			} catch (InvalidInputException e) {
				// Log.debug(e);
			}

			// metter à jour le formulaire si les donnees sont differentes
			if (Utils.safeEquals(rect, newRect) == false) {
				cropPanel.updateValuesWithoutFire(newRect);
			}

		}

	}

	/**
	 * Met à jour le gestionnaire d'import en fonction de la saisie de
	 * l'utilisateur
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextfieldListener extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {

			// recuperer les valeurs saisies
			Rectangle rect = null;
			try {
				rect = cropPanel.getRectangle();
			} catch (InvalidInputException e1) {
				return;
			}

			// changer si valeurs différentes
			if (rect.equals(configm.getCropRectangle()) == false) {
				configm.setCropRectangle(rect);
			}

		}

	}

	/**
	 * Lance la configuration visuelle pour dossier
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class VisualCropConfigLauncher implements ActionListener, Runnable {

		@Override
		public void actionPerformed(ActionEvent e) {
			ThreadManager.runLater(this);
		}

		@Override
		public void run() {

			try {
				importm.startCropAreaConfiguration(mode);
			}

			catch (IOException | MapImportException e1) {

				// arreter la configuration
				importm.stopCropConfiguration();

				// message d'erreur
				guim.showErrorInDialog(
						guim.getMainWindow(),
						"Impossible de lancer la configuration visuelle."
								+ "<br>Si vous souhaitez importer à partir d'un dossier, vérifiez que le dossier "
								+ "existe et vérifiez qu'il contient bien des images.",
						false);
			}
		}

	}

}
