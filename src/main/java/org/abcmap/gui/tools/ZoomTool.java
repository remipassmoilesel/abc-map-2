package org.abcmap.gui.tools;

import com.vividsolutions.jts.geom.Coordinate;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

/**
 * Zoom
 * <p>
 * click left +
 * click right -
 * + visual selection
 */
public class ZoomTool extends MapTool {

    private final SimpleRectangleDesigner rectangleAreaDesigner;

    /**
     * Minimal size in pixel of zoom selection. If selection is lower than this value, no zoom is performed
     */
    private double minSizePx;

    public ZoomTool() {
        super();
        rectangleAreaDesigner = new SimpleRectangleDesigner();
        minSizePx = 10;
    }

    /**
     * Zoom in or out if user perform a click
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);

        // check if project is initialized
        if (getProjectOrShowMessage() == null) {
            return;
        }

        // left click: zoom in
        if (SwingUtilities.isLeftMouseButton(e)) {
            getMainMapPane().zoomIn();
        }

        // right click: zoom out
        else {
            getMainMapPane().zoomOut();
        }

        refreshMainMap();

    }

    /**
     * User drag on map, draw a selection
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        // draw a zoom selection on right click drag
        if (SwingUtilities.isRightMouseButton(e)) {
            rectangleAreaDesigner.mouseDragged(e);
        }

        repaintMainMap();

    }

    /**
     * User release button, zoom if he was drawing and if area is valid
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        rectangleAreaDesigner.mouseReleased(e);

        // user end area drawing
        Rectangle rect = rectangleAreaDesigner.getRectangle();
        if (rect != null && rect.getWidth() > minSizePx && rect.getHeight() > minSizePx) {
            AffineTransform transform = getWorldToScreenTransform();

            // get transformed bottom left corner
            Coordinate blc = screenPointToWorldCoordinate(rect.x, rect.y + rect.height);

            // get transformed upper right corner
            Coordinate urc = screenPointToWorldCoordinate(rect.x + rect.width, rect.y);

            // create a new envelope and set it on main map
            ReferencedEnvelope env = new ReferencedEnvelope(blc.x, urc.x, blc.y, urc.y, projectm.getProject().getCrs());
            getMainMapPane().setWorldEnvelope(env);

            rectangleAreaDesigner.resetRectangle();

            // repaint map
            refreshMainMap();

        }

        // shape may be too tiny, reset all
        else {
            rectangleAreaDesigner.resetRectangle();

            // repaint map
            repaintMainMap();
        }

    }

    /**
     * Draw zoom selection on map if necessary
     *
     * @param g2d
     */
    @Override
    public void drawOnMainMap(Graphics2D g2d) {
        super.drawOnMainMap(g2d);
        rectangleAreaDesigner.draw(g2d);
    }



}
