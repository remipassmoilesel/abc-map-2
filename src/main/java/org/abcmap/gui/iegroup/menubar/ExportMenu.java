package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.export.ExportLayoutFrames;
import org.abcmap.gui.ie.export.ExportMapToPng;
import org.abcmap.gui.ie.export.PrintLayouts;

public class ExportMenu extends InteractionElementGroup {

    public ExportMenu() {

        label = "Export";

        addInteractionElement(new PrintLayouts());
        addSeparator();
        addInteractionElement(new ExportLayoutFrames());
        addInteractionElement(new ExportMapToPng());

    }

}
