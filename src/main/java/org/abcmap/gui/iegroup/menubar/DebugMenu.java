package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.debug.CreateFakeProject;
import org.abcmap.gui.ie.debug.ShowLastEvents;

public class DebugMenu extends InteractionElementGroup {

    public DebugMenu() {

        label = "Débogage";

        addInteractionElement(new ShowLastEvents());
        addInteractionElement(new CreateFakeProject());
    }

}
