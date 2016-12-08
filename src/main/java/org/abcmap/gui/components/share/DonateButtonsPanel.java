package org.abcmap.gui.components.share;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;

import javax.swing.*;

public class DonateButtonsPanel extends JPanel {

	public DonateButtonsPanel() {
		super(new MigLayout("insets 10, gap 10"));

		add(new JButton(GuiIcons.SOCIAL_PAYPAL));
		add(new JButton(GuiIcons.SOCIAL_WWW));
	}
}
