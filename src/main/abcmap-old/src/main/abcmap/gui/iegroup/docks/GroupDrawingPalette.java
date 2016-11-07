package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.draw.SelectColors;
import abcmap.gui.ie.draw.SelectDrawCaracteristics;
import abcmap.gui.iegroup.InteractionElementGroup;

public class GroupDrawingPalette extends InteractionElementGroup {

	public GroupDrawingPalette() {
		label = "Couleurs";
		blockIcon = GuiIcons.GROUP_COLOR_PALETTE;

		// selecteur de couleur
		addInteractionElement(new SelectColors());
		addInteractionElement(new SelectDrawCaracteristics());
	}

}
