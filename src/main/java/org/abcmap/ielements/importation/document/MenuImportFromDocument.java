package org.abcmap.ielements.importation.document;

import org.abcmap.ielements.GroupOfInteractionElements;

public class MenuImportFromDocument extends GroupOfInteractionElements {

    public MenuImportFromDocument() {

        this.label = "Importer un document...";
        this.help = "Cliquez ici pour importer un document PDF, SVG ou une image. Le document sera copié "
                + "dans le projet et inséré en tant qu'image";

        // selection du document
        addInteractionElement(new SelectDocumentImportPath());

        // options d'import
        addInteractionElement(new SelectDocumentImportOptions());

        // facteur d'agrandissement
        addInteractionElement(new SelectDocumentImportScale());

        // lancement de l'import
        addInteractionElement(new LaunchDocumentImport());

    }

}
