package org.abcmap.ielements.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;
import org.abcmap.ielements.project.*;

public class FileToolbar extends Toolbar {

    public FileToolbar() {
        addInteractionElement(new NewProject());
        addInteractionElement(new OpenProject());
        addInteractionElement(new SaveProject());
        addInteractionElement(new SaveAsProject());
        addInteractionElement(new CloseProject());
    }

}
