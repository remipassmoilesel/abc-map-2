package abcmap.gui.ie.draw;

import java.awt.Component;

import abcmap.gui.comps.draw.layers.LayerSelectorPanel;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.gui.Lng;

public class SelectLayer extends InteractionElement {

	public SelectLayer() {
		label = "Gestion des calques";
		help = ".....";
	}

	@Override
	protected Component createPrimaryGUI() {
		return new LayerSelectorPanel();
	}

}
