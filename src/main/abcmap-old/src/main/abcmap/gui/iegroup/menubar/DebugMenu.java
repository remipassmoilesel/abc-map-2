package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.debug.ShowLastEvents;
import abcmap.gui.iegroup.InteractionElementGroup;

public class DebugMenu extends InteractionElementGroup {

	public DebugMenu() {

		label = "Debogage";

		addInteractionElement(new ShowLastEvents());
	}

}
