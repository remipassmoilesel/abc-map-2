package abcmap.gui.dialogs.simple;

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.GuiUtils;

/**
 * Boite de dialogue d'information. <br>
 * Changement possible des boutons via la surcharge de getButtons <br>
 * Changement possible de la largeur via setWidth <br>
 * Penser a appeler construct()
 * 
 * @author remipassmoilesel
 *
 */
public class SimpleInformationDialog extends JDialog implements Refreshable {

	/** Le titre de la fenêtre */
	protected String dialogTitle;

	/** L'icone de la fenetre */
	protected ImageIcon largeIcon;

	/** Le message principal de la fenetre */
	protected String message;

	/** Les dimensions de la fenetre */
	protected Dimension dimensions;

	protected JPanel contentPane;

	/** Le panneau qui contient les boutons */
	protected JPanel buttonsPanel;

	public SimpleInformationDialog(Window parent) {
		super(parent);

		// ecouter la fermeture pour conserver le résultat
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new CustomWindowListener());

		// dialog modale
		this.setModal(true);
		this.setModalityType(ModalityType.APPLICATION_MODAL);

		// titre par defaut
		this.dialogTitle = "Information";

		// icone par defaut
		this.largeIcon = GuiIcons.DIALOG_INFORMATION_ICON;

		// message par defaut
		this.message = "Lorem ipsum ....";

		reconstruct();

	}

	@Override
	public void reconstruct() {

		// pas d'actions hors de l'EDT
		GuiUtils.throwIfNotOnEDT();

		// titre du dialog
		setTitle(dialogTitle);

		// contenu
		contentPane = new JPanel(new MigLayout("insets 5"));

		// icone
		JLabel iconLbl = new JLabel(largeIcon);
		iconLbl.setVerticalAlignment(SwingConstants.TOP);
		contentPane.add(iconLbl,
				"west, gapleft 10px, gaptop 10px, gapright 15px,");

		// titre
		HtmlLabel title = new HtmlLabel(dialogTitle);
		title.setStyle(GuiStyle.DIALOG_TITLE_1);
		contentPane.add(title, "gaptop 10px, wrap 10px");

		// message
		// prendre en compte les chnagements de police dans le JEditorPane
		JEditorPane messageArea = new JEditorPane("text/html", message);
		messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
				Boolean.TRUE);
		GuiStyle.applyStyleTo(GuiStyle.DIALOG_TEXT, messageArea);

		messageArea.setOpaque(false);
		messageArea.setEditable(false);
		contentPane.add(messageArea, "width 300px!, wrap 10px,");

		// boutons
		addDefaultButtons();

		// taille et assignation
		setContentPane(contentPane);

		pack();

		// valider pour avoir dimensions
		setLocationRelativeTo(null);

		refresh();

	}

	/**
	 * Retourne un JPanel avec les boutons par défaut
	 * 
	 * @return
	 */
	protected void addDefaultButtons() {

		buttonsPanel = new JPanel(new MigLayout("insets 5"));

		JButton hideButton = new JButton("Masquer ce message");
		hideButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonsPanel.add(hideButton, "align right");

		// ajouter au contenu
		contentPane.add(buttonsPanel, "align right, gapright 15px, wrap 15px,");
	}

	/**
	 * Affecter un message à la fenêtre. Le message sera encadré de balises
	 * HTML.
	 * 
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = "<html>" + message + "</html>";
	}

	@Override
	public void setTitle(String title) {
		super.setTitle(title);
		dialogTitle = title;
	}

	private class CustomWindowListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	protected JPanel getButtonsPanel() {
		return buttonsPanel;
	}

	@Override
	public void refresh() {
		this.revalidate();
		this.repaint();
	}

}
