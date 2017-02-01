package org.abcmap.ielements.importation.document;

import org.abcmap.ielements.GroupOfInteractionElements;

public class MenuDocumentImport extends GroupOfInteractionElements {

    public MenuDocumentImport() {

        this.label = "Importer un document...";
        this.help = "...";

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
