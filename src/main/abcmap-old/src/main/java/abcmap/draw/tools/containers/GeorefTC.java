package abcmap.draw.tools.containers;

import abcmap.draw.tools.GeorefTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.GeorefToolOptionPanel;

public class GeorefTC extends ToolContainer {

	public GeorefTC() {
		this.id = "GEOREF_TOOL";
		this.readableName = "Outil de géo-référencement";
		this.optionPanelClass = GeorefToolOptionPanel.class;
		this.accelerator = shortcuts.GEOREF_TOOL;
		this.icon = GuiIcons.TOOL_GEOREF;
		this.toolClass = GeorefTool.class;
	}

}