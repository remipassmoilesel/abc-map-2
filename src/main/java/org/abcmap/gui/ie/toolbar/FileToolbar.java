package org.abcmap.gui.ie.toolbar;

import org.abcmap.gui.components.toolbar.Toolbar;

public class FileToolbar extends Toolbar {

	public FileToolbar() {
		addInteractionElement(new NewProject());
		addInteractionElement(new OpenProject());
		addInteractionElement(new SaveProject());
		addInteractionElement(new SaveAsProject());
		addInteractionElement(new CloseProject());
	}

}
