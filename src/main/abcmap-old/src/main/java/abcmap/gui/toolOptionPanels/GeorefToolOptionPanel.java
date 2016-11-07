package abcmap.gui.toolOptionPanels;

import abcmap.gui.ie.geo.SelectGeoReferences;
import abcmap.utils.gui.GuiUtils;

public class GeorefToolOptionPanel extends ToolOptionPanel {

	/**
	 * Create the panel.
	 */
	public GeorefToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// récupérer l'interface dans l'element d'interaction de selection de
		// reference active
		SelectGeoReferences ie = new SelectGeoReferences();

		add(ie.getPrimaryGUI(), "wrap");

	}
}
