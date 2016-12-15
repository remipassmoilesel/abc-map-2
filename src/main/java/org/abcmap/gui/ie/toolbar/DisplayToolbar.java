package org.abcmap.gui.ie.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.gui.ie.display.windowmode.WindowModeSelection;
import org.abcmap.gui.ie.display.zoom.ResetZoom;
import org.abcmap.gui.ie.display.zoom.ZoomIn;
import org.abcmap.gui.ie.display.zoom.ZoomOut;

public class DisplayToolbar extends Toolbar {

    public DisplayToolbar() {

        addInteractionElement(new ZoomIn());
        addInteractionElement(new ZoomOut());
        addInteractionElement(new ResetZoom());

        WindowModeSelection ws = new WindowModeSelection();
        add(ws.getPrimaryGUI());
    }

}
