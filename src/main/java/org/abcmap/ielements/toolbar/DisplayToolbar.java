package org.abcmap.ielements.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.ielements.display.windowmode.WindowModeSelection;
import org.abcmap.ielements.display.zoom.ResetZoomOfMainMap;
import org.abcmap.ielements.display.zoom.ZoomInMainMap;
import org.abcmap.ielements.display.zoom.ZoomOutMainMap;

public class DisplayToolbar extends Toolbar {

    public DisplayToolbar() {

        addInteractionElement(new ZoomInMainMap());
        addInteractionElement(new ZoomOutMainMap());
        addInteractionElement(new ResetZoomOfMainMap());

        WindowModeSelection ws = new WindowModeSelection();
        add(ws.getPrimaryGUI());
    }

}
