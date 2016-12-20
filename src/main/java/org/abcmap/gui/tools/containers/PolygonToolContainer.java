package org.abcmap.gui.tools.containers;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.tools.PolygonTool;

/**
 * Created by remipassmoilesel on 19/12/16.
 */
public class PolygonToolContainer extends ToolContainer {

    public PolygonToolContainer() {
        this.id = ToolLibrary.POLYGON_TOOL;
        this.readableName = "Polygone";
        this.icon = GuiIcons.TOOL_POLYGON;
        this.accelerator = shortcuts.POLYGON_TOOL;
        this.toolClass = PolygonTool.class;
    }

}
