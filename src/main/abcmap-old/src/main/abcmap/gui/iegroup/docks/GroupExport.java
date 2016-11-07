package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.export.GpxExport;
import abcmap.gui.ie.export.KmlExport;
import abcmap.gui.ie.export.PngLayoutExport;
import abcmap.gui.ie.export.PngMapExport;
import abcmap.gui.ie.export.PrintLayouts;
import abcmap.gui.ie.export.ShpExport;
import abcmap.gui.iegroup.InteractionElementGroup;

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
