package org.abcmap.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.debug.CreateFakeProject;
import org.abcmap.ielements.debug.ShowLastEvents;

public class DebugMenu extends GroupOfInteractionElements {

    public DebugMenu() {

        label = "Débogage";

        addInteractionElement(new ShowLastEvents());
        addInteractionElement(new CreateFakeProject());
    }

}
