package abcmap.gui.comps.fileselection;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

/**
 * Panneau affichant une liste de fichiers sous forme de boutons avecune action
 * possible.
 * 
 * @author remipassmoilesel
 *
 */
public class FileSelectionPanel extends JPanel {

	/** Le modele de la liste d'affichage des fichiers */
	private DefaultListModel<File> filesModel;

	/** La liste d'affichage des fichiers */
	private JList<File> jlist;

	/** Le bouton d'effacement de la liste */
	private JButton resetButton;

	/** Le bouton d'action */
	private JButton actionButton;

	/** Le fichier actuellement sélectionné */
	private File activeFile;

	public FileSelectionPanel() {

		// layout manager
		super(new MigLayout("insets 5"));

		// fichier actif
		this.activeFile = null;

		// le modele de la liste dynamique de fichiers
		this.filesModel = new DefaultListModel<File>();

		// liste dynamique de calques
		jlist = new JList<File>(filesModel);
		jlist.setAlignmentY(Component.TOP_ALIGNMENT);
		jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
		jlist.setBorder(BorderFactory.createLineBorder(Color.gray));
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jlist.setVisibleRowCount(5);
		jlist.setCellRenderer(new FileSelectionRenderer());
		jlist.addListSelectionListener(new SelectionListener());

		// la liste est dans un scroll pane
		JScrollPane sp = new JScrollPane(jlist);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(sp, "span, width 90%, height 130px!, wrap 8px");

		// bouton d'action
		actionButton = new JButton("Ouvrir");
		add(actionButton);

		// bouton de reset
		resetButton = new JButton("Effacer l'historique");
		add(resetButton, "wrap");

	}

	/**
	 * Construire un panneau de selection avec une liste de fichiers
	 * 
	 * @param files
	 */
	public FileSelectionPanel(Collection<File> files) {
		this();
		addFiles(files);
	}

	/**
	 * Change le fichier actif en fonction de la selectionde l'utilisateur.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {

			// le fichier selectionné
			File selection = jlist.getSelectedValue();
			if (selection != null && selection.equals(activeFile) == false) {
				activeFile = selection;
			}

		}

	}

	/**
	 * Ajouter un gestionnaire d'action au bouton d'action (Texte "Ouvrir" par
	 * défaut)
	 * 
	 * @param al
	 */
	public void addActionButtonListener(ActionListener al) {
		actionButton.addActionListener(al);
	}

	/**
	 * Ajouter un gestionnaire d'action au bouton d'effacement de la liste
	 * 
	 * @param al
	 */
	public void addResetButtonListener(ActionListener al) {
		resetButton.addActionListener(al);
	}

	/**
	 * Vide la liste et la repeint
	 */
	public void clearFileList() {
		filesModel.clear();

		// rafraichir la liste
		jlist.revalidate();
		jlist.repaint();
	}

	/**
	 * Ajoute les fichiers et repeint la liste
	 * 
	 * @param files
	 */
	public void addFiles(Collection<File> files) {

		// ajouter les elements au modele
		for (File f : files) {
			filesModel.addElement(f);
		}

		// rafraichir la liste
		jlist.revalidate();
		jlist.repaint();

	}

	/**
	 * Retourne le fichier sélectionné ou null
	 * 
	 * @return
	 */
	public File getActiveFile() {
		return activeFile;
	}

}
