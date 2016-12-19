package org.abcmap.gui.iegroup.docks;


import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.draw.SelectLayer;

public class GroupLayersAndDimensions extends InteractionElementGroup {
    public GroupLayersAndDimensions() {
        label = "Calques et dimensions";
        blockIcon = GuiIcons.GROUP_LAYERS;

        addInteractionElement(new SelectLayer());
    }

}
