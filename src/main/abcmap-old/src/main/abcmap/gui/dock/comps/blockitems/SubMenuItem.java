package abcmap.gui.dock.comps.blockitems;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import abcmap.gui.GuiCursor;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.GuiUtils;

public class SubMenuItem extends SimpleBlockItem {

	/** Le groupe d'interaction affiché par l'element */
	private InteractionElementGroup interactionGroup;

	/** Le panneau listant les sous elements du menu */
	protected DockMenuPanel subMenu;

	/** Le dock parent ou sera affiché le sous menu */
	private Dock dock;

	public SubMenuItem(InteractionElementGroup group) {
		super(group);

		this.interactionGroup = group;

		// changer de couleur sous la souris
		changeColorUnderFocus(true);

		// ecouter les clics
		labelName.addMouseListener(new SubMenuMouseListener());

		// style
		setCursor(GuiCursor.HAND_CURSOR);

		reconstruct();
		refresh();
	}

	@Override
	public void reconstruct() {
		super.reconstruct();

		// construire le sous menu
		subMenu = new DockMenuPanel();

		// renommer le sous menu au nom de l'element
		subMenu.setMenuTitle(interactionGroup.getLabel());

		for (InteractionElement e : interactionGroup.getElements()) {
			subMenu.addMenuElement(e);
		}

		subMenu.reconstruct();
	}

	public void setInteractionGroup(InteractionElementGroup interactionGroup) {
		this.interactionGroup = interactionGroup;
	}

	/**
	 * Montrer le sous menu
	 */
	private void showSubMenu() {

		GuiUtils.throwIfNotOnEDT();

		// rechercher le parent si necessaire
		if (dock == null) {
			dock = Dock.getDockParentForComponent(this);
		}

		// montrer le sous menu
		dock.showWidgetspace(subMenu);
	}

	/**
	 * Montrer le sous menu lors d'un clic
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class SubMenuMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent e) {
			showSubMenu();
		}

	}

}
