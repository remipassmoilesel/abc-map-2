package org.abcmap.gui.components.dock;

import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Button on top of dock used to navigate in dock history or show help
 */
public class DockNavButton extends JLabel {

    public static final String EXPAND_HELP = "EXPAND_HELP";
    public static final String NEXT = "NEXT";
    public static final String PREVIOUS = "PREVIOUS";
    public static final String CLOSE = "CLOSE";
    private String type;
    private GuiManager guim;
    private boolean expandHelp;

    private Dock dockParent;
    private Container widgetSpace;

    public DockNavButton(String type) {
        super();

        this.guim = Main.getGuiManager();

        this.type = type;

        this.expandHelp = false;

        ImageIcon ic = null;
        if (EXPAND_HELP.equals(type)) {
            ic = GuiIcons.DI_NAVB_EXPAND_INFOS;
        } else if (NEXT.equals(type)) {
            ic = GuiIcons.DI_NAVB_NEXT;
        } else if (PREVIOUS.equals(type)) {
            ic = GuiIcons.DI_NAVB_PREVIOUS;
        } else if (CLOSE.equals(type)) {
            ic = GuiIcons.DI_NAVB_CLOSE;
        }
        this.setIcon(ic);

        addMouseListener(new ClickListener());

        setCursor(guim.getClickableCursor());

    }

    public void checkValidity() {

        if (dockParent == null) {
            dockParent = (Dock) GuiUtils.searchParentOf(this, Dock.class);
            if (dockParent == null)
                return;
        }

        int i = dockParent.getWidgetSpaceSupport().getIndex();
        int h = dockParent.getWidgetSpaceSupport().getHistorySize();

        if (PREVIOUS.equals(type)) {
            this.setEnabled(i > 0);
        } else if (NEXT.equals(type)) {
            this.setEnabled(i < h - 1);
        }

    }

    private class ClickListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {

            if (dockParent == null) {
                dockParent = (Dock) GuiUtils.searchParentOf(DockNavButton.this, Dock.class);
                if (dockParent == null)
                    return;
            }

            if (widgetSpace == null) {
                widgetSpace = (Container) GuiUtils.searchParentOf(DockNavButton.this,
                        DockWidgetSpaceSupport.class);
                if (widgetSpace == null)
                    return;
            }

            if (NEXT.equals(type)) {
                dockParent.displayNextWidgetspaceAvailable();
            } else if (PREVIOUS.equals(type)) {
                dockParent.displayPreviousWidgetspaceAvailable();
            } else if (CLOSE.equals(type)) {
                dockParent.hideWidgetspace();
            } else if (EXPAND_HELP.equals(type)) {
                expandHelp = !expandHelp;
                Dock.expandHelpRecursively(widgetSpace, expandHelp);
            }

            checkValidity();

        }

    }

}
