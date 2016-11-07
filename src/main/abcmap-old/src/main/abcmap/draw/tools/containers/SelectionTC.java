package abcmap.draw.tools.containers;

import abcmap.draw.tools.SelectionTool;
import abcmap.gui.GuiIcons;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.comps.help.InteractionSequence;
import abcmap.gui.toolOptionPanels.SelectionToolOptionPanel;

public class SelectionTC extends ToolContainer {
	public SelectionTC() {

		this.id = "SELECTION_TOOL";
		this.toolClass = SelectionTool.class;
		
		this.readableName = "Outil sélection";
		this.optionPanelClass = SelectionToolOptionPanel.class;
		this.accelerator = shortcuts.SELECTION_TOOL;
		this.icon = GuiIcons.TOOL_SELECTION;

		this.interactionsSequences = new InteractionSequence[] {

				new InteractionSequence("Sélectionner un élément", Interaction.SIMPLE_CLICK),

				new InteractionSequence("Sélectionner dans un cadre", new Interaction[] { Interaction.DRAG, }),

				new InteractionSequence("Sélectionner plusieurs éléments",
						new Interaction[] { Interaction.PRESS_SHIFT, Interaction.SIMPLE_CLICK }),

		};

	}

}
