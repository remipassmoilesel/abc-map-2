package org.abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.align.AlignAndDistribute;
import abcmap.gui.ie.position.MoveElementsByCoordinates;
import abcmap.gui.ie.position.MoveElementsByZOrder;
import abcmap.gui.iegroup.InteractionElementGroup;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.align.AlignAndDistribute;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsByCoordinates;
import org.abcmap.gui.toProcess.gui.ie.position.MoveElementsByZOrder;

public class GroupObjectPosition extends InteractionElementGroup{
	public GroupObjectPosition() {
		label = "Positionnement d'objets";
		blockIcon = GuiIcons.GROUP_OBJECT_POSITION;

		addInteractionElement(new MoveElementsByZOrder());
		addInteractionElement(new AlignAndDistribute());
		addInteractionElement(new MoveElementsByCoordinates());

	}
}
