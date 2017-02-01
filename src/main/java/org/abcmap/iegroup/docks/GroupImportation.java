package org.abcmap.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.importation.MenuImportFromInternet;
import org.abcmap.ielements.importation.data.MenuSpreadsheetImport;
import org.abcmap.ielements.importation.directory.MenuImportFromDirectory;
import org.abcmap.ielements.importation.document.MenuDocumentImport;
import org.abcmap.ielements.importation.geo.MenuGeoDataImport;
import org.abcmap.ielements.importation.manual.MenuImportManualCapture;
import org.abcmap.ielements.importation.robot.MenuRobotImport;

public class GroupImportation extends GroupOfInteractionElements {

    public GroupImportation() {

        label = "Importation";
        blockIcon = GuiIcons.GROUP_IMPORT;

        addInteractionElement(new MenuImportFromInternet());
        addInteractionElement(new MenuDocumentImport());
        addInteractionElement(new MenuGeoDataImport());
        addInteractionElement(new MenuSpreadsheetImport());
        addInteractionElement(new MenuImportFromDirectory());
        addInteractionElement(new MenuRobotImport());
        addInteractionElement(new MenuImportManualCapture());


    }

}
