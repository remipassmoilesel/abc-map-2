package abcmap.gui.toolOptionPanels;

import javax.swing.JButton;

import abcmap.gui.comps.buttons.HtmlCheckbox;
import abcmap.utils.gui.GuiUtils;

public class TileToolOptionPanel extends ToolOptionPanel {

	public TileToolOptionPanel() {

		GuiUtils.throwIfNotOnEDT();

		// bouton de reanalyse de tuiles
		GuiUtils.addLabel("Analyse de tuiles: ", this, "wrap");

		JButton btnAnalyseTiles = new JButton(
				"Analyser les tuiles sélectionnées");
		add(btnAnalyseTiles, gapLeft + largeWrap);

		// montrer les points d'interet
		HtmlCheckbox chkDisplayIpts = new HtmlCheckbox(
				"Afficher les points d'interet");
		add(chkDisplayIpts, largeWrap);

	}

}
