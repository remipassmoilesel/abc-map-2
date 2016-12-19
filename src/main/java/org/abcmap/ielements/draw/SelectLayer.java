package org.abcmap.ielements.draw;

import org.abcmap.gui.components.layers.LayerSelectorPanel;
import org.abcmap.ielements.InteractionElement;

import java.awt.*;

public class SelectLayer extends InteractionElement {

    public SelectLayer() {
        label = "Gestion des calques";
        help = ".....";
    }

    @Override
    protected Component createPrimaryGUI() {
        return new LayerSelectorPanel();
    }

}
