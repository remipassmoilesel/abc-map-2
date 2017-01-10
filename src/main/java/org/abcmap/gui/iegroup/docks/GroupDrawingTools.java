package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.draw.SelectDrawingTool;
import org.abcmap.ielements.draw.ShowToolHelp;
import org.abcmap.ielements.draw.ShowToolOptionPanel;

public class GroupDrawingTools extends GroupOfInteractionElements {

    public GroupDrawingTools() {
        label = "Dessin";
        blockIcon = GuiIcons.GROUP_DRAW;

        addInteractionElement(new SelectDrawingTool());
        addInteractionElement(new ShowToolHelp());
        addInteractionElement(new ShowToolOptionPanel());

    }

}
