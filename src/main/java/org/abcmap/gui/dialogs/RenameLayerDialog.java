package org.abcmap.gui.dialogs;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

		this.setTitle("Renommer le calque");
		this.setModal(true);
		this.setSize(new Dimension(255, 135));
		this.setLocationRelativeTo(null);

		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CustomWindowListener());

		this.input = null;

		JPanel mainPanel = new JPanel(new MigLayout());

		GuiUtils.addLabel("Nouveau nom du calque: ", mainPanel, "wrap");

		txtName = new JTextField();
		KeyAdapter.addListener(txtName, new LayerNameChecker());
		mainPanel.add(txtName, "width 150px!");

		btnOk = new JButton("Renommer");
		btnOk.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				validateAndExit();
			}
		});
		btnOk.setEnabled(false);
		mainPanel.add(btnOk);

		btnCancel = new JButton("Annuler");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cancelAndExit();
			}
		});
		mainPanel.add(btnCancel);

		txtName.setText(name);

		setContentPane(mainPanel);
		pack();
	}

	public class LayerNameChecker extends KeyAdapter {

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
