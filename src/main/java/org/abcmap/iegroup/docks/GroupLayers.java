package org.abcmap.iegroup.docks;


import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.draw.SelectLayer;
import org.abcmap.ielements.layers.LayerInformationsPanel;

public class GroupLayers extends GroupOfInteractionElements {

    public GroupLayers() {
        label = "Calques";
        blockIcon = GuiIcons.GROUP_LAYERS;

        addInteractionElement(new SelectLayer());
        addInteractionElement(new LayerInformationsPanel());
    }

}
