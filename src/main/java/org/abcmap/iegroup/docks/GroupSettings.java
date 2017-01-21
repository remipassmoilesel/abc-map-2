package org.abcmap.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.analyse.AnalyseSelectedTiles;
import org.abcmap.ielements.importation.SelectPictureAnalyseMode;

public class GroupSettings extends GroupOfInteractionElements {

    public GroupSettings() {
        label = "Réglages";
        blockIcon = GuiIcons.GROUP_SETTINGS;

        addInteractionElement(new AnalyseSelectedTiles());
        addInteractionElement(new SelectPictureAnalyseMode());
    }
}
