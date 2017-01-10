package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.export.*;

public class GroupExport extends GroupOfInteractionElements {

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
