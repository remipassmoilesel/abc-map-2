package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.debug.CreateFakeProject;
import org.abcmap.ielements.debug.ShowLastEvents;

public class DebugMenu extends InteractionElementGroup {

    public DebugMenu() {

        label = "Débogage";

        addInteractionElement(new ShowLastEvents());
        addInteractionElement(new CreateFakeProject());
    }

}
