package org.abcmap.gui.ie.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.gui.ie.display.windowmode.WindowModeSelector;
import org.abcmap.gui.ie.display.zoom.ResetZoom;
import org.abcmap.gui.ie.display.zoom.ZoomIn;
import org.abcmap.gui.ie.display.zoom.ZoomOut;

public class DisplayToolbar extends Toolbar {

    public DisplayToolbar() {

        addInteractionElement(new ZoomIn());
        addInteractionElement(new ZoomOut());
        addInteractionElement(new ResetZoom());

        WindowModeSelector ws = new WindowModeSelector();
        add(ws.getPrimaryGUI());
    }

}
