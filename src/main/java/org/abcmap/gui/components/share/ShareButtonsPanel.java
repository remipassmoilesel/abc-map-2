package org.abcmap.gui.components.share;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;

import javax.swing.*;

public class ShareButtonsPanel extends JPanel {

	public ShareButtonsPanel() {
		super(new MigLayout("insets 10, gap 10"));
		construct();
	}

	public void construct() {
		ImageIcon[] shareIcons = new ImageIcon[] { GuiIcons.SOCIAL_FACEBOOK,
				GuiIcons.SOCIAL_GOOGLE, GuiIcons.SOCIAL_TWEETER,
				GuiIcons.SOCIAL_LINKEDIN, GuiIcons.SOCIAL_YOUTUBE,
				GuiIcons.SOCIAL_PINTEREST, };

		int i = 0;
		for (ImageIcon icon : shareIcons) {
			String cst = i == shareIcons.length / 2 - 1 ? "wrap" : "";
			add(new JButton(icon), cst);

			i++;
		}

		revalidate();
		repaint();
	}
}
