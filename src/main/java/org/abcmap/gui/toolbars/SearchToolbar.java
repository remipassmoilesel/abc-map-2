package org.abcmap.gui.toolbars;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.search.IESearchTextField;
import org.abcmap.gui.utils.GuiUtils;

public class SearchToolbar extends Toolbar {

    public SearchToolbar() {
        setLayout(new MigLayout("insets 6, gap 6"));
        GuiUtils.addLabel("Rechercher: ", this);
        add(new IESearchTextField(), "width 100px!, height 30px!");
    }

}
