package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.export.*;

public class GroupExport extends InteractionElementGroup {

	public GroupExport() {
		label = "Export et impression";
		blockIcon = GuiIcons.GROUP_EXPORT;

		addInteractionElement(new PrintLayouts());

		addSeparator();
		addInteractionElement(new PngMapExport());
		addInteractionElement(new PngLayoutExport());
		addInteractionElement(new ShpExport());

		addSeparator();
		addInteractionElement(new GpxExport());
		addInteractionElement(new KmlExport());

	}

}
