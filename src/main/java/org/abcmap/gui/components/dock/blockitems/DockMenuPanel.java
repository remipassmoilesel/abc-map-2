package org.abcmap.gui.components.dock.blockitems;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DockMenuPanel extends JPanel {

    protected ArrayList<Object> elements;

    protected String title;

    protected String description;

    public DockMenuPanel() {
        super(new MigLayout("fillx, insets 5 5 5 5"));

        this.title = "No title";
        this.description = null;
        this.elements = new ArrayList<Object>();

        reconstruct();
    }

    public void reconstruct() {

        removeAll();

        JPanel metadatas = new JPanel(new MigLayout("insets 5"));


        GuiUtils.addLabel(title, metadatas, "width 95%!, wrap", GuiStyle.DOCK_MENU_TITLE_1);

        if (description != null && description.isEmpty() == false) {

            JEditorPane desc = new JEditorPane("text/html", "<html>"
                    + description + "</html>");

            desc.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                    Boolean.TRUE);
            desc.setOpaque(false);
            desc.setEditable(false);
            GuiStyle.applyStyleTo(GuiStyle.DOCK_MENU_DESCRIPTION, desc);

            metadatas.add(desc, "width 95%!");
        }

        add(metadatas, "width " + Dock.MENU_ITEM_WIDTH + "!, wrap");

        for (Object o : elements) {

            if (o instanceof Component) {
                addItem((Component) o);
            } else if (o instanceof InteractionElement) {
                addItem(((InteractionElement) o).getBlockGUI());
            }

        }

        refresh();
    }


    private void addItem(Component c) {
        super.add(c, "width " + Dock.MENU_ITEM_WIDTH + "!, wrap");
    }

    public void addMenuElement(InteractionElement o) {
        elements.add(o);
    }

    public void addMenuElement(Component o) {
        elements.add(o);
    }

    public ArrayList<Object> getMenuElements() {
        return new ArrayList<>(elements);
    }

    public void clearMenuElementsList() {
        elements.clear();
    }

    public void setMenuDescription(String description) {
        this.description = description;
    }

    public String getMenuDescription() {
        return description;
    }

    public String getMenuTitle() {
        return title;
    }

    public void setMenuTitle(String title) {
        this.title = title;
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public Component add(Component comp) {
        return super.add(comp);
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public void add(PopupMenu arg0) {
        super.add(arg0);
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public Component add(String name, Component comp) {
        return super.add(name, comp);
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public Component add(Component comp, int index) {
        return super.add(comp, index);
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    /**
     * Use instead addMenuComponent()
     */
    @Deprecated
    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, constraints, index);
    }

    public void refresh() {
        revalidate();
        repaint();
    }

}
