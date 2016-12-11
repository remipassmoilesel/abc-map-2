package org.abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.draw.SelectDrawingTool;
import abcmap.gui.ie.draw.ShowToolHelp;
import abcmap.gui.ie.draw.ShowToolOptionPanel;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.gui.Lng;

public class GroupDrawingTools extends InteractionElementGroup {

	public GroupDrawingTools() {
		label = Lng.get("draw menu");
		blockIcon = GuiIcons.GROUP_DRAW;

		addInteractionElement(new SelectDrawingTool());
		addInteractionElement(new ShowToolHelp());
		addInteractionElement(new ShowToolOptionPanel());

	}

}
