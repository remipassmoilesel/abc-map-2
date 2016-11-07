package abcmap.gui.menu;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import abcmap.gui.comps.buttons.HtmlMenuItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.GuiManager;
import abcmap.managers.stub.MainManager;

/**
 * Element de menu adapté aux barre de menu Swing. Lors d'un clic, affiche le
 * GUI primaire de l'element dans une boite de dialogue.
 * 
 * @author remipassmoilesel
 *
 */
public class DialogMenuItem extends HtmlMenuItem {

	private JDialog dialog;
	private GuiManager guim;

	public DialogMenuItem(InteractionElement elmt) {

		super(elmt.getLabel());

		guim = MainManager.getGuiManager();

		setIcon(elmt.getMenuIcon());
		setAccelerator(elmt.getAccelerator());
		addActionListener(new ShowDialogListener());

		// le dialogue d'affichage de l'element
		dialog = new JDialog(guim.getMainWindow());
		dialog.setModal(true);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);

		// le contenu du dialogue
		Component primary = elmt.getPrimaryGUI();
		if (primary == null) {
			throw new NullPointerException("Gui is null");
		}

		JPanel contentPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
		contentPane.add(primary);

		dialog.pack();
	}

	private class ShowDialogListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}

	}

}
