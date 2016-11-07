package abcmap.gui.dock.comps.blockitems;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import abcmap.gui.GuiCursor;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.ie.ModelInteractionElement;
import abcmap.utils.threads.ThreadManager;

public class ClickableBlockItem extends SimpleBlockItem {

	private static final String CLICKABLE_MENU_ITEM_ACTION = "CLICKABLE_MENU_ITEM_ACTION";

	public static ClickableBlockItem create(InteractionElement elmt) {
		ClickableBlockItem smi = new ClickableBlockItem(elmt);
		smi.reconstruct();
		return smi;
	}

	private AbstractAction fireAction;

	public ClickableBlockItem(InteractionElement elmt) {
		super(elmt);

		// caracteristiques
		this.changeColorUnderFocus(true);
		this.setOpaque(true);
		this.labelName.setCursor(GuiCursor.HAND_CURSOR);

		// enclencher l'action lors d'un clic
		this.fireAction = new FireAction();
		setFocusable(true);

		// enclencher l'acton à l'aide de la barre espace
		this.getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke("SPACE"),
				CLICKABLE_MENU_ITEM_ACTION);
		this.getActionMap().put(CLICKABLE_MENU_ITEM_ACTION, fireAction);

		// souris et focus
		this.labelName.addMouseListener(new ItemActionListener());
	}

	/**
	 * Objet activé lors d'une action
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class FireAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			ThreadManager.runLater(interactionElmt);
		}
	};

	private class ItemActionListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			fireAction.actionPerformed(new ActionEvent(ClickableBlockItem.this,
					ActionEvent.ACTION_PERFORMED, CLICKABLE_MENU_ITEM_ACTION));
		}

	}

}
