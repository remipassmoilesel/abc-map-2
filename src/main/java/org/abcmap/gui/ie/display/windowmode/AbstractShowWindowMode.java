package org.abcmap.gui.ie.display.windowmode;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.windows.MainWindowMode;

public abstract class AbstractShowWindowMode extends InteractionElement {

    public AbstractShowWindowMode(MainWindowMode dir) {

        if (MainWindowMode.SHOW_MAP.equals(dir)) {
            label = "Afficher la carte";
            help = "Afficher la carte";
            menuIcon = GuiIcons.SMALLICON_DISPLAYMAP;
        }

        //
        else if (MainWindowMode.SHOW_LAYOUTS.equals(dir)) {
            label = "Afficher la mise en page";
            help = "Afficher la mise en page";
            menuIcon = GuiIcons.SMALLICON_DISPLAYLAYOUT;
        }

        //
        else if (MainWindowMode.SHOW_REFUSED_TILES.equals(dir)) {
            label = "Afficher les tuiles refusées";
            help = "Afficher les tuiles refusées";
            menuIcon = GuiIcons.SMALLICON_DISPLAYREFUSEDTILES;
        }

    }


}
