package org.abcmap.ielements.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.ielements.display.windowmode.WindowModeSelection;
import org.abcmap.ielements.display.zoom.ResetZoom;
import org.abcmap.ielements.display.zoom.ZoomIn;
import org.abcmap.ielements.display.zoom.ZoomOut;

public class DisplayToolbar extends Toolbar {

    public DisplayToolbar() {

        addInteractionElement(new ZoomIn());
        addInteractionElement(new ZoomOut());
        addInteractionElement(new ResetZoom());

        WindowModeSelection ws = new WindowModeSelection();
        add(ws.getPrimaryGUI());
    }

}
