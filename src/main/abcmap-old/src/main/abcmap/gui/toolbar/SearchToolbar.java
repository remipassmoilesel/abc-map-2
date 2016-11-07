package abcmap.gui.toolbar;

import net.miginfocom.swing.MigLayout;
import abcmap.gui.comps.textfields.search.CommandSearchTextField;
import abcmap.utils.gui.GuiUtils;

public class SearchToolbar extends Toolbar {

	public SearchToolbar() {
		setLayout(new MigLayout("insets 6, gap 6"));
		GuiUtils.addLabel("Rechercher: ", this);
		add(new CommandSearchTextField(), "width 100, height 30");
	}

}
