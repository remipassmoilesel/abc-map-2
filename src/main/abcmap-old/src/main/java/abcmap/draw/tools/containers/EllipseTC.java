package abcmap.draw.tools.containers;

import abcmap.draw.shapes.Ellipse;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.RectangleShapeTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.RectangleShapeToolOptionPanel;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;

public class EllipseTC extends ToolContainer {
	public EllipseTC() {
		this.id = "ELLIPSE_TOOL";
		this.readableName = "Outil ellipse";
		this.accelerator = shortcuts.ELLIPSE_TOOL;
		this.icon = GuiIcons.TOOL_ELLIPSE;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Créer une forme", Interaction.DRAG),

				new InteractionSequence("Créer une forme proportionnelle",
						new Interaction[] { Interaction.DRAG, Interaction.PRESS_CONTROL }),

		};

	}

	@Override
	public MapTool getNewInstance() {
		currentInstance = new RectangleShapeTool(Ellipse.class);
		return currentInstance;
	}

	@Override
	protected ToolOptionPanel createToolOptionPanel() {
		return new RectangleShapeToolOptionPanel(Ellipse.class);
	}
}