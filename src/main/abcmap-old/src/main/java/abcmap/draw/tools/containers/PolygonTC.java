package abcmap.draw.tools.containers;

import abcmap.draw.shapes.Polygon;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.PolypointShapeTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.PolypointToolOptionPanel;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;

public class PolygonTC extends ToolContainer {
	public PolygonTC() {
		this.id = "POLYGON_TOOL";
		this.readableName = "Outil polygone";
		this.accelerator = shortcuts.POLYGON_TOOL;
		this.icon = GuiIcons.TOOL_POLYGON;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Créer une forme", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Ajouter des points", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Terminer une forme", Interaction.DOUBLE_CLICK),

		};

	}

	@Override
	public MapTool getNewInstance() {
		currentInstance = new PolypointShapeTool(Polygon.class);
		return currentInstance;
	}

	@Override
	protected ToolOptionPanel createToolOptionPanel() {
		return new PolypointToolOptionPanel(Polygon.class);
	}
}