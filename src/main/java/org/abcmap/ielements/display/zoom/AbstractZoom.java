package org.abcmap.ielements.display.zoom;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;

/**
 * Change zoom values for main map
 */
public abstract class AbstractZoom extends InteractionElement {

    protected enum Direction {
        IN, OUT, CENTER
    }

    private Direction direction;

    public AbstractZoom(Direction dir) {

        this.direction = dir;

        // zoom in
        if (Direction.IN.equals(dir)) {
            label = "Zoom avant";
            help = "Cliquez ici pour zoomer la carte.";
            menuIcon = GuiIcons.SMALLICON_ZOOMIN;
        }

        // zoom out
        else if (Direction.OUT.equals(dir)) {
            label = "Zoom arrière";
            help = "Cliquez ici pour dézoomer la carte.";
            menuIcon = GuiIcons.SMALLICON_ZOOMOUT;
        }

        // reset display
        else if (Direction.CENTER.equals(dir)) {
            label = "Remise à zéro de l'affichage";
            help = "Cliquez ici pour remettre à zéro l'affichage.";
            menuIcon = GuiIcons.MAP_MOVECENTER;
        }
    }

    @Override
    public void run() {

        // zoom in
        if (Direction.IN.equals(direction)) {
            mapm.mainmap.zoomIn();
        }

        // zoom out
        else if (Direction.OUT.equals(direction)) {
            mapm.mainmap.zoomOut();
        }

        // reset display
        else if (Direction.CENTER.equals(direction)) {
            mapm.mainmap.resetDisplay();
        }

        // error
        else {
            throw new IllegalStateException("Unknown mode: " + direction);
        }

        mapm.mainmap.refresh();

    }

}
