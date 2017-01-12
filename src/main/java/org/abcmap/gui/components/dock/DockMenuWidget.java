package org.abcmap.gui.components.dock;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.CustomComponent;
import org.abcmap.gui.components.buttons.HtmlLabel;
import org.abcmap.gui.components.dock.blockitems.DockMenuPanel;
import org.abcmap.gui.windows.MainWindowMode;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * COmponent designed to be used in dock. Click on it open side menu.
 */
public class DockMenuWidget extends CustomComponent {

    /**
     * Listener used to open menu on click
     */
    protected final WidgetActionListener openMenuListener;

    /**
     * Side menu
     */
    protected DockMenuPanel menuPanel;

    /**
     * Element of menu
     */
    protected GroupOfInteractionElements groupOfInteractionElement;

    /**
     * Widget icon
     */
    protected JLabel lblIcon;

    /**
     * Parent dock, lazy initialized
     */
    protected Dock dock;

    /**
     * Main window mode associated with dock
     */
    protected MainWindowMode windowMode;

    protected GuiManager guim;

    public DockMenuWidget() {

        this.guim = Main.getGuiManager();

        setLayout(new MigLayout("insets 5"));
        setOpaque(true);

        this.menuPanel = null;

        this.windowMode = MainWindowMode.SHOW_MAP;

        lblIcon = new JLabel(GuiIcons.DEFAULT_GROUP_ICON);
        add(lblIcon);

        openMenuListener = new WidgetActionListener();
        addActionListener(openMenuListener);

    }

    /**
     * Open menu on user click
     */
    private class WidgetActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            getDockParent().showWidgetspace(menuPanel);

            getDockParent().setMenuSelected(DockMenuWidget.this);

            guim.getMainWindow().setWindowMode(windowMode);
        }
    }

    /**
     * Return dock parent of this component
     *
     * @return
     */
    protected Dock getDockParent() {
        if (dock == null) {
            dock = Dock.getDockParentForComponent(DockMenuWidget.this);
        }
        return dock;
    }

    /**
     * Set window mode used by this menu. This window mode will be enabled when menu is open.
     *
     * @param windowMode
     */
    public void setWindowMode(MainWindowMode windowMode) {
        this.windowMode = windowMode;
    }

    /**
     * Set icon of menu. User have to click on this icon to open menu
     *
     * @param icon
     */
    public void setIcon(ImageIcon icon) {

        if (icon == null) {
            throw new NullPointerException("Icon cannot be null");
        }

        lblIcon.setIcon(icon);
        lblIcon.revalidate();
        lblIcon.repaint();
    }

    /**
     * Set the group of interaction elements displayed in this menu
     *
     * @param ieg
     */
    public void setGroupOfInteractionElement(GroupOfInteractionElements ieg) {

        this.groupOfInteractionElement = ieg;

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

    /**
     * Return the group of interaction elements displayed here
     *
     * @return
     */
    public GroupOfInteractionElements getGroupOfInteractionElement() {
        return groupOfInteractionElement;
    }

    /**
     * Return panel used to display group of interaction elements
     *
     * @return
     */
    public DockMenuPanel getMenuPanel() {
        return menuPanel;
    }

}
