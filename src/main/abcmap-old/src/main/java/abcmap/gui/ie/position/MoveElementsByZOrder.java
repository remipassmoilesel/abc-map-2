package abcmap.gui.ie.position;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.ie.InteractionElement;
import net.miginfocom.swing.MigLayout;

/**
 * Panneau regroupant tous les changements possibles d'ordre Z. Utilise les
 * fonctionnalités des commandes dérivées de AbstractCHnageZOrder
 * 
 * @author remipassmoilesel
 *
 */
public class MoveElementsByZOrder extends InteractionElement {

	public MoveElementsByZOrder() {
		this.label = "Changer la position en profondeur";
		this.help = "Utilisez les boutons ci-dessous pour changer la position des objets sélectionnés "
				+ "en profondeur.";

		// affichage particulier lors des recherches
		this.displaySimplyInSearch = false;
	}

	@Override
	protected Component createPrimaryGUI() {

		InteractionElement[] elements = new InteractionElement[] { new MoveElementsUp(),
				new MoveElementsDown(), new MoveElementsTop(), new MoveElementsBottom(), };

		JPanel panel = new JPanel(new MigLayout("insets 2, gap 2"));
		for (InteractionElement ie : elements) {

			// creation du bouton
			JButton button = new JButton(ie.getMenuIcon());
			button.setToolTipText(ie.getLabel());
			button.addActionListener(ie);

			panel.add(button, "height 35px!, width 35px!");
		}

		return panel;
	}

}
