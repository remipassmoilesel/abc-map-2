package org.abcmap.gui.tools.containers;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.tools.LineTool;

/**
 * Created by remipassmoilesel on 19/12/16.
 */
public class LineToolContainer extends ToolContainer {

    public LineToolContainer() {
        this.id = ToolLibrary.LINE_TOOL;
        this.readableName = "Ligne";
        this.icon = GuiIcons.TOOL_LINE;
        this.accelerator = shortcuts.LINE_TOOL;
        this.toolClass = LineTool.class;
    }
}
