package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.project.CloseProject;
import abcmap.gui.ie.project.NewProject;
import abcmap.gui.ie.project.OpenProject;
import abcmap.gui.ie.project.SaveAsProject;
import abcmap.gui.ie.project.SaveProject;
import abcmap.gui.ie.project.SetProjectComment;
import abcmap.gui.ie.project.SetProjectTitle;
import abcmap.gui.ie.recents.OpenRecentProject;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.gui.Lng;

public class GroupProject extends InteractionElementGroup {

	public GroupProject() {
		label = Lng.get("project menu");
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
