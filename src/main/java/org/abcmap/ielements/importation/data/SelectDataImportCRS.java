package org.abcmap.ielements.importation.data;

import org.abcmap.gui.components.geo.CrsSelectionPanel;
import org.abcmap.ielements.InteractionElement;

import java.awt.*;

public class SelectDataImportCRS extends InteractionElement {

    public SelectDataImportCRS() {

        this.label = "Système de coordonnées de l'import";
        this.help = "....";

    }

    @Override
    protected Component createPrimaryGUI() {
        CrsSelectionPanel panel = new CrsSelectionPanel();
        return panel;
    }

}
