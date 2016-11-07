package abcmap.gui.comps.share;

import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.gui.GuiIcons;
import net.miginfocom.swing.MigLayout;

public class DonateButtonsPanel extends JPanel {

	public DonateButtonsPanel() {
		super(new MigLayout("insets 10, gap 10"));

		add(new JButton(GuiIcons.SOCIAL_PAYPAL));
		add(new JButton(GuiIcons.SOCIAL_WWW));
	}
}
