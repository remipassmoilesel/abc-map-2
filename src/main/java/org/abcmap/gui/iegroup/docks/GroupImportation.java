package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.importation.data.MenuDataImport;
import org.abcmap.ielements.importation.directory.MenuImportFromDirectory;
import org.abcmap.ielements.importation.document.MenuImportFromDocument;
import org.abcmap.ielements.importation.manual.MenuImportManualCapture;
import org.abcmap.ielements.importation.robot.MenuRobotImport;

public class GroupImportation extends GroupOfInteractionElements {

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
