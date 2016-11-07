package abcmap.gui.dialogs.simple;

import java.awt.Window;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Boite de dialogue d'information. <br>
 * Changement possible des boutons via la surcharge de getButtons <br>
 * Changement possible de la largeur via setWidth <br>
 * Penser a appeler construct()
 * 
 * @author remipassmoilesel
 *
 */
public class InformationTextFieldDialog extends SimpleInformationDialog {

	protected String textFieldValue;
	private JTextField textField;

	public static void showLater(final Window parent, final String message,
			final String textFieldValue) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				InformationTextFieldDialog itd = new InformationTextFieldDialog(parent, message,
						textFieldValue);
				itd.setVisible(true);

			}
		});
	}

	public InformationTextFieldDialog(Window parent, String message, String textFieldValue) {
		super(parent);

		setMessage(message);
		setTextFieldValue(textFieldValue);

		reconstruct();
	}

	public void setTextFieldValue(String textFieldValue) {
		this.textFieldValue = textFieldValue;
	}

	public void reconstruct() {

		super.reconstruct();

		// retirer les boutons
		contentPane.remove(getButtonsPanel());

		// ajouter le hamps de texte
		textField = new JTextField();
		textField.setEditable(false);
		textField.setText(textFieldValue);

		contentPane.add(textField, "width 80%");

		// remettre les boutons par défaut
		addDefaultButtons();

		contentPane.revalidate();
		contentPane.repaint();

		pack();

		// valider pour avoir dimensions
		setLocationRelativeTo(null);

	}

}
