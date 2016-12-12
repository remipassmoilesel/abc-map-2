package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.recents.OpenRecentProject;

public class GroupProject extends InteractionElementGroup {

    public GroupProject() {
        label = "Projet";
        blockIcon = GuiIcons.GROUP_PROJECT;

        addInteractionElement(new NewProject());
        addInteractionElement(new OpenProject());
        addInteractionElement(new OpenRecentProject());
        addInteractionElement(new SaveProject());
        addInteractionElement(new SaveAsProject());
        addInteractionElement(new CloseProject());

        addSeparator();
        addInteractionElement(new SetProjectTitle());
        addInteractionElement(new SetProjectComment());

    }

}
