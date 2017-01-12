package org.abcmap.gui.components.search;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

class InteractivePopupDisplay extends JPopupMenu {

    private JPanel content;
    private JScrollPane scroll;

    private Component compParent;

    private int maxPopupHeight;
    private int minPopupHeight;
    private int preferredPopupWidth;

    InteractivePopupDisplay(Component parent) {

        super();
        this.setLayout(new BorderLayout());

        // default dimensions of popup
        this.preferredPopupWidth = 400;
        this.minPopupHeight = 450;
        this.maxPopupHeight = 600;

        setPreferredSize(new Dimension(preferredPopupWidth, minPopupHeight));

        // parent near which is displayed popup
        this.compParent = parent;

        // main panel of pop up
        this.content = new JPanel(new MigLayout("insets 5"));

        // all is in a scroll pane
        this.scroll = new JScrollPane(content);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getHorizontalScrollBar().setUnitIncrement(150);

        this.add(scroll, BorderLayout.CENTER);
    }

    /**
     * Show or hide pop up
     *
     * @param val
     */
    public void showPopup(boolean val) {

        // show popup
        if (val) {
            int x = 0;
            int y = compParent.getHeight();

            this.show(compParent, x, y);

        }

        // hide popup
        else {
            this.setVisible(false);
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Return content pane of pop up
     *
     * @return
     */
    public JPanel getContentPane() {
        return content;
    }

    /**
     * Propose a value as height. This value will be used if it is in maximum and minimum bounds.
     * <p>
     * If specified argument is null, height will be computed
     *
     * @param height
     */
    public void proposePopupHeight(Integer height) {

        // if height is null, compute height and width
        if (height == null) {
            height = computePreferredHeight();
        }

        if (height < minPopupHeight) {
            height = minPopupHeight;
        }

        if (height > maxPopupHeight) {
            height = maxPopupHeight;
        }

        Dimension dim = getPreferredSize();
        dim.height = height;
        setPreferredSize(dim);
    }

    /**
     * Compute preferred height of component relative to childs of content pane
     *
     * @return
     */
    public int computePreferredHeight() {
        int height = 0;
        for (Component c : content.getComponents()) {
            height += c.getPreferredSize().height;
        }

        return height;
    }

    /**
     * Adjust height to optimal value
     */
    public void adjustHeight() {
        proposePopupHeight(null);
    }

}
