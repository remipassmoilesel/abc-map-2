package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.draw.SelectColors;
import org.abcmap.gui.toProcess.gui.ie.draw.SelectDrawCaracteristics;

public class GroupDrawingPalette extends InteractionElementGroup {

	public GroupDrawingPalette() {
		label = "Couleurs";
		blockIcon = GuiIcons.GROUP_COLOR_PALETTE;

		addInteractionElement(new SelectColors());
		addInteractionElement(new SelectDrawCaracteristics());
	}

}
