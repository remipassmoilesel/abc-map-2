package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.ActivateGeoreferencement;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.SelectGeoReferences;
import org.abcmap.gui.ie.SelectMapCRS;

public class GroupGeoreferencement extends InteractionElementGroup {

	public GroupGeoreferencement() {
		label = "Géolocalisation";
		blockIcon = GuiIcons.GROUP_GEOLOC;

		addInteractionElement(new ActivateGeoreferencement());

		addInteractionElement(new SelectMapCRS());

		addInteractionElement(new SelectGeoReferences());

	}

}
