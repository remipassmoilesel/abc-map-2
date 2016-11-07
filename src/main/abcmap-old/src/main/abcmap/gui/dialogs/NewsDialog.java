package abcmap.gui.dialogs;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.ie.ressources.GoToAskFormPage;
import abcmap.gui.ie.ressources.GoToWebSite;
import net.miginfocom.swing.MigLayout;

public class NewsDialog extends JDialog {

	private JEditorPane editorPane;

	public NewsDialog(Window parent) {
		super(parent);

		// proprietes du dialog
		setTitle("Abc-Map version " + ConfigurationConstants.SOFTWARE_VERSION);
		this.setSize(new Dimension(500, 400));
		this.setLocationRelativeTo(parent);
		this.setModal(true);

		// content pane
		JPanel mainPanel = new JPanel(new MigLayout());

		// panneau d'affichage
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		mainPanel.add(editorPane);

		// boutons d'action
		JButton btnGoTo = new JButton("Visiter le site");
		btnGoTo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				GoToWebSite gtw = new GoToWebSite();
				gtw.actionPerformed(e);
			}
		});

		JButton btnCancel = new JButton("Fermer cette fenêtre");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		mainPanel.add(btnGoTo);
		mainPanel.add(btnCancel);

		setContentPane(mainPanel);
		pack();
	}

	public void setContent(String t) {
		editorPane.setText(t);
	}

}
