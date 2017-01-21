package org.abcmap.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.draw.SelectColors;
import org.abcmap.ielements.draw.SelectDrawCaracteristics;

public class GroupDrawingPalette extends GroupOfInteractionElements {

	public GroupDrawingPalette() {
		label = "Couleurs";
		blockIcon = GuiIcons.GROUP_COLOR_PALETTE;

		addInteractionElement(new SelectColors());
		addInteractionElement(new SelectDrawCaracteristics());
	}

}
