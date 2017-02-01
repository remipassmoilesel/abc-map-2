package org.abcmap.ielements.importation.data;

import org.abcmap.ielements.GroupOfInteractionElements;

public class MenuSpreadsheetImport extends GroupOfInteractionElements {

    public MenuSpreadsheetImport() {
        this.label = "Importer un classeur de données...";
        this.help = "...";

        // path of list
        addInteractionElement(new SelectDataToImport());

        // open an example
        addInteractionElement(new CreateDataFile());

        // options
        addInteractionElement(new SelectDataImportOptions());

        // coordinate system
        addInteractionElement(new SelectDataImportCRS());

        // launch
        addInteractionElement(new LaunchDataImport());

    }
}
