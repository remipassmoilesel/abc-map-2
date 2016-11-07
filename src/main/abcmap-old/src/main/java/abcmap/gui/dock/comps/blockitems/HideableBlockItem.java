package abcmap.gui.dock.comps.blockitems;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import abcmap.gui.GuiCursor;
import abcmap.gui.ie.InteractionElement;

public class HideableBlockItem extends SimpleBlockItem {

	public static HideableBlockItem create(InteractionElement ie, Component bottom, boolean bottomIsVisible) {
		HideableBlockItem smi = new HideableBlockItem(ie);
		smi.setBottomComponent(bottom);
		smi.setBottomComponentVisible(bottomIsVisible);
		smi.reconstruct();
		return smi;
	}

	// private JLabel hideButton;
	private boolean bottomElementVisible = true;
	private HideActionListener hideActionListener;

	public HideableBlockItem(InteractionElement elmt) {
		super(elmt);

		changeColorUnderFocus(true);

		// element de masquage du composant bas
		this.hideActionListener = new HideActionListener();
		labelName.addMouseListener(hideActionListener);

		// bouton de masquage
		// hideButton = new JLabel(GuiIcons.DI_HIDE_ITEM);
		// hideButton.addMouseListener(hideActionListener);
		// hideButton.setCursor(GuiCursor.HAND_CURSOR);

		// enlever tout
		// removeAll();

		// // rajout des elements
		// add(labelName, "width 81%!");
		// add(hideButton);
		// add(buttonHelp, "wrap");

		// curseur main
		labelName.setCursor(GuiCursor.HAND_CURSOR);

		reconstruct(helpVisible);

	}

	@Override
	public void reconstruct(boolean showHelp) {
		super.reconstruct(showHelp);

		// masquer l'element
		if (bottomElementVisible == false) {
			if (bottomComponent != null)
				remove(bottomComponent);
			// if (hideButton != null)
			// hideButton.setIcon(GuiIcons.DI_SHOW_ITEM);
		}

		// afficher tout
		else {
			// if (hideButton != null)
			// hideButton.setIcon(GuiIcons.DI_HIDE_ITEM);
		}

	}

	@Override
	public void reconstruct() {
		setBottomComponentVisible(bottomElementVisible);
		reconstruct(helpVisible);
	}

	public void refresh(boolean showHelp, boolean showBottomElement) {
		setBottomComponentVisible(showBottomElement);
		reconstruct(showHelp);
	}

	private class HideActionListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			bottomElementVisible = !bottomElementVisible;
			reconstruct(helpVisible);
		}

	}

	public void setBottomComponentVisible(boolean bottomElementVisible) {
		this.bottomElementVisible = bottomElementVisible;
	}

}
