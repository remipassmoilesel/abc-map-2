package org.abcmap.gui.components.map;

import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Move map when user drag it on component and change scale when user use mouse wheel
 */
public class CachedMapPaneMouseController extends MouseAdapter {

    private final CachedMapPane pane;
    private Point lastPosition;

    public CachedMapPaneMouseController(CachedMapPane pane) {
        this.pane = pane;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        // check if left button enabled
        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        // first move, keep position and return
        if (lastPosition == null) {
            lastPosition = e.getPoint();
            return;
        }

        // get mouse move
        Point m = e.getPoint();
        double mx = lastPosition.getX() - m.getX();
        double my = lastPosition.getY() - m.getY();

        // scale it
        mx = scaleValue(mx);
        my = scaleValue(my);

        // adapt world envelope
        ReferencedEnvelope translated = pane.getWorldEnvelope();
        translated.translate(mx, -my);
        pane.setWorldEnvelope(translated);

        pane.refreshMap();

        lastPosition = m;

    }

    /**
     * Scale a distance from panel unit to world unit
     *
     * @param val
     * @return
     */
    public double scaleValue(double val) {
        double scale = pane.getScale();
        return val * scale;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        // check if left button enabled
        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        lastPosition = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);

        if (e.getWheelRotation() < 0) {
            pane.zoomIn();
        } else {
            pane.zoomOut();
        }

        pane.refreshMap();

    }

}
