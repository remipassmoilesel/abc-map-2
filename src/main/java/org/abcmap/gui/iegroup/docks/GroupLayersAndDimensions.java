package org.abcmap.gui.iegroup.docks;


import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.draw.SelectLayer;

public class GroupLayersAndDimensions extends InteractionElementGroup {
    public GroupLayersAndDimensions() {
        label = "Calques et dimensions";
        blockIcon = GuiIcons.GROUP_LAYERS;

        addInteractionElement(new SelectLayer());
    }

}
