package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.analyse.AnalyseSelectedTiles;
import org.abcmap.ielements.analyse.ImportImageFromFile;
import org.abcmap.ielements.analyse.ImportTileFromFile;

public class ImportMenu extends InteractionElementGroup {

    public ImportMenu() {

        label = "Import";

        addInteractionElement(new AnalyseSelectedTiles());

        addSeparator();
        addInteractionElement(new ImportImageFromFile());
        addInteractionElement(new ImportTileFromFile());

    }

}
