package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.draw.SelectDrawingTool;
import org.abcmap.gui.ie.draw.ShowToolHelp;
import org.abcmap.gui.ie.draw.ShowToolOptionPanel;

public class GroupDrawingTools extends InteractionElementGroup {

    public GroupDrawingTools() {
        label = "Dessin";
        blockIcon = GuiIcons.GROUP_DRAW;

        addInteractionElement(new SelectDrawingTool());
        addInteractionElement(new ShowToolHelp());
        addInteractionElement(new ShowToolOptionPanel());

    }

}
