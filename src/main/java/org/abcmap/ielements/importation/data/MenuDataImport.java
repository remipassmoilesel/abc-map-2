package org.abcmap.ielements.importation.data;

import org.abcmap.ielements.InteractionElementGroup;

public class MenuDataImport extends InteractionElementGroup {

    public MenuDataImport() {
        this.label = "Importer un fichier de données...";
        this.help = "Cliquez ici pour ouvrir le menu d'import de fichier de données. Ce menu contient tous "
                + "les éléments permettant d'importer des données à partir d'un fichier de tableur, "
                + "d'un fichier GPX ou KML.";

        // Chemin de la liste à importer
        addInteractionElement(new SelectDataToImport());

        // Ouvrir une liste d'exemple
        addInteractionElement(new CreateDataFile());

        // options
        addInteractionElement(new SelectDataImportOptions());

        // systeme de coordonnées
        addInteractionElement(new SelectDataImportCRS());

        // lancement
        addInteractionElement(new LaunchDataImport());

    }

    // @Override
    // protected DockMenuPanel createSubMenu() {
    // return new SubMenuDataImport();
    // }
}
