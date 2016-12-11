package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.debug.ShowLastEvents;

public class DebugMenu extends InteractionElementGroup {

    public DebugMenu() {

        label = "Debogage";

        addInteractionElement(new ShowLastEvents());
    }

}
