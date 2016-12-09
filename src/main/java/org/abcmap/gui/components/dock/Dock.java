package org.abcmap.gui.components.dock;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.toProcess.gui.iegroup.InteractionElementGroup;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.HasDisplayableSpace;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Dock extends JPanel implements HasDisplayableSpace {

    public static final int DOCK_WIDTH = 60;
    public static final int MENU_ITEM_WIDTH = 260;
    public static final int VERTICAL_SCROLLBAR_UNIT_INCREMENT = 50;
    public static final Color DOCK_BACKGROUND = new Color(240, 240, 240);

    /**
     * Panel where are displayed widgets, e.g: button to open side panel
     */
    private JPanel widgetIconsPanel;

    /**
     * Where are displayed menu elements
     */
    private DockWidgetSpacePanel widgetSpacePanel;

    /**
     * Dock orientation: E / W
     */
    private DockOrientation orientation;

    private GuiManager guim;

    public Dock(DockOrientation orientation) {

        guim = MainManager.getGuiManager();

        setLayout(new MigLayout("insets 0, gap 3, fill"));

        this.orientation = orientation;

        widgetIconsPanel = new JPanel(new MigLayout("insets 20 5 5 5, gapy 20"));
        widgetIconsPanel.setOpaque(false);
        widgetIconsPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        String place = orientation.equals(DockOrientation.WEST) ? "dock west" : "dock east";
        add(widgetIconsPanel, "grow, width " + DOCK_WIDTH + ", " + place);

        widgetSpacePanel = new DockWidgetSpacePanel(orientation);
    }

    public DockWidgetSpacePanel getWidgetSpacePanel() {
        return widgetSpacePanel;
    }

    public void displayNextWidgetspaceAvailable() {
        widgetSpacePanel.displayNext();
    }

    public void displayPreviousWidgetspaceAvailable() {
        widgetSpacePanel.displayPrevious();
    }

    /**
     * Display a component in dock side panel. This is the only method to use for display a component in dock, to keep it in history.
     *
     * @param widgetsp
     */
    public void showWidgetspace(Component widgetsp) {

        widgetSpacePanel.displayNew(widgetsp);

        boolean alreadyHere = Arrays.asList(getComponents()).contains(
                widgetSpacePanel);

        if (alreadyHere == false) {
            add(widgetSpacePanel, "dock center");
        }

        widgetsp.revalidate();
        widgetsp.repaint();

        widgetSpacePanel.refresh();

        refresh();

    }

    @Override
    public void displayComponent(JComponent comp) {
        showWidgetspace(comp);
    }

    public void hideWidgetspace() {

        GuiUtils.throwIfNotOnEDT();

        remove(widgetSpacePanel);

        setAllMenuSelected(false);

        refresh();
    }

    public void addWidget(JComponent widget) {
        widgetIconsPanel.add(widget, "grow, wrap");
    }

    public void refresh() {
        revalidate();
        repaint();
    }

    public DockOrientation getOrientation() {
        return orientation;
    }

    /**
     * Use instead showWidgetspace(Component widgetsp)
     *
     * @param comp
     * @return
     */
    @Deprecated
    @Override
    public Component add(Component comp) {
        return super.add(comp);
    }

    /**
     * Use instead showWidgetspace(Component widgetsp)
     *
     * @param comp
     * @return
     */
    @Deprecated
    @Override
    public Component add(String name, Component comp) {
        return super.add(name, comp);
    }

    /**
     * Use instead showWidgetspace(Component widgetsp)
     *
     * @param comp
     * @return
     */
    @Deprecated
    @Override
    public Component add(Component comp, int index) {
        return super.add(comp, index);
    }

    /**
     * Use instead showWidgetspace(Component widgetsp)
     *
     * @param comp
     * @return
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    /**
     * Use instead showWidgetspace(Component widgetsp)
     *
     * @param comp
     * @return
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, constraints, index);
    }

    /**
     * Utility to expand/unexpand help recursively
     *
     * @param cont
     * @param val
     */
    public static void expandHelpRecursively(Container cont, boolean val) {

        if (cont instanceof HasExpandableHelp) {
            HasExpandableHelp he = (HasExpandableHelp) cont;
            if (he.isHelpExpanded() != val) {
                he.expandHelp(val);
                cont.revalidate();
                cont.repaint();
            }
        }

        for (Component c : cont.getComponents()) {

            if (c instanceof Container)
                expandHelpRecursively((Container) c, val);

            else if (c instanceof HasExpandableHelp) {
                HasExpandableHelp he = (HasExpandableHelp) c;
                if (he.isHelpExpanded() != val) {
                    he.expandHelp(val);
                    c.revalidate();
                    c.repaint();
                }
            }

        }
    }

    /**
     * Retrouve et afficher le premier menu de dock correspondant à la classe du
     * groupe d'interaction passé en paramètre, ou retourne faux si aucun menu
     * n'est trouvé.
     * <p>
     * Le mode de la fenêtre principale sera modifié en conséquence.
     *
     * @param iegClass
     * @return
     */

    /**
     * Find and show first panel composed of specified class.
     * <p>
     * Main window mode will be changed
     *
     * @param iegClass
     * @return
     */
    public boolean showFirstMenuPanelCorrespondingThis(Class<? extends InteractionElementGroup> iegClass) {
        return showFirstMenuPanelCorrespondingThis(iegClass.getSimpleName());
    }

    /**
     * Find and show first panel composed of specified class.
     * <p>
     * Main window mode will be changed
     *
     * @param className
     * @return
     */
    public boolean showFirstMenuPanelCorrespondingThis(String className) {

        for (Component c : widgetIconsPanel.getComponents()) {

            // widget is a menu
            if (c instanceof DockMenuWidget) {

                // comparer les noms de classes
                DockMenuWidget mw = (DockMenuWidget) c;
                if (className.equals(mw.getInteractionElementGroup().getClass().getSimpleName())) {

                    // change window mode if necessary
                    guim.setMainWindowMode(mw.getInteractionElementGroup().getWindowMode());

                    // afficher l'espace
                    showWidgetspace(mw.getMenuPanel());

                    return true;
                }
            }

        }

        return false;

    }

    /**
     * Return menu containing specified component or null if nothin is found
     *
     * @param comp
     * @return
     */
    public DockMenuWidget getMenuWidgetParentOf(Component comp) {
        return (DockMenuWidget) GuiUtils.searchParentOf(comp, DockMenuWidget.class);
    }

    public void setMenuSelected(DockMenuWidget menu) {
        GuiUtils.throwIfNotOnEDT();
        setAllMenuSelected(false);
        menu.setSelected(true);
    }

    private void setAllMenuSelected(boolean val) {
        for (Component c : widgetIconsPanel.getComponents()) {
            if (c instanceof DockMenuWidget) {

                DockMenuWidget dwm = (DockMenuWidget) c;
                dwm.setSelected(val);

                dwm.revalidate();
                dwm.repaint();
            }
        }
    }

    /**
     * Return parent of specified component or null if nothing is found
     *
     * @param comp
     * @return
     */
    public static Dock getDockParentForComponent(Component comp) {
        return (Dock) GuiUtils.searchParentOf(comp, Dock.class);
    }

}
