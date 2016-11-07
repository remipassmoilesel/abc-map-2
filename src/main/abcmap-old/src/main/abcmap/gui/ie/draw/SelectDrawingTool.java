package abcmap.gui.ie.draw;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import abcmap.gui.comps.draw.ToolSelectionPanel;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.Utils;

public class SelectDrawingTool extends InteractionElement {

	public SelectDrawingTool() {
		label = "Outils";
		help = "Sélectionnez ci-dessous l'outil que vous souhaitez utiliser. Le fonctionnement "
				+ "de chaque outil est détaillé "
				+ "dans le panneau d'aide de l'outil actif. Si l'outil possède des options, "
				+ "vous pouvez les modifier dans le panneau d'options de l'outil.";

		displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {
		ToolSelectionPanel panel = new ToolSelectionPanel();
		panel.addActionListener(new ChangeToolActionListener());
		return panel;
	}

	private class ChangeToolActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			// l'outil du manager
			String managerToolId = drawm.getCurrentToolContainer().getId();

			// l'outil appelant
			String toolId = e.getActionCommand();

			if (Utils.safeEquals(managerToolId, toolId) == false) {
				drawm.setCurrentTool(toolId);
			}
		}

	}

}
