package abcmap.gui.iegroup.docks;

import abcmap.gui.GuiIcons;
import abcmap.gui.ie.geo.ActivateGeoreferencement;
import abcmap.gui.ie.geo.SelectGeoReferences;
import abcmap.gui.ie.geo.SelectMapCRS;
import abcmap.gui.iegroup.InteractionElementGroup;

public class GroupGeoreferencement extends InteractionElementGroup {

	public GroupGeoreferencement() {
		label = "Géolocalisation";
		blockIcon = GuiIcons.GROUP_GEOLOC;

		// activer ou desactiver le georeferencement
		addInteractionElement(new ActivateGeoreferencement());
		
		// selectionner le systeme de coordonnées
		addInteractionElement(new SelectMapCRS());

		// selectionner les references
		addInteractionElement(new SelectGeoReferences());

	}

}
