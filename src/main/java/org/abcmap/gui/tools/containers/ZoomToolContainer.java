package org.abcmap.gui.tools.containers;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.tools.ZoomTool;

public class ZoomToolContainer extends ToolContainer {

    public ZoomToolContainer() {
        this.id = ToolLibrary.ZOOM_TOOL;
        this.readableName = "Zoom";
        this.icon = GuiIcons.TOOL_ZOOM;
        this.toolClass = ZoomTool.class;
    }
}
