package org.abcmap.gui.components.dock.blockitems;

import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.gui.utils.GuiUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SubMenuItem extends SimpleBlockItem {

    private GroupOfInteractionElements interactionGroup;

    protected DockMenuPanel subMenu;

    private Dock dock;

    public SubMenuItem(GroupOfInteractionElements group) {
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

    public void setInteractionGroup(GroupOfInteractionElements interactionGroup) {
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
