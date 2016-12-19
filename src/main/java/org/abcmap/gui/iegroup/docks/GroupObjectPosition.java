package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.align.AlignAndDistribute;
import org.abcmap.ielements.position.MoveElementsByCoordinates;
import org.abcmap.ielements.position.MoveElementsByZOrder;

public class GroupObjectPosition extends InteractionElementGroup {
    public GroupObjectPosition() {
        label = "Positionnement d'objets";
        blockIcon = GuiIcons.GROUP_OBJECT_POSITION;

        addInteractionElement(new MoveElementsByZOrder());
        addInteractionElement(new AlignAndDistribute());
        addInteractionElement(new MoveElementsByCoordinates());

    }
}
