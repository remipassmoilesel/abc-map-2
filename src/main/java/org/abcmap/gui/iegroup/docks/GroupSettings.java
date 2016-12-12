package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.analyse.AnalyseSelectedTiles;
import org.abcmap.gui.ie.importation.SelectPictureAnalyseMode;

public class GroupSettings extends InteractionElementGroup {

    public GroupSettings() {
        label = "Réglages";
        blockIcon = GuiIcons.GROUP_SETTINGS;

        addInteractionElement(new AnalyseSelectedTiles());
        addInteractionElement(new SelectPictureAnalyseMode());
    }
}
