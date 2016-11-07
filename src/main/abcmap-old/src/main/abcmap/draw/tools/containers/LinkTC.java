package abcmap.draw.tools.containers;

import abcmap.draw.tools.LinkTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.LinkToolOptionPanel;

public class LinkTC extends ToolContainer {
	public LinkTC() {
		this.id = "LINK_TOOL";
		this.toolClass = LinkTool.class;
		this.readableName = "Outil de lien";
		this.accelerator = shortcuts.LINK_TOOL;
		this.optionPanelClass = LinkToolOptionPanel.class;
		this.icon = GuiIcons.TOOL_LINK;
	}
}
