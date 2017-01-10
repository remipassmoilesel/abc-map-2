package org.abcmap.gui.iegroup.docks;


import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.draw.SelectLayer;

public class GroupLayersAndDimensions extends GroupOfInteractionElements {
    public GroupLayersAndDimensions() {
        label = "Calques";
        blockIcon = GuiIcons.GROUP_LAYERS;

        addInteractionElement(new SelectLayer());
    }

}
