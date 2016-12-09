package org.abcmap.gui.components.dock.blockitems;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import abcmap.gui.GuiCursor;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.utils.Refreshable;
import abcmap.utils.gui.GuiUtils;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.gui.toProcess.gui.iegroup.InteractionElementGroup;
import org.abcmap.gui.utils.GuiUtils;

public class SubMenuItem extends SimpleBlockItem {

    private InteractionElementGroup interactionGroup;

    protected DockMenuPanel subMenu;

    private Dock dock;

    public SubMenuItem(InteractionElementGroup group) {
        super(group);

        this.interactionGroup = group;

        changeColorUnderFocus(true);

        labelName.addMouseListener(new SubMenuMouseListener());

        setCursor(GuiCursor.HAND_CURSOR);

        reconstruct();
        refresh();
    }

    @Override
    public void reconstruct() {

        subMenu = new DockMenuPanel();

        subMenu.setMenuTitle(interactionGroup.getLabel());

        for (InteractionElement e : interactionGroup.getElements()) {
            subMenu.addMenuElement(e);
        }

        subMenu.reconstruct();
    }

    public void setInteractionGroup(InteractionElementGroup interactionGroup) {
        this.interactionGroup = interactionGroup;
    }

    private void showSubMenu() {

        GuiUtils.throwIfNotOnEDT();

        if (dock == null) {
            dock = Dock.getDockParentForComponent(this);
        }

        dock.showWidgetspace(subMenu);
    }

    private class SubMenuMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            showSubMenu();
        }

    }

}
