package abcmap.gui.dock.comps.blockitems;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.buttons.HtmlLabel;
import abcmap.utils.Refreshable;

public class BlockItemHelpArea extends JPanel implements Refreshable {

	private HtmlLabel labelHelp;

	public BlockItemHelpArea() {
		super(new MigLayout("insets 5"));
		GuiStyle.applyStyleTo(GuiStyle.SIMPLE_BLOCK_ITEM_HELP, this);

		labelHelp = new HtmlLabel("No help");
		labelHelp.setStyle(GuiStyle.SIMPLE_BLOCK_ITEM_HELP);

		add(labelHelp, "grow");
	}

	public BlockItemHelpArea(String help) {
		this();
		setText(help);
	}

	public void setText(String help) {
		labelHelp.setText(help);
	}

	public void refresh() {
		revalidate();
		repaint();
	}

	@Override
	public void reconstruct() {
		refresh();
	}

}
