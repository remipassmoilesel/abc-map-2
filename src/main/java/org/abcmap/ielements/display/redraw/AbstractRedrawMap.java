package org.abcmap.ielements.display.redraw;

import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractRedrawMap extends InteractionElement {

    protected enum Mode {
        ALL_MAP, ACTIVE_LAYER
    }

    private Mode action;

    public AbstractRedrawMap(Mode action) {

        // show main window
        if (Mode.ALL_MAP.equals(action)) {
            label = "Redessiner toute la carte";
            help = "Force la carte à se redessiner. Cette fonction peut être utile en cas de problème d'affichage.";
        }

        // show manual import window
        else if (Mode.ACTIVE_LAYER.equals(action)) {
            label = "Redessiner le calque actif";
            help = "Force le calque actif à se redessiner. Cette fonction peut être utile en cas de problème d'affichage.";
        }

        this.action = action;

    }

    @Override
    public void run() {
        
        GuiUtils.throwIfOnEDT();

        Project project = getCurrentProjectOrShowMessage();
        if (project == null) {
            return;
        }

        try {
            if (getOperationLock() == false) {
                return;
            }

            // delete active layer cache
            if (Mode.ACTIVE_LAYER.equals(action)) {
                project.deleteCacheForLayer(project.getActiveLayer().getId(), null);
            }

            // delete all map
            else if (Mode.ALL_MAP.equals(action)) {
                for (AbmAbstractLayer lay : project.getLayersList()) {
                    project.deleteCacheForLayer(lay.getId(), null);
                }
            }

            // show message for user
            dialm().showMessageInBox("La carte va se redessiner");

        } finally {
            releaseOperationLock();
        }

    }

}
