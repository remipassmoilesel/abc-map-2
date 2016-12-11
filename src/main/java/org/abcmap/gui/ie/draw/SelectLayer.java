package org.abcmap.gui.ie.draw;

import org.abcmap.gui.components.layers.LayerSelectorPanel;
import org.abcmap.gui.ie.InteractionElement;

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
