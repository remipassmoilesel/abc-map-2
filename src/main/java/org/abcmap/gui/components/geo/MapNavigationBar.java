package org.abcmap.gui.components.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Navigation bar on bottom right corner of map, with zoom and reset buttons
 * <p>
 * This bar must be added on a panel without layout (absolute positioning),
 * Position of bar should be updated by calling refreshBoundsFrom()
 * <p>
 */
public class MapNavigationBar extends JPanel {

    private static final String ZOOM_IN = "ZOOM_IN";
    private static final String ZOOM_OUT = "ZOOM_OUT";
    private static final String CENTER = "CENTER";

    /**
     * Panel to control
     */
    private CachedMapPane pane;

    public MapNavigationBar(CachedMapPane pane) {
        super(new MigLayout("insets 2, gap 2px"));

        // keep panel reference
        this.pane = pane;

        // transparent panel
        this.setOpaque(false);

        // add actions
        ZoomActionListener zoomAl = new ZoomActionListener();

        JButton zoomin = new JButton(GuiIcons.MAP_ZOOMIN);
        zoomin.setActionCommand(ZOOM_IN);
        zoomin.addActionListener(zoomAl);
        add(zoomin, "wrap");

        JButton zoomout = new JButton(GuiIcons.MAP_ZOOMOUT);
        zoomout.setActionCommand(ZOOM_OUT);
        zoomout.addActionListener(zoomAl);
        add(zoomout, "wrap");

        JButton center = new JButton(GuiIcons.MAP_MOVECENTER);
        center.setActionCommand(CENTER);
        center.addActionListener(zoomAl);
        add(center, "right");

        zoomin.setCursor(GuiCursor.HAND_CURSOR);
        zoomout.setCursor(GuiCursor.HAND_CURSOR);
        center.setCursor(GuiCursor.HAND_CURSOR);
    }

    /**
     * Perform zoom
     */
    private class ZoomActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String ac = e.getActionCommand();

            // zoom in
            if (ZOOM_IN.equals(ac)) {
                pane.zoomIn();
            }

            // zoom out
            else if (ZOOM_OUT.equals(ac)) {
                pane.zoomOut();
            }

            // reset display
            else if (CENTER.equals(ac)) {
                pane.resetDisplay();
            }

            // error
            else {
                throw new IllegalStateException("Unknown mode:  " + ac);
            }

            pane.refreshMap();
        }
    }


}
