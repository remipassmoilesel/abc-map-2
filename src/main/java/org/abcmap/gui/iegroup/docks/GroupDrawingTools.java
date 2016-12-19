package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.draw.SelectDrawingTool;
import org.abcmap.ielements.draw.ShowToolHelp;
import org.abcmap.ielements.draw.ShowToolOptionPanel;

public class GroupDrawingTools extends InteractionElementGroup {

    public GroupDrawingTools() {
        label = "Dessin";
        blockIcon = GuiIcons.GROUP_DRAW;

        addInteractionElement(new SelectDrawingTool());
        addInteractionElement(new ShowToolHelp());
        addInteractionElement(new ShowToolOptionPanel());

    }

}
