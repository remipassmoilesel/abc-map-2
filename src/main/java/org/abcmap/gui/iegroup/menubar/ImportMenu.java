package org.abcmap.gui.iegroup.menubar;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.analyse.AnalyseSelectedTiles;
import org.abcmap.gui.toProcess.gui.ie.analyse.ImportImageFromFile;
import org.abcmap.gui.toProcess.gui.ie.analyse.ImportTileFromFile;

public class ImportMenu extends InteractionElementGroup {

    public ImportMenu() {

        label = "Import";

        addInteractionElement(new AnalyseSelectedTiles());

        addSeparator();
        addInteractionElement(new ImportImageFromFile());
        addInteractionElement(new ImportTileFromFile());

    }

}
