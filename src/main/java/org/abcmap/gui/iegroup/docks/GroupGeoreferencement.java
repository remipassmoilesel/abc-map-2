package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.geo.ActivateGeoreferencement;
import org.abcmap.ielements.geo.SelectGeoReferences;
import org.abcmap.ielements.geo.SelectMapCRS;

public class GroupGeoreferencement extends GroupOfInteractionElements {

    public GroupGeoreferencement() {
        label = "Géolocalisation";
        blockIcon = GuiIcons.GROUP_GEOLOC;

        addInteractionElement(new ActivateGeoreferencement());

        addInteractionElement(new SelectMapCRS());

        addInteractionElement(new SelectGeoReferences());

    }

}
