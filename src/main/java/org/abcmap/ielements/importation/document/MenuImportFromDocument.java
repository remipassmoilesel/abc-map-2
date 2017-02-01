package org.abcmap.ielements.importation.document;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.importation.AddShapefileLayer;

public class MenuImportFromDocument extends GroupOfInteractionElements {

    public MenuImportFromDocument() {

        this.label = "Importer un document...";
        this.help = "...";

        // add a shapefile
        addInteractionElement(new AddShapefileLayer());

        addSeparator();

        // select document
        addInteractionElement(new SelectDocumentImportPath());

        // import options
        addInteractionElement(new SelectDocumentImportOptions());

        // enlarge document
        addInteractionElement(new SelectDocumentImportScale());

        // launch import
        addInteractionElement(new LaunchDocumentImport());

    }

}
