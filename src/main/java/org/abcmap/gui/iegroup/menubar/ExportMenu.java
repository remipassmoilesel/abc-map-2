package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.export.ExportLayoutFrames;
import org.abcmap.ielements.export.ExportMapToPng;
import org.abcmap.ielements.export.PrintLayouts;

public class ExportMenu extends GroupOfInteractionElements {

    public ExportMenu() {

        label = "Export";

        addInteractionElement(new PrintLayouts());
        addSeparator();
        addInteractionElement(new ExportLayoutFrames());
        addInteractionElement(new ExportMapToPng());

    }

}
