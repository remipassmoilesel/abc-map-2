package abcmap.gui.comps.importation;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.exceptions.InvalidInputException;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.gui.comps.textfields.IntegerTextField;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.ImportManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;

public class CropDimensionsPanel extends JPanel {

	private HtmlCheckbox chkActivateCropping;
	private IntegerTextField txtX;
	private IntegerTextField txtY;
	private IntegerTextField txtW;
	private IntegerTextField txtH;
	private JButton btnVisualConfig;
	private JButton btnCloseWindow;
	private Mode mode;
	private IntegerTextField[] allTextFields;
	private TextFieldEnablerAL textFieldEnabler;
	private CropActivationAL listener;
	private ImportManager importm;
	private ConfigurationManager confm;

	public enum Mode {
		WITH_CLOSE_WINDOW_BUTTON, WITH_VISUAL_CONFIG_BUTTON
	}

	public CropDimensionsPanel(Mode mode) {

		super(new MigLayout("insets 0"));

		this.mode = mode;

		this.importm = MainManager.getImportManager();
		this.confm = MainManager.getConfigurationManager();

		// changement de l'activation des champs en fonctiondes clics
		this.textFieldEnabler = new TextFieldEnablerAL();

		// checkbox d'activation du recadrage
		chkActivateCropping = new HtmlCheckbox("Activer le recadrage");
		chkActivateCropping.setSelected(true);

		add(chkActivateCropping, "span, growx, wrap 10px");
		chkActivateCropping.addActionListener(textFieldEnabler);

		// champs de saisie des données
		txtX = new IntegerTextField(5);
		txtY = new IntegerTextField(5);
		txtW = new IntegerTextField(5);
		txtH = new IntegerTextField(5);
		this.allTextFields = new IntegerTextField[] { txtX, txtY, txtW, txtH };

		// panneau de saisie des données
		GuiUtils.addLabel("Zone à conserver: ", this, "span, wrap");

		JPanel panel2 = new JPanel(new MigLayout("insets 5 10 5 5"));

		GuiUtils.addLabel("x: ", panel2);
		panel2.add(txtX, "gapright 10px");

		GuiUtils.addLabel("y: ", panel2);
		panel2.add(txtY, "span, wrap");

		GuiUtils.addLabel("w: ", panel2);
		panel2.add(txtW);

		GuiUtils.addLabel("h: ", panel2);
		panel2.add(txtH, "span, wrap 8px");

		// panneau d'informations
		panel2.add(new JLabel(GuiIcons.CROP_INFORMATIONS), "span, align center");

		add(panel2, "wrap 10px");

		// boutons, plusieurs modes possibles
		btnVisualConfig = new JButton("Configurer visuellement");
		btnCloseWindow = new JButton("Fermer cette fenêtre");

		// ajouter les boutons en fonction du mode désiré
		construct(mode);

		revalidate();
		repaint();

	}

	public void construct(Mode mode) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// enlever les boutons
		remove(btnVisualConfig);
		remove(btnCloseWindow);

		// rajouter ceux concernés
		if (Mode.WITH_CLOSE_WINDOW_BUTTON.equals(mode)) {
			add(btnCloseWindow, "span, align center, wrap");
		}

		else {
			add(btnVisualConfig, "span, align center, wrap");
		}
	}

	public HtmlCheckbox getChkActivateCropping() {
		return chkActivateCropping;
	}

	private class TextFieldEnablerAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// valeur à appliquer
			boolean val = chkActivateCropping.isSelected();

			// appliquer aux champs de texte
			setTextfieldsEnabled(val);

		}

	}

	/**
	 * Dans cet ordre: x, y, w, h
	 * 
	 * @return
	 */
	public IntegerTextField[] getTextFields() {
		return allTextFields;
	}

	/**
	 * Dans cet ordre: x, y, w, h
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	public Integer[] getValues() throws InvalidInputException {

		Integer[] values = new Integer[4];
		int i = 0;
		for (IntegerTextField itf : allTextFields) {
			values[i] = itf.getIntegerValue();
			i++;
		}

		return values;
	}

	public Rectangle getRectangle() throws InvalidInputException {

		Rectangle r = new Rectangle();
		r.x = allTextFields[0].getIntegerValue();
		r.y = allTextFields[1].getIntegerValue();
		r.width = allTextFields[2].getIntegerValue();
		r.height = allTextFields[3].getIntegerValue();

		return r;
	}

	/**
	 * Met a jour les champs de texte sans lancer d'evenement
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void updateValuesWithoutFire(int x, int y, int w, int h) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		int[] values = new int[] { x, y, w, h };
		for (int i = 0; i < allTextFields.length; i++) {

			// champs a modifier
			IntegerTextField txtField = allTextFields[i];

			// insertion si necessaire uniquement
			if (txtField.isFocusOwner() == false) {
				GuiUtils.changeText(txtField, String.valueOf(values[i]));
			}
		}

	}

	/**
	 * Met à jour checkbox et active les champs de texte en conséquence sans
	 * lancer d'évenemenent
	 * 
	 * @param val
	 */
	public void updateChkCroppingWithoutFire(boolean val) {
		if (chkActivateCropping.isSelected() != val) {
			GuiUtils.setSelected(chkActivateCropping, val);
			setTextfieldsEnabled(val);
		}
	}

	public void updateValuesWithoutFire(Rectangle r) {
		updateValuesWithoutFire(r.x, r.y, r.width, r.height);
	}

	public JButton getBtnCloseWindow() {
		return btnCloseWindow;
	}

	public JButton getBtnVisualConfig() {
		return btnVisualConfig;
	}

	public void addListener(KeyListener listener) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		for (IntegerTextField itf : allTextFields) {
			KeyListenerUtil.addListener(itf, listener);
		}
	}

	public void removeDocumentListener(KeyListener listener) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		for (IntegerTextField itf : allTextFields) {
			KeyListenerUtil.removeListener(itf, listener);
		}
	}

	public void setTextfieldsEnabled(boolean val) {

		// pas d'action hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		for (IntegerTextField itf : allTextFields) {
			if (itf.isEnabled() != val) {
				itf.setEnabled(val);
			}
		}
	}

	public void activateCroppingListener(boolean value) {

		// active le listener
		if (value) {
			listener = new CropActivationAL();
			chkActivateCropping.addActionListener(listener);
		}

		else {
			if (listener != null) {
				chkActivateCropping.removeActionListener(listener);
			}
		}

	}

	private class CropActivationAL implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// recuperer la valeur saisie
			boolean val = ((HtmlCheckbox) e.getSource()).isSelected();

			if (confm.isCroppingEnabled() != val) {
				confm.setCroppingEnabled(val);
			}
		}

	}

}
