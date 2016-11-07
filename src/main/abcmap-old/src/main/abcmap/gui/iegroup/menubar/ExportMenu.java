package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.export.ExportLayoutFrames;
import abcmap.gui.ie.export.ExportMapToPng;
import abcmap.gui.ie.export.PrintLayouts;
import abcmap.gui.iegroup.InteractionElementGroup;

public class ExportMenu extends InteractionElementGroup {

	public ExportMenu() {

		label = "Export";

		addInteractionElement(new PrintLayouts());
		addSeparator();
		addInteractionElement(new ExportLayoutFrames());
		addInteractionElement(new ExportMapToPng());

	}

}
