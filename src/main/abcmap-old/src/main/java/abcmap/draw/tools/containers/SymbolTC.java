package abcmap.draw.tools.containers;

import abcmap.draw.tools.SymbolTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.SymbolToolOptionPanel;

public class SymbolTC extends ToolContainer {

	public SymbolTC() {
		this.id = "SYMBOL_TOOL";
		this.toolClass = SymbolTool.class;

		this.readableName = "Outil symboles";
		this.optionPanelClass = SymbolToolOptionPanel.class;
		this.accelerator = shortcuts.SYMBOL_TOOL;
		this.icon = GuiIcons.TOOL_SYMBOL;
	}

}
