package abcmap.draw.tools.containers;

import abcmap.draw.shapes.Polyline;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.PolypointShapeTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.PolypointToolOptionPanel;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;

public class PolylineTC extends ToolContainer {
	public PolylineTC() {
		this.id = "POLYLINE_TOOL";
		this.readableName = "Outil polyligne";
		this.accelerator = shortcuts.POLYLINE_TOOL;
		this.icon = GuiIcons.TOOL_POLYLINE;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Créer une forme", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Ajouter des points", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Terminer une forme", Interaction.DOUBLE_CLICK),

		};
	}

	@Override
	public MapTool getNewInstance() {
		currentInstance = new PolypointShapeTool(Polyline.class);
		return currentInstance;
	}

	@Override
	protected ToolOptionPanel createToolOptionPanel() {
		return new PolypointToolOptionPanel(Polyline.class);
	}
}