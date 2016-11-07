package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.draw.SelectLayer;
import abcmap.gui.ie.draw.SelectMapDimensions;
import abcmap.gui.iegroup.InteractionElementGroup;

public class GroupLayersAndDimensions extends InteractionElementGroup {
	public GroupLayersAndDimensions() {
		label = "Calques et dimensions";
		blockIcon = GuiIcons.GROUP_LAYERS;

		addInteractionElement(new SelectLayer());
		addInteractionElement(new SelectMapDimensions());
	}

}
