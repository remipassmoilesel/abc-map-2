package abcmap.draw.tools.containers;

import abcmap.draw.tools.LabelTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.toolOptionPanels.LabelToolOptionPanel;

public class LabelTC extends ToolContainer {
	public LabelTC() {
		this.id = "LABEL_TOOL";
		this.toolClass = LabelTool.class;

		this.readableName = "Outil texte";
		this.optionPanelClass = LabelToolOptionPanel.class;
		this.accelerator = shortcuts.LABEL_TOOL;
		this.icon = GuiIcons.TOOL_TEXT;
	}

}