package org.abcmap.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.align.AlignAndDistribute;
import org.abcmap.ielements.position.MoveElementsByCoordinates;
import org.abcmap.ielements.position.MoveElementsByZOrder;

public class GroupObjectPosition extends GroupOfInteractionElements {
    public GroupObjectPosition() {
        label = "Positionnement d'objets";
        blockIcon = GuiIcons.GROUP_OBJECT_POSITION;

        addInteractionElement(new MoveElementsByZOrder());
        addInteractionElement(new AlignAndDistribute());
        addInteractionElement(new MoveElementsByCoordinates());

    }
}
