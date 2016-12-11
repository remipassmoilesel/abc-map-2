package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.program.QuitProgram;
import org.abcmap.gui.toProcess.gui.ie.project.*;
import org.abcmap.gui.toProcess.gui.ie.recents.OpenRecentProject;

public class FileMenu extends InteractionElementGroup {

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
