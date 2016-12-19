package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.InteractionElementGroup;

import java.util.ArrayList;

public class GroupPlugins extends InteractionElementGroup {

    public GroupPlugins() {
        label = "Modules d'extension";
        blockIcon = GuiIcons.GROUP_PLUGINS;

        ArrayList<InteractionElement> plgs = InteractionElement.getAllAvailablesPlugins();

        for (InteractionElement ie : plgs) {
            addInteractionElement(ie);
        }

    }

}
