package abcmap.draw.tools.containers;

import abcmap.draw.tools.LegendTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.LegendToolOptionPanel;

public class LegendTC extends ToolContainer {
	public LegendTC() {
		this.id = "LEGEND_TOOL";
		this.readableName = "Outil de légende";
		this.toolClass = LegendTool.class;
		this.accelerator = shortcuts.LEGEND_TOOL;
		this.optionPanelClass = LegendToolOptionPanel.class;
		this.icon = GuiIcons.TOOL_LEGEND;
	}
}
