package org.abcmap.ielements.importation.geo;

import org.abcmap.ielements.GroupOfInteractionElements;

public class MenuGeoDataImport extends GroupOfInteractionElements {

    public MenuGeoDataImport() {

        this.label = "Importer un document géographique...";
        this.help = "...";

        // add a shapefile
        addInteractionElement(new AddShapefileLayer());

        // add a GPX file
        addInteractionElement(new AddGpxData());

    }

}
