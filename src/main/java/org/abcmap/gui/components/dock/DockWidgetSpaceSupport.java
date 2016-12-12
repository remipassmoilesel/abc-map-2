package org.abcmap.gui.components.dock;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

/**
 * Extended panel of a dock, maskable. Allow dock components to show larger components as menu, ...
 * <p>
 * Keep history of shown components, to allow user to come back to previous display.
 */
public class DockWidgetSpaceSupport extends JPanel {

    /**
     * History of displayed components
     */
    private ArrayList<Component> componentHistory;

    /**
     * Current index in history
     */
    private int index;

    private JPanel header;
    private DockOrientation orientation;
    private ArrayList<DockNavButton> buttons;

    public DockWidgetSpaceSupport(DockOrientation orientation) {

        this.orientation = orientation;

        this.componentHistory = new ArrayList<Component>();
        this.index = -1;

        setLayout(new MigLayout("insets 0, gap 0"));
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // header with navigation buttons
        String migAlign = DockOrientation.WEST.equals(orientation) ? "right" : "left";
        header = new JPanel();
        header.setLayout(new MigLayout("align " + migAlign + ", gapx 20"));

        String[] buttonsWEST = new String[]{DockNavButton.PREVIOUS,
                DockNavButton.NEXT, DockNavButton.EXPAND_HELP,
                DockNavButton.CLOSE,};
        String[] buttonsEST = new String[]{DockNavButton.CLOSE,
                DockNavButton.EXPAND_HELP, DockNavButton.PREVIOUS,
                DockNavButton.NEXT};
        String[] btts;
        buttons = new ArrayList<>();

        // invert button according to orientation
        btts = DockOrientation.EST.equals(orientation) ? buttonsEST : buttonsWEST;

        for (String s : btts) {
            DockNavButton b = new DockNavButton(s);
            header.add(b);
            buttons.add(b);
        }

        add(header, "span, grow, push, wrap 5px");

        Border lineBorder = BorderFactory.createLineBorder(new Color(210, 210, 210));
        header.setBorder(lineBorder);
        setBorder(lineBorder);

    }

    /**
     * Display component and add it to history
     *
     * @param comp
     */
    public void displayNew(Component comp) {

        // break history: remove all components after index
        while (componentHistory.size() - 1 > index) {
            componentHistory.remove(componentHistory.size() - 1);
        }

        // add comp to history
        if (componentHistory.size() == 0 || componentHistory.get(componentHistory.size() - 1).equals(comp) == false) {
            componentHistory.add(comp);
            index++;
        }

        addWidgetPanel(comp);
    }

    public void refresh() {
        this.revalidate();
        this.repaint();
    }

    /**
     * Display next component in history
     */
    public void displayNext() {
        index++;
        Component c = null;
        try {
            c = componentHistory.get(index);
        } catch (IndexOutOfBoundsException e) {
            index = componentHistory.size() - 1;
        }

        if (c != null) {
            addWidgetPanel(c);
            refresh();
        }

    }

    /**
     * Display previous component in history
     */
    public void displayPrevious() {

        index--;
        Component c = null;
        try {
            c = componentHistory.get(index);
        } catch (IndexOutOfBoundsException e) {
            index = 0;
        }

        if (c != null) {
            addWidgetPanel(c);
            refresh();
        }
    }

    private void checkButtonsValidity() {
        for (DockNavButton b : buttons) {
            b.checkValidity();
        }
    }

    /**
     * Add a component in center of menu, in a scroll pane
     *
     * @param comp
     */
    private void addWidgetPanel(Component comp) {

        // remove all but navigation bar
        for (Component c : getComponents()) {
            if (c.equals(header) == false) {
                remove(c);
            }
        }

        JScrollPane sp = new JScrollPane(comp);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.getVerticalScrollBar().setUnitIncrement(Dock.VERTICAL_SCROLLBAR_UNIT_INCREMENT);
        sp.setBorder(null);

        add(sp, "width 100%, height max, wrap");

        checkButtonsValidity();

    }

    public int getIndex() {
        return index;
    }

    public int getHistorySize() {
        return componentHistory.size();
    }

    @Deprecated
    @Override
    public void remove(Component comp) {
        super.remove(comp);
    }

    @Deprecated
    @Override
    public void remove(int index) {
        super.remove(index);
    }

    @Deprecated
    @Override
    public Component add(Component comp) {
        return super.add(comp);
    }

    @Deprecated
    @Override
    public Component add(String name, Component comp) {
        return super.add(name, comp);
    }

    @Deprecated
    @Override
    public Component add(Component comp, int index) {
        return super.add(comp, index);
    }

    @Deprecated
    @Override
    public void add(Component comp, Object constraints) {
        super.add(comp, constraints);
    }

    @Deprecated
    @Override
    public void add(Component comp, Object constraints, int index) {
        super.add(comp, constraints, index);
    }

}
