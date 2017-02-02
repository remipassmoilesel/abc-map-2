package org.abcmap.ielements.importation.data;

import org.abcmap.gui.components.dock.blockitems.DockMenuPanel;

public class SubMenuDataImport extends DockMenuPanel {

    public SubMenuDataImport() {
        super();

        addMenuElement(new SelectSpreadsheetToImport());

        addMenuElement(new CreateDataFile());

        addMenuElement(new SelectDataImportOptions());

        addMenuElement(new SelectDataImportCRS());

        addMenuElement(new LaunchDataImport());

        reconstruct();
    }
}
