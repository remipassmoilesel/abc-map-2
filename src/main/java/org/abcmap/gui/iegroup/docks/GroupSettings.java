package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.analyse.AnalyseSelectedTiles;
import org.abcmap.ielements.importation.SelectPictureAnalyseMode;

public class GroupSettings extends InteractionElementGroup {

    public GroupSettings() {
        label = "Réglages";
        blockIcon = GuiIcons.GROUP_SETTINGS;

        addInteractionElement(new AnalyseSelectedTiles());
        addInteractionElement(new SelectPictureAnalyseMode());
    }
}
