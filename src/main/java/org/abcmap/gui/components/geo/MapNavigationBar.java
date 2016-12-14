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
 * Navigation bar on bottom right corner of map, with zoom and reset buttons
 * <p>
 * This bar must be added on a panel without layout (absolute positioning),
 * Position of bar should be updated by calling refreshBoundsFrom()
 */
public class MapNavigationBar extends JPanel {

    /**
     * Last parent size, used to avoid unnecessary updates of position
     */
    private Dimension lastParentSize;

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

    /**
     * Refresh position of navigation bar
     *
     * @param parentSize
     */
    public void refreshBoundsFrom(Dimension parentSize) {

        // avoid uneeded calls
        // TODO: sometimes comparison block update
        //if (Utils.safeEquals(parentSize, lastParentSize)) {
        //    return;
        //} else {
        //    lastParentSize = parentSize;
        //}

        Dimension navbarDims = getPreferredSize();

        int x = parentSize.width - navbarDims.width;
        int y = parentSize.height - navbarDims.height;
        int width = navbarDims.width;
        int height = navbarDims.height;
        setBounds(x, y, width, height);

        repaint();
    }

}
