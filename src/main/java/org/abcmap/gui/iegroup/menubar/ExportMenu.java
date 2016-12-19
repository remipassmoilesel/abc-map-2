package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.export.ExportLayoutFrames;
import org.abcmap.ielements.export.ExportMapToPng;
import org.abcmap.ielements.export.PrintLayouts;

public class ExportMenu extends InteractionElementGroup {

    public ExportMenu() {

        label = "Export";

        addInteractionElement(new PrintLayouts());
        addSeparator();
        addInteractionElement(new ExportLayoutFrames());
        addInteractionElement(new ExportMapToPng());

    }

}
