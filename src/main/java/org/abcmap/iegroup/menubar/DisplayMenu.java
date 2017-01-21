package org.abcmap.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.display.redraw.RedrawActiveLayer;
import org.abcmap.ielements.display.redraw.RedrawAllMap;
import org.abcmap.ielements.display.zoom.ResetZoomOfMainMap;
import org.abcmap.ielements.display.zoom.ZoomInMainMap;
import org.abcmap.ielements.display.zoom.ZoomOutMainMap;


public class DisplayMenu extends GroupOfInteractionElements {

    public DisplayMenu() {

        label = "Affichage";

        addInteractionElement(new RedrawActiveLayer());
        addInteractionElement(new RedrawAllMap());

        addSeparator();

        addInteractionElement(new ZoomInMainMap());
        addInteractionElement(new ZoomOutMainMap());
        addInteractionElement(new ResetZoomOfMainMap());

    }

}
