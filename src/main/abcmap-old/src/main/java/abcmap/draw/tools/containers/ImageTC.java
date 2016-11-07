package abcmap.draw.tools.containers;

import abcmap.draw.shapes.Image;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.RectangleShapeTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.RectangleShapeToolOptionPanel;
import abcmap.gui.toolOptionPanels.ToolOptionPanel;

public class ImageTC extends ToolContainer {

	public ImageTC() {

		this.id = "IMAGE_TOOL";
		this.readableName = "Outil images";
		this.optionPanelClass = null;
		this.accelerator = shortcuts.IMAGE_TOOL;
		this.icon = GuiIcons.TOOL_IMAGE;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Sélectionner un élément", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Sélectionner plusieurs éléments",
						new Interaction[] { Interaction.PRESS_SHIFT, Interaction.SIMPLE_CLICK }), };

	}

	@Override
	public MapTool getNewInstance() {
		currentInstance = new RectangleShapeTool(Image.class, false);
		return currentInstance;
	}
	
	@Override
	protected ToolOptionPanel createToolOptionPanel() {
		return new RectangleShapeToolOptionPanel(Image.class);
	}
}
