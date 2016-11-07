package abcmap.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

import abcmap.utils.gui.KeyListenerUtil;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

public class RenameLayerDialog extends JDialog {

	private static final String VALID_NAME = "^( *[^ ]+ *)+$";

	public static final String ACTION_VALIDATED = "ACTION_VALIDATED";
	public static final String ACTION_CANCELED = "ACTION_CANCELED";

	private JButton btnOk;
	private String input;
	private JTextField txtName;
	private JButton btnCancel;
	private String action = null;

	public RenameLayerDialog(Component parent) {
		this(parent, "");
	}

	public RenameLayerDialog(Component parent, String name) {

		GuiUtils.throwIfNotOnEDT();

		// propriétés du dialog
		this.setTitle("Renommer le calque");
		this.setModal(true);
		this.setSize(new Dimension(255, 135));
		this.setLocationRelativeTo(null);

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CustomWindowListener());

		// saisie utilisateur
		this.input = null;

		// panneau principal
		JPanel mainPanel = new JPanel(new MigLayout());

		// saisie du nom
		GuiUtils.addLabel("Nouveau nom du calque: ", mainPanel, "wrap");

		// zone de saisie du nom
		txtName = new JTextField();
		KeyListenerUtil.addListener(txtName, new LayerNameChecker());
		mainPanel.add(txtName, "width 150px!");

		// bouton valider
		btnOk = new JButton("Renommer");
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				validateAndExit();
			}
		});
		btnOk.setEnabled(false);
		mainPanel.add(btnOk);

		// bouton annuler
		btnCancel = new JButton("Annuler");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelAndExit();
			}
		});
		mainPanel.add(btnCancel);

		// initialisation
		txtName.setText(name);

		setContentPane(mainPanel);
		pack();
	}

	public class LayerNameChecker extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {
			boolean test = txtName.getText().matches(VALID_NAME);
			btnOk.setEnabled(test);
		}
	}

	public void validateAndExit() {
		this.action = ACTION_VALIDATED;
		this.input = txtName.getText();
		this.dispose();
	}

	public void cancelAndExit() {
		this.action = ACTION_CANCELED;
		this.input = null;
		this.dispose();
	}

	public String getInput() {
		return input;
	}

	public String getAction() {
		return action;
	}

	private class CustomWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			cancelAndExit();
		}
	}

}
