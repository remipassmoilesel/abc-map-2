package org.abcmap.iegroup.menubar;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.program.QuitProgram;
import org.abcmap.ielements.project.*;
import org.abcmap.ielements.recents.OpenRecentProject;

public class FileMenu extends GroupOfInteractionElements {

    public FileMenu() {

        label = "Fichier";

        addInteractionElement(new NewProject());
        addInteractionElement(new OpenProject());
        addInteractionElement(new OpenRecentProject());
        addInteractionElement(new SaveProject());
        addInteractionElement(new SaveAsProject());

        addSeparator();
        addInteractionElement(new CloseProject());
        addInteractionElement(new QuitProgram());
    }

}
