package abcmap.gui.windows.crop;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.color.ColorDialogButton;
import abcmap.gui.comps.color.ColorEvent;
import abcmap.gui.comps.color.ColorEventListener;
import abcmap.gui.comps.importation.CropDimensionsPanel;
import abcmap.gui.comps.importation.CropDimensionsPanel.Mode;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

/**
 * Boite de dialogue liée à la fenete de sélection de zone de recadrage. Cette
 * boite de dialoguqe affiche les valeurs numeriques.
 * 
 * @author remipassmoilesel
 *
 */
public class CropDimensionsDialog extends JDialog {

	private static final int MARGIN = 20;
	private CropDimensionsPanel cropPanel;
	private CropConfigurationWindow cropWindow;
	private GuiManager guim;
	private ConfigurationManager configm;
	private ImportManager importm;

	public CropDimensionsDialog(CropConfigurationWindow cropwin) {
		super();

		this.guim = MainManager.getGuiManager();
		this.configm = MainManager.getConfigurationManager();
		this.importm = MainManager.getImportManager();

		// icone du logiciel
		guim.setWindowIconFor(this);

		this.cropWindow = cropwin;

		// proprietes par defaut
		this.setModal(false);
		this.setTitle("Recadrage");
		this.setAlwaysOnTop(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		// inutile car ajouté dans la fenetre
		// this.addWindowListener(closeWL);

		// panneau principal
		JPanel contentPane = new JPanel(new MigLayout("insets 15px, gap 8px"));
		this.setContentPane(contentPane);

		// bouton de changement de couleur du tracé
		GuiUtils.addLabel("Couleur du tracé: ", this, "wrap",
				GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

		ColorDialogButton colorButton = new ColorDialogButton();
		colorButton.getListenerHandler().add(new ColorbuttonListener());

		contentPane.add(colorButton, "gapleft 10px, wrap 15px");

		// panneau avec valeurs de recadrage
		cropPanel = new CropDimensionsPanel(Mode.WITH_CLOSE_WINDOW_BUTTON);

		// action des boutons
		cropPanel.activateCroppingListener(true);
		cropPanel.getBtnCloseWindow().addActionListener(new CloseListener());

		GuiUtils.addLabel("Valeurs de recadrage: ", this, "wrap",
				GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);

		contentPane.add(cropPanel, "gapleft 10px, wrap 10px");

		// ecouter les changements dans le panneau de saisie
		cropPanel.addListener(new TextfieldListener());

		this.pack();
	}

	public void moveToDefaultPosition() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dial = this.getSize();
		Point loc = new Point(screen.width - dial.width - MARGIN, MARGIN);
		this.setLocation(loc);
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		refresh();
	}

	public void refresh() {

		// mettre à jour les champs texte
		cropPanel.updateValuesWithoutFire(configm.getCropRectangle());

		// mettre a jour la checkbox
		cropPanel.updateChkCroppingWithoutFire(configm.isCroppingEnabled());

		cropPanel.repaint();
	}

	public CropDimensionsPanel getCropPanel() {
		return cropPanel;
	}

	/**
	 * Action sur le bouton fermer la fenetre
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CloseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			cropWindow.setVisible(false);
		}
	}

	/**
	 * Change la couleur du tracé en fonction de la saisie de l'utilisateur
	 * 
	 * @author remipassmoilesel
	 */
	private class ColorbuttonListener implements ColorEventListener {

		@Override
		public void colorChanged(ColorEvent c) {

			// changer la couleur du tracé
			CropSelectionRectangle selection = cropWindow.getSelection();
			selection.setColor(c.getColor());

			// rafraichir
			selection.refreshShape();
			cropWindow.refreshImagePane();

		}

	}

	/**
	 * Ecouter les champs de texte et enregistrer les valeurs saisies
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

}
