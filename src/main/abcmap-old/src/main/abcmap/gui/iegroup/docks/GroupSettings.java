package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.analyse.AnalyseSelectedTiles;
import abcmap.gui.ie.importation.SelectPictureAnalyseMode;
import abcmap.gui.iegroup.InteractionElementGroup;

public class GroupSettings extends InteractionElementGroup {

	public GroupSettings() {
		label = "Réglages";
		blockIcon = GuiIcons.GROUP_SETTINGS;

		addInteractionElement(new AnalyseSelectedTiles());
		addInteractionElement(new SelectPictureAnalyseMode());
	}
}
