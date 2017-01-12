package org.abcmap.gui.components.dock.blockitems;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DockMenuPanel extends JPanel {

    /**
     * Width in percent of items inserted in panel
     */
    private int menuItemWidthPercent = 95;

    /**
     * Elements of menu. These elements can be of different type
     */
    protected ArrayList<Object> elements;

    /**
     * Title displayed on the top of panel
     */
    protected String title;

    /**
     * Small description displayed on top of panel
     */
    protected String description;

    public DockMenuPanel() {
        super(new MigLayout("fillx, insets 5 5 5 5"));

        this.title = "No title";
        this.description = null;
        this.elements = new ArrayList<>();

        reconstruct();
    }

    /**
     * Reconstruct panel
     */
    public void reconstruct() {

        removeAll();

        JPanel metadatas = new JPanel(new MigLayout("insets 5"));

        GuiUtils.addLabel(title, metadatas, "width 95%!, wrap", GuiStyle.DOCK_MENU_TITLE_1);

        if (description != null && description.isEmpty() == false) {

            JEditorPane desc = new JEditorPane("text/html", "<html>" + description + "</html>");

            desc.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
            desc.setOpaque(false);
            desc.setEditable(false);
            GuiStyle.applyStyleTo(GuiStyle.DOCK_MENU_DESCRIPTION, desc);

            metadatas.add(desc, "width 95%!");
        }

        addComponent(metadatas);

        for (Object o : elements) {

            // add a swing components
            if (o instanceof Component) {
                addComponent((Component) o);
            }

            //  add an interaction element
            else if (o instanceof InteractionElement) {
                addComponent(((InteractionElement) o).getBlockGUI());
            }

            // unknown element
            else {
                throw new IllegalStateException("Unknown element type: " + o);
            }

        }

        refresh();
    }

    /**
     * Add a swing component with normalized width
     *
     * @param c
     */
    protected void addComponent(Component c) {
        super.add(c, "width " + menuItemWidthPercent + "%!, wrap");
    }

    /**
     * Add interaction element to this menu
     *
     * @param o
     */
    public void addMenuElement(InteractionElement o) {
        elements.add(o);
    }

    /**
     * Add swing component to this menu
     *
     * @param o
     */
    public void addMenuElement(Component o) {
        elements.add(o);
    }

    /**
     * Return current displayed elements
     *
     * @return
     */
    public ArrayList<Object> getMenuElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Remove elements from this panel
     */
    public void clearMenuElementsList() {
        elements.clear();
    }

    /**
     * Set description of this panel
     *
     * @param description
     */
    public void setMenuDescription(String description) {
        this.description = description;
    }

    /**
     * Return current description of this panel
     *
     * @return
     */
    public String getMenuDescription() {
        return description;
    }

    /**
     * Return title of this panel
     *
     * @return
     */
    public String getMenuTitle() {
        return title;
    }

    /**
     * Set title of this panel
     *
     * @param title
     */
    public void setMenuTitle(String title) {
        this.title = title;
    }

    /**
     * Repaint this panel
     */
    public void refresh() {
        revalidate();
        repaint();
    }


    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public Component add(Component comp) {
        return super.add(comp);
    }

    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public void add(PopupMenu arg0) {
        super.add(arg0);
    }

    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public Component add(String name, Component comp) {
        return super.add(name, comp);
    }

    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public Component add(Component comp, int index) {
        return super.add(comp, index);
    }

    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    /**
     * Use instead addMenuElement()
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, constraints, index);
    }

}
