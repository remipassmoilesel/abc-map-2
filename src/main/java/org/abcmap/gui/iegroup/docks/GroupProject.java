package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.project.*;
import org.abcmap.ielements.recents.OpenRecentProject;

public class GroupProject extends GroupOfInteractionElements {

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
