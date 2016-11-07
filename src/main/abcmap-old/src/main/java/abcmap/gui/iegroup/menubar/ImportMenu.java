package abcmap.gui.iegroup.menubar;

import abcmap.gui.ie.analyse.AnalyseSelectedTiles;
import abcmap.gui.ie.analyse.ImportImageFromFile;
import abcmap.gui.ie.analyse.ImportTileFromFile;
import abcmap.gui.iegroup.InteractionElementGroup;

public class ImportMenu extends InteractionElementGroup {

	public ImportMenu() {

		label = "Import";

		addInteractionElement(new AnalyseSelectedTiles());
		
		addSeparator();
		addInteractionElement(new ImportImageFromFile());
		addInteractionElement(new ImportTileFromFile());

	}

}
