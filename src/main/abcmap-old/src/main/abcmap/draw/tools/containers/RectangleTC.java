package abcmap.draw.tools.containers;

import abcmap.draw.shapes.Rectangle;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.RectangleShapeTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.RectangleShapeToolOptionPanel;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;

public class RectangleTC extends ToolContainer {
	public RectangleTC() {
		this.id = "RECTANGLE_TOOL";
		this.readableName = "Outil rectangle";
		this.accelerator = shortcuts.RECTANGLE_TOOL;
		this.icon = GuiIcons.TOOL_RECTANGLE;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Créer une forme", Interaction.DRAG),

				new InteractionSequence("Créer une forme proportionnelle",
						new Interaction[] { Interaction.DRAG, Interaction.PRESS_CONTROL }),

		};
	}

	@Override
	public MapTool getNewInstance() {
		currentInstance = new RectangleShapeTool(Rectangle.class);
		return currentInstance;
	}

	@Override
	protected ToolOptionPanel createToolOptionPanel() {
		return new RectangleShapeToolOptionPanel(Rectangle.class);
	}
}