package abcmap.gui.dialogs;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.gui.dialogs.simple.SimpleQuestionDialog;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import net.miginfocom.swing.MigLayout;

/**
 * Dialogue de recuperation de projet. A revoir entierement.
 * 
 * 
 * TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
 * 
 * @author remipassmoilesel
 *
 */
public class RecoverProjectsDialog extends JDialog {

	public static final String RECOVERY = "RECOVERY";
	public static final String DELETE = "DELETE";
	public static final String CONTINUE = "CONTINUE";

	private String userAction;
	private File directory;
	private JButton btnContinue;

	public RecoverProjectsDialog(Window parent) {
		super(parent);

		// action de l'utilisateur
		this.userAction = null;
		this.directory = null;

		// proprietes du dialog
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CustomWindowListener());
		this.setModal(true);
		this.setLocationRelativeTo(null);
		this.setTitle("Récupération de projets");

		// content pane
		JPanel mainPanel = new JPanel(new MigLayout());

		// titre
		GuiUtils.addLabel("Récupération de projets", mainPanel, "wrap");

		// explication
		GuiUtils.addLabel("Un ou plusieurs projets ont été ... Vous pouvez:",
				mainPanel, "wrap");

		// bouton récupérer
		JButton btnRecover = new JButton("Récupérer les projets");
		btnRecover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userAction = RECOVERY;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						BrowseDialogResult bdr = SimpleBrowseDialog
								.browseDirectory(null);
						if (Utils.safeEquals(bdr.getReturnVal(),
								BrowseDialogResult.APPROVE)) {
							directory = bdr.getFile();
							userAction = RECOVERY;
							dispose();
						}
					}
				});
			}
		});
		mainPanel.add(btnRecover, "wrap");

		// bouton supprimer
		JButton btnDelete = new JButton("Supprimer les projets");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						QuestionResult sqr = SimpleQuestionDialog.askQuestion(
								RecoverProjectsDialog.this, "Etes vous sur ?");
						if (Utils.safeEquals(sqr.getReturnVal(),
								QuestionResult.YES)) {
							userAction = DELETE;
							dispose();
						}
					}
				});
			}
		});
		mainPanel.add(btnDelete, "wrap");

		// bouton continuer
		btnContinue = new JButton("Continuer et décider plus tard");
		btnContinue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userAction = CONTINUE;
				dispose();
			}
		});
		mainPanel.add(btnContinue, "wrap");

		setContentPane(mainPanel);

	}

	public String getUserAction() {
		return userAction;
	}

	public File getDirectory() {
		return directory;
	}

	private class CustomWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			btnContinue.doClick();
		}
	}

}
