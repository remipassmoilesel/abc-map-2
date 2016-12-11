package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.toProcess.gui.ie.importation.data.MenuDataImport;
import org.abcmap.gui.toProcess.gui.ie.importation.directory.MenuImportFromDirectory;
import org.abcmap.gui.toProcess.gui.ie.importation.document.MenuImportFromDocument;
import org.abcmap.gui.toProcess.gui.ie.importation.manual.MenuImportManualCapture;
import org.abcmap.gui.toProcess.gui.ie.importation.robot.MenuRobotImport;

public class GroupImportation extends InteractionElementGroup {

    public GroupImportation() {

        label = "Importation";
        blockIcon = GuiIcons.GROUP_IMPORT;

        addInteractionElement(new MenuImportFromDirectory());
        addInteractionElement(new MenuImportFromDocument());
        addInteractionElement(new MenuRobotImport());
        addInteractionElement(new MenuImportManualCapture());
        addInteractionElement(new MenuDataImport());

    }

}
