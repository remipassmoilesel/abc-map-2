package abcmap.utils.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.utils.Utils;

/**
 * Ouvre une boite parcourir et entre le chemin sélectionné dans le champs de
 * texte passé au constructeur.
 * <p>
 * Si fireEventOnChange == true une KeyEvent sera propagé.
 * 
 * @author remipassmoilesel
 *
 */
public class BrowsePathActionListener implements ActionListener {

	/** Le composant de texte à mettre à jour */
	private JTextComponent componentToUpdate;

	/** Si vrai inclure les fichiers dans la boite parcourir */
	private boolean includeFiles;

	/** Si vrai envoi un KeyEvent lors du changement du champs */
	private boolean fireEventOnChange;

	/**
	 * 
	 * @param componentToUpdate
	 * @param includeFiles
	 * @param fireEventOnChange
	 */
	public BrowsePathActionListener(JTextComponent componentToUpdate,
			boolean includeFiles, boolean fireEventOnChange) {

		this.includeFiles = includeFiles;
		this.componentToUpdate = componentToUpdate;
		this.fireEventOnChange = fireEventOnChange;

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// recuperer le parent
		Window parent = SwingUtilities.windowForComponent((Component) e
				.getSource());

		// demander le repertoire à importer
		final BrowseDialogResult bdr = includeFiles ? SimpleBrowseDialog
				.browseFileToOpen(parent, null) : SimpleBrowseDialog
				.browseDirectory(parent);

		// si l'utilisateur n'annule pas
		if (BrowseDialogResult.CANCEL.equals(bdr.getReturnVal()) == false) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					// recuperer le chemin du dossier
					String path = bdr.getFile().getAbsolutePath();

					// maj le composant texte
					GuiUtils.changeText(componentToUpdate, path);

					// propager une evenement si demandé
					if (fireEventOnChange) {
						KeyEvent ke = new KeyEvent(componentToUpdate,
								KeyEvent.KEY_RELEASED, System
										.currentTimeMillis(), 0,
								KeyEvent.VK_UNDEFINED, 'a');
						componentToUpdate.dispatchEvent(ke);
					}
				}
			});
		}
	}
}
