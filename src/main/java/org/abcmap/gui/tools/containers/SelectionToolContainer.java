package org.abcmap.gui.tools.containers;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.tools.SelectionTool;

/**
 * Created by remipassmoilesel on 19/12/16.
 */
public class SelectionToolContainer extends ToolContainer {

    public SelectionToolContainer() {
        this.id = ToolLibrary.SELECTION_TOOL;
        this.readableName = "Sélection";
        this.icon = GuiIcons.TOOL_SELECTION;
        this.accelerator = shortcuts.SELECTION_TOOL;
        this.toolClass = SelectionTool.class;
    }
}
