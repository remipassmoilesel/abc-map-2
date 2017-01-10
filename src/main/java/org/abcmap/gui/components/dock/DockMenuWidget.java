package org.abcmap.gui.components.dock;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.CustomComponent;
import org.abcmap.gui.components.buttons.HtmlLabel;
import org.abcmap.gui.components.dock.blockitems.DockMenuPanel;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.gui.windows.MainWindowMode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * COmponent designed to be used in dock. Click on it open side menu.
 */
public class DockMenuWidget extends CustomComponent {

    /**
     * Side menu
     */
    private DockMenuPanel menuPanel;

    /**
     * Element of menu
     */
    private GroupOfInteractionElements interactionElementGroup;

    /**
     * Widget icon
     */
    private JLabel lblIcon;

    /**
     * Parent dock, lazy initialized
     */
    private Dock dock;

    /**
     * Main window mode associated with dock
     */
    private MainWindowMode windowMode;

    private GuiManager guim;

    public DockMenuWidget() {

        this.guim = Main.getGuiManager();

        setLayout(new MigLayout("insets 5"));
        setOpaque(true);

        this.menuPanel = null;

        this.windowMode = MainWindowMode.SHOW_MAP;

        lblIcon = new JLabel(GuiIcons.DEFAULT_GROUP_ICON);
        add(lblIcon);

        addActionListener(new WidgetActionListener());

    }

    /**
     * Open menu on user click
     */
    private class WidgetActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            // get parent
            if (dock == null) {
                dock = Dock.getDockParentForComponent(DockMenuWidget.this);
            }

            dock.showWidgetspace(menuPanel);

            dock.setMenuSelected(DockMenuWidget.this);

            guim.getMainWindow().setWindowMode(windowMode);
        }
    }

    public void setWindowMode(MainWindowMode windowMode) {
        this.windowMode = windowMode;
    }

    public void setIcon(ImageIcon icon) {

        if (icon == null) {
            throw new NullPointerException("Icon cannot be null");
        }

        lblIcon.setIcon(icon);
        lblIcon.revalidate();
        lblIcon.repaint();
    }

    public void setInteractionElementGroup(GroupOfInteractionElements ieg) {

        this.interactionElementGroup = ieg;

        setIcon(ieg.getBlockIcon());
        setToolTipText(ieg.getLabel());

        // create menu panel
        menuPanel = new DockMenuPanel();

        // panel title
        menuPanel.setMenuTitle(ieg.getLabel());

        // description
        menuPanel.setMenuDescription(ieg.getHelp());

        // add elements
        for (InteractionElement e : ieg.getElements()) {

            // add separator
            if (GroupOfInteractionElements.isSeparator(e)) {
                menuPanel.addMenuElement(new HtmlLabel(" "));
            }

            // or add element
            else {
                menuPanel.addMenuElement(e);
            }

        }

        menuPanel.reconstruct();
    }

    public GroupOfInteractionElements getInteractionElementGroup() {
        return interactionElementGroup;
    }

    public DockMenuPanel getMenuPanel() {
        return menuPanel;
    }

}
