package abcmap.draw.tools.containers;

import abcmap.draw.tools.TileTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.TileToolOptionPanel;

public class TileTC extends ToolContainer {

	public TileTC() {
		this.id = "TILE_TOOL";
		this.toolClass = TileTool.class;
		
		this.readableName = "Outil tuile";
		this.optionPanelClass = TileToolOptionPanel.class;
		this.accelerator = shortcuts.TILE_TOOL;
		this.icon = GuiIcons.TOOL_TILE;
	}

}
