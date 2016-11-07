package abcmap.gui.ie.align;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import net.miginfocom.swing.MigLayout;

public class AlignAndDistribute extends InteractionElement {

	public AlignAndDistribute() {
		label = "Aligner et distribuer des objets";
		help = "Choisissez un bouton ci dessous pour aligner et distribuer les formes sélectionnées.";

		// affichage particulier lors des recherches
		displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		InteractionElement[] elements = new InteractionElement[] { new AlignTop(),
				new AlignBottom(), new AlignLeft(), new AlignRight(), new AlignMiddleHorizontal(),
				new AlignMiddleVertical(), new DistributeHorizontal(), new DistributeVertical(), };

		JPanel panel = new JPanel(new MigLayout("insets 2, gap 5"));
		int i = 1;
		for (InteractionElement ie : elements) {

			// creation du bouton
			JButton button = new JButton(ie.getMenuIcon());
			button.setToolTipText(ie.getLabel());
			button.addActionListener(ie);

			String csts = i % 4 == 0 ? "wrap" : "";

			panel.add(button, "height 35px!, width 35px!, " + csts);

			i++;
		}

		return panel;
	}

}
