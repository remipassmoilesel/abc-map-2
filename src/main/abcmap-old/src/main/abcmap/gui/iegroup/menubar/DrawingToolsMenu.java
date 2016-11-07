package abcmap.gui.iegroup.menubar;

import java.awt.event.ActionEvent;

import abcmap.draw.tools.containers.ToolContainer;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;

public class DrawingToolsMenu extends InteractionElementGroup {

	public DrawingToolsMenu() {

		label = "Outils de dessin";

		// recuperer la liste des outils
		ToolContainer[] tcs = ToolLibrary.getAvailablesTools();

		// iterer les outils
		for (ToolContainer tc : tcs) {

			// creer un element d'interaction pour l'outil
			ToolInteractionElement ie = new ToolInteractionElement(tc);

			// ajouter l'element
			addInteractionElement(ie);
		}

	}

	private class ToolInteractionElement extends InteractionElement {

		private ToolContainer toolc;

		public ToolInteractionElement(ToolContainer tc) {

			this.toolc = tc;

			this.label = tc.getReadableName();
			this.accelerator = tc.getAccelerator();
			this.menuIcon = tc.getIcon();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			super.actionPerformed(e);

			drawm.setCurrentTool(toolc);
		}
	}

}
