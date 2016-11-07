package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.program.QuitProgram;
import abcmap.gui.ie.project.CloseProject;
import abcmap.gui.ie.project.NewProject;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.project.SaveAsProject;
import abcmap.gui.ie.project.SaveProject;
import abcmap.gui.ie.recents.OpenRecentProject;
import abcmap.gui.iegroup.InteractionElementGroup;

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
