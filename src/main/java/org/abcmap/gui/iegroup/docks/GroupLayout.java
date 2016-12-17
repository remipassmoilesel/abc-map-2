package org.abcmap.gui.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.layouts.ManageLayouts;
import org.abcmap.gui.windows.MainWindowMode;

public class GroupLayout extends InteractionElementGroup {
    public GroupLayout() {
        label = "Mise en page";
        blockIcon = GuiIcons.GROUP_LAYOUT;

        windowMode = MainWindowMode.SHOW_LAYOUTS;

        addInteractionElement(new ManageLayouts());
    }
}
