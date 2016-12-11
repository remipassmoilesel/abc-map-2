package org.abcmap.gui.components.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.ie.display.zoom.ResetZoom;
import org.abcmap.gui.ie.display.zoom.ZoomIn;
import org.abcmap.gui.ie.display.zoom.ZoomOut;

import javax.swing.*;
import java.awt.*;

/**
 * Navigation bar on bottom right corner of map
 */
public class MapNavigationBar extends JPanel {

    private JPanel dpanel;
    private Rectangle lastParentBounds;

    public MapNavigationBar() {
        super(new MigLayout("insets 2, gap 2px"));

        this.setOpaque(false);

        ZoomIn zi = new ZoomIn();
        JButton zoomin = new JButton(zi.getMenuIcon());
        zoomin.addActionListener(zi);
        add(zoomin, "wrap");

        ZoomOut zo = new ZoomOut();
        JButton zoomout = new JButton(zo.getMenuIcon());
        zoomout.addActionListener(zo);
        add(zoomout, "wrap");

        ResetZoom rz = new ResetZoom();
        JButton center = new JButton(rz.getMenuIcon());
        center.addActionListener(rz);
        add(center, "right");

        zoomin.setCursor(GuiCursor.HAND_CURSOR);
        zoomout.setCursor(GuiCursor.HAND_CURSOR);
        center.setCursor(GuiCursor.HAND_CURSOR);
    }

    public void setPanelToControl(JPanel dpanel) {
        this.dpanel = dpanel;
    }

    public void refreshBoundsFrom(Rectangle parentBounds) {

        // avoid uneeded calls
        if (Utils.safeEquals(parentBounds, lastParentBounds)) {
            return;
        } else {
            lastParentBounds = parentBounds;
        }

        Dimension navbarDims = getPreferredSize();

        int x = parentBounds.x + parentBounds.width - navbarDims.width;
        int y = parentBounds.y + parentBounds.height - navbarDims.height;
        int width = navbarDims.width;
        int height = navbarDims.height;
        setBounds(x, y, width, height);

        repaint();
    }

}
