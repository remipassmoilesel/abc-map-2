package org.abcmap.ielements.importation;

import org.abcmap.ielements.GroupOfInteractionElements;

public class MenuImportFromInternet extends GroupOfInteractionElements {

    public MenuImportFromInternet() {

        this.label = "Importer des ressources distantes";
        this.help = "...";

        addInteractionElement(new AddDataFromCatalog());
        addInteractionElement(new AddWMSLayer());

    }
}
