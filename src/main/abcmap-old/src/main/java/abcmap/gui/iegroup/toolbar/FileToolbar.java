package abcmap.gui.iegroup.toolbar;

import abcmap.gui.ie.project.CloseProject;
import abcmap.gui.ie.project.NewProject;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.project.SaveAsProject;
import abcmap.gui.ie.project.SaveProject;
import abcmap.gui.toolbar.Toolbar;

public class FileToolbar extends Toolbar {

	public FileToolbar() {
		addInteractionElement(new NewProject());
		addInteractionElement(new OpenProject());
		addInteractionElement(new SaveProject());
		addInteractionElement(new SaveAsProject());
		addInteractionElement(new CloseProject());
	}

}
