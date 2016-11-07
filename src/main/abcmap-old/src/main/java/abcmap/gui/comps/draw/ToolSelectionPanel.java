package abcmap.gui.comps.draw;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;
import abcmap.draw.tools.containers.ToolContainer;
import abcmap.draw.tools.containers.ToolLibrary;

public class ToolSelectionPanel extends JPanel {

	private static final Integer TOOLS_PER_LINE = 3;
	private ArrayList<JToggleButton> buttons;

	public ToolSelectionPanel() {
		super();

		// layout manager
		setLayout(new MigLayout("gap 5, insets 5"));

		// lister les outils disponibles
		ToolContainer[] tools = ToolLibrary.getAvailablesTools();

		// groupe pour toggle buttons
		ButtonGroup bg = new ButtonGroup();
		buttons = new ArrayList<>();

		// initialisation a 1 pour colonnes
		int i = 1;

		// itérer les outils pour affichage
		for (ToolContainer tc : tools) {

			// recuperation des informations
			ImageIcon icon = tc.getIcon();
			String tip = tc.getReadableName();

			// creation du bouton
			JToggleButton bt = new JToggleButton(icon);
			bt.setToolTipText(tip);
			bt.setActionCommand(tc.getId());

			// ajout au groupe de boutons
			bg.add(bt);
			buttons.add(bt);

			// ajout du bouton avec eventuel retour a la ligne
			String csts = i % TOOLS_PER_LINE == 0 ? "wrap," : "";
			this.add(bt, csts + "width 50px!");

			i++;
		}

	}

	public void addActionListener(ActionListener listener) {
		for (JToggleButton btn : buttons) {
			btn.addActionListener(listener);
		}
	}

}
