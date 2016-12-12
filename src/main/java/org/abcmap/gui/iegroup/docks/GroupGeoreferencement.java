package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.geo.ActivateGeoreferencement;
import org.abcmap.gui.ie.geo.SelectGeoReferences;
import org.abcmap.gui.ie.geo.SelectMapCRS;

public class GroupGeoreferencement extends InteractionElementGroup {

    public GroupGeoreferencement() {
        label = "Géolocalisation";
        blockIcon = GuiIcons.GROUP_GEOLOC;

        addInteractionElement(new ActivateGeoreferencement());

        addInteractionElement(new SelectMapCRS());

        addInteractionElement(new SelectGeoReferences());

    }

}
