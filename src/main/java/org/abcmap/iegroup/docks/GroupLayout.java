package org.abcmap.iegroup.docks;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.layouts.ManageLayouts;
import org.abcmap.gui.windows.MainWindowMode;

public class GroupLayout extends GroupOfInteractionElements {
    public GroupLayout() {
        label = "Mise en page";
        blockIcon = GuiIcons.GROUP_LAYOUT;

        windowMode = MainWindowMode.SHOW_LAYOUTS;

        addInteractionElement(new ManageLayouts());
    }
}
