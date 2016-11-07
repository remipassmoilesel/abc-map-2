package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.layouts.ModifyLayouts;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.windows.MainWindowMode;

public class GroupLayout extends InteractionElementGroup {
	public GroupLayout() {
		label = "Mise en page";
		blockIcon = GuiIcons.GROUP_LAYOUT;

		windowMode = abcmap.gui.windows.MainWindowMode.SHOW_LAYOUTS;

		addInteractionElement(new ModifyLayouts());
	}
}
