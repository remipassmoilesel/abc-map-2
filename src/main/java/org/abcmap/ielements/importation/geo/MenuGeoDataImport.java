package org.abcmap.ielements.importation.geo;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.importation.AddShapefileLayer;
import org.abcmap.ielements.importation.document.LaunchDocumentImport;
import org.abcmap.ielements.importation.document.SelectDocumentImportOptions;
import org.abcmap.ielements.importation.document.SelectDocumentImportPath;
import org.abcmap.ielements.importation.document.SelectDocumentImportScale;

public class MenuGeoDataImport extends GroupOfInteractionElements {

    public MenuGeoDataImport() {

        this.label = "Importer un document géographique...";
        this.help = "...";

        // add a shapefile
        addInteractionElement(new AddShapefileLayer());

        addSeparator();

    }

}
