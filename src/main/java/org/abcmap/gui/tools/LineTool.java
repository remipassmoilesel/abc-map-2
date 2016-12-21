package org.abcmap.gui.tools;

import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.project.layers.FeatureLayer;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Draw line on map with current style
 */
public class LineTool extends MapTool {

    private LineBuilder builder;

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        // TODO: block zoom when drawing ?

        FeatureLayer layer = getActiveFeatureLayerIfLeftClickOrShowMessage(e);
        if (layer == null) {
            return;
        }

        // one click only
        if (e.getClickCount() < 2) {

            // first point of line
            if (builder == null) {
                builder = drawm.getLineBuilder();
                builder.newLine(screenToWorldCoordinate(e.getPoint()));
            }

            // other points of line
            else {
                builder.addPoint(screenToWorldCoordinate(e.getPoint()));
            }

        }

        // double click or more
        else if (e.getClickCount() > 1) {

            if (builder != null) {
                builder.terminateLine(screenToWorldCoordinate(e.getPoint()));
                builder = null;

                deleteActiveLayerCache();
            }

        }

        repaintMainMap();

    }

    @Override
    public void drawOnMainMap(Graphics2D g2d) {
        super.drawOnMainMap(g2d);

        // draw current shape if necessary
        if (builder != null) {
            builder.drawCurrentShape(g2d, getMainMapWorldToScreen());
        }
    }


}
