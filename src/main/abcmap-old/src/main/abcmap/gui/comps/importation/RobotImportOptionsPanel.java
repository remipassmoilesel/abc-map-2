package abcmap.gui.comps.importation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;

import abcmap.exceptions.InvalidInputException;
import abcmap.gui.comps.textfields.DecimalTextField;
import abcmap.importation.robot.RobotConfiguration;
import abcmap.managers.Log;
import abcmap.utils.Utils;
import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau de saisie de parametres de capture automatique.
 * 
 * @author remipassmoilesel
 *
 */
public class RobotImportOptionsPanel extends JPanel implements
		HasListenerHandler<ActionListener> {

	private Integer[] predefinedCoverings = new Integer[] { 5, 10, 15, 20, 25,
			30 };

	private String wrap15;
	private String gapLeft;
	private RobotConfiguration[] predefinedConf;
	private DecimalTextField txtMovingDelay;
	private DecimalTextField txtHiddingDelay;
	private DecimalTextField txtCaptureDelay;
	private JComboBox<String> cbConfiguration;
	private JComboBox<Integer> cbCovering;

	private ListenerHandler<ActionListener> listenerHandler;

	private String[] predefinedConfNames;

	public RobotImportOptionsPanel() {

		super(new MigLayout("insets 0"));

		this.wrap15 = "wrap 15,";
		this.gapLeft = "gapleft 15px,";

		// gestion des evenements
		listenerHandler = new ListenerHandler<ActionListener>();

		// méthode d'import
		JRadioButton rdImportFromULC = new JRadioButton(
				"Commencer du coin haut gauche");
		rdImportFromULC.setSelected(true);
		JRadioButton rdImportFromMiddle = new JRadioButton(
				"Commencer du millieu");

		ButtonGroup bg = new ButtonGroup();
		bg.add(rdImportFromULC);
		bg.add(rdImportFromMiddle);

		// retransmettre les actions
		RadioEventReporter radioEventReporter = new RadioEventReporter();

		rdImportFromMiddle.addActionListener(radioEventReporter);
		rdImportFromULC.addActionListener(radioEventReporter);

		add(rdImportFromULC, "wrap");
		add(rdImportFromMiddle, wrap15);

		// réglages prédéfinis
		GuiUtils.addLabel("Réglages prédéfinis: ", this, "wrap");
		predefinedConf = RobotConfiguration.getPredefinedConfigurations();
		predefinedConfNames = RobotConfiguration
				.getPredefinedConfigurationNames();
		cbConfiguration = new JComboBox<>(predefinedConfNames);
		cbConfiguration.addActionListener(new TextfieldSettingsUpdater());
		add(cbConfiguration, gapLeft + wrap15);

		// couvertures entre les tuiles
		GuiUtils.addLabel("Couverture entre images: ", this, "wrap");
		cbCovering = new JComboBox<>(predefinedCoverings);
		cbCovering.addActionListener(new CoveringEventReporter());
		add(cbCovering, gapLeft + "split 2");
		GuiUtils.addLabel("%", this, gapLeft + wrap15);

		// reporter toutes les mdoificatins de champs texte
		TextFieldEventReporter textFieldReporter = new TextFieldEventReporter();

		String sLabel = "s.";

		// delai avant déplacement
		GuiUtils.addLabel("Délai avant déplacement:", this, "wrap");
		txtMovingDelay = new DecimalTextField(3);
		KeyListenerUtil.addListener(txtMovingDelay, textFieldReporter);
		add(txtMovingDelay, gapLeft + "split 2");
		GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

		// delai avant capture
		GuiUtils.addLabel("Délai avant capture de l'écran:", this, "wrap");
		txtCaptureDelay = new DecimalTextField(3);
		KeyListenerUtil.addListener(txtCaptureDelay, textFieldReporter);
		add(txtCaptureDelay, gapLeft + "split 2");
		GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

		// delai de masquage des fenêtres
		GuiUtils.addLabel("Délai avant masquage des fenêtres:", this, "wrap");
		txtHiddingDelay = new DecimalTextField(3);
		KeyListenerUtil.addListener(txtHiddingDelay, textFieldReporter);
		add(txtHiddingDelay, gapLeft + "split 2");
		GuiUtils.addLabel(sLabel, this, gapLeft + wrap15);

		setValues(RobotConfiguration.NORMAL_IMPORT);
	}

	/**
	 * Lors d'une action sur le combo de couverture, transmettre un evenement
	 * puis mettre à jour le combo
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class CoveringEventReporter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// changer le combo et sauver les parametres
			changeToCustomSettingsSetAndSave();

			fireEvent();
		}

	}

	/**
	 * Lors d'une action sur un bouton radio, transmettre un evenement.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class RadioEventReporter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			fireEvent();
		}

	}

	private void fireEvent() {
		listenerHandler.fireEvent(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, null));
	}

	/**
	 * Lorsque qu'un champs de texte est modifié, envoi un evenement aux
	 * observateurs du formulaire et met à jour le combo de choix de set de
	 * réglage
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldEventReporter extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {

			// changer le combo et sauver les parametres
			changeToCustomSettingsSetAndSave();

			// avertir les observateurs
			fireEvent();

		}

	}

	/**
	 * Lors d'une modification de réglage, change le combo en
	 * "parametres personnalisé" si nécéssaire et sauvegarde les modifications
	 * dans les parametres personnalisé, pour que l'utilisateur puisse les
	 * retrouver.
	 */
	private void changeToCustomSettingsSetAndSave() {

		// mettre le combo en position parametres personnalisés
		if (cbConfiguration.getSelectedIndex() != RobotConfiguration.CUSTOM_SETTINGS_INDEX) {
			GuiUtils.changeWithoutFire(
					cbConfiguration,
					predefinedConfNames[RobotConfiguration.CUSTOM_SETTINGS_INDEX]);
		}

		// mettre à jour la configuration personnalisée
		try {
			RobotConfiguration.CUSTOM_IMPORT.update(getValues());
		} catch (InvalidInputException e) {
			// Log.debug(e);
		}
	}

	/**
	 * Met à jour les champs de texte lorsque l'utilisateur sélectionne un set
	 * de parametres prédéfini
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextfieldSettingsUpdater implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ev) {

			// récupérer la configuration sélectionnée
			RobotConfiguration rconf = predefinedConf[cbConfiguration
					.getSelectedIndex()];

			setValues(rconf);
		}

	}

	/**
	 * Affiche une configuration et notifie les observateurs
	 * 
	 * @param rconf
	 */
	public void setValues(RobotConfiguration rconf) {

		GuiUtils.throwIfNotOnEDT();

		// mettre à jour la configuration personnalisée
		RobotConfiguration.CUSTOM_IMPORT.update(rconf);

		// delai avant capture
		String captureDelay = String.valueOf(rconf.getCaptureDelay() / 1000f);
		if (txtCaptureDelay.isFocusOwner() == false) {
			GuiUtils.changeText(txtCaptureDelay, captureDelay);
		}

		// delai avant deplacement
		String movingDelay = String.valueOf(rconf.getMovingDelay() / 1000f);
		if (txtMovingDelay.isFocusOwner() == false) {
			GuiUtils.changeText(txtMovingDelay, movingDelay);
		}

		// delai de masquage
		String hiddingDelay = String.valueOf(rconf.getHiddingDelay() / 1000f);
		if (txtHiddingDelay.isFocusOwner() == false) {
			GuiUtils.changeText(txtHiddingDelay, hiddingDelay);
		}

		// taux de couverture
		int covering = Math.round(rconf.getCovering() * 100);

		if (Utils.safeEquals(cbCovering.getSelectedItem(), covering) == false) {
			GuiUtils.changeWithoutFire(cbCovering, covering);
		}

		// notification
		fireEvent();
	}

	/**
	 * Retourne un objet de configuration contenant les paramètres sélectionnés
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	public RobotConfiguration getValues() throws InvalidInputException {

		// récupérer la configuration sélectionnée
		RobotConfiguration rconf = new RobotConfiguration("SELECTED - "
				+ cbConfiguration.getSelectedItem());

		try {
			rconf.setCaptureDelay(getCaptureDelayMs());
			rconf.setCovering(getCovering());
			rconf.setMovingDelay(getMovingDelayMs());
			rconf.setHiddingDelay(getHiddingDelayMs());
		} catch (Exception e) {
			// Log.debug(e);
			throw new InvalidInputException(e);
		}

		return rconf;

	}

	/**
	 * Retourne le delai de masquage saisi
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	private Integer getHiddingDelayMs() throws InvalidInputException {
		return Math.round(txtHiddingDelay.getFloatValue() * 1000);
	}

	/**
	 * Retourne le taux de recouvrement saisi
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	private float getCovering() throws InvalidInputException {
		try {
			return ((Integer) cbCovering.getSelectedItem()) / 100f;
		} catch (Exception e) {
			Log.debug(e);
			throw new InvalidInputException();
		}
	}

	/**
	 * Retourne le delai avant mouvement saisi
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	private int getMovingDelayMs() throws InvalidInputException {
		return Math.round(txtMovingDelay.getFloatValue() * 1000);
	}

	/**
	 * Retourne le delai avant capture saisi
	 * 
	 * @return
	 * @throws InvalidInputException
	 */
	private int getCaptureDelayMs() throws InvalidInputException {
		return Math.round(txtCaptureDelay.getFloatValue() * 1000);
	}

	@Override
	public ListenerHandler<ActionListener> getListenerHandler() {
		return listenerHandler;
	}

}
