package org.abcmap.gui.iegroup.menubar;

import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.gui.tools.containers.ToolContainer;
import org.abcmap.gui.tools.containers.ToolLibrary;

import java.awt.event.ActionEvent;

public class DrawingToolsMenu extends InteractionElementGroup {

    public DrawingToolsMenu() {

        label = "Outils de dessin";

        ToolContainer[] tcs = ToolLibrary.getAvailableTools();

        for (ToolContainer tc : tcs) {
            ToolInteractionElement ie = new ToolInteractionElement(tc);
            addInteractionElement(ie);
        }

    }

    private class ToolInteractionElement extends InteractionElement {

        private ToolContainer toolc;

        public ToolInteractionElement(ToolContainer tc) {

            this.toolc = tc;

            this.label = tc.getReadableName();
            this.accelerator = tc.getAccelerator();
            this.menuIcon = tc.getIcon();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);

            drawm().setCurrentTool(toolc);
        }
    }

}
