package abcmap.gui.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import abcmap.gui.GuiIcons;
import abcmap.gui.comps.CustomComponent;
import abcmap.gui.ie.InteractionElement;
import abcmap.utils.threads.ThreadManager;
import net.miginfocom.swing.MigLayout;

public class ToolbarButton extends CustomComponent {

	private InteractionElement interactionElement;

	public ToolbarButton(InteractionElement ie) {

		setLayout(new MigLayout("insets 3"));

		interactionElement = ie;

		// icones
		JLabel icon = new JLabel(
				interactionElement.getMenuIcon() != null ? interactionElement
						.getMenuIcon() : GuiIcons.DEFAULT_TOOLBAR_BUTTON_ICON);
		icon.setOpaque(false);
		add(icon, "dock center");

		setToolTipText(ie.getLabel());

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ThreadManager.runLater(interactionElement);
			}
		});
	}

}
