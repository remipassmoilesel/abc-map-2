package org.abcmap.gui.components.map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

/**
 * Move map when user drag it on component and change scale when user use mouse wheel
 */
public class CachedMapPaneMouseController extends MouseAdapter {

    private static final double ZOOM_ORIGINAL_INCREMENT = 20;
    private final CachedMapPane pane;
    private Point lastPosition;

    public CachedMapPaneMouseController(CachedMapPane pane) {
        this.pane = pane;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

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

        // adapt world position
        Point2D p = pane.getWorldPosition();

        pane.setWorldPosition(new Point2D.Double(p.getX() + mx, p.getY() - my));

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
        int psx = pane.getPartialSidePx();
        double psw = pane.getPartialSideWu();
        return val * psw / psx;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        lastPosition = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        super.mouseWheelMoved(e);

        double zoomUnit = scaleValue(ZOOM_ORIGINAL_INCREMENT);

        if (e.getWheelRotation() < 0) {
            zoomUnit = -zoomUnit;
        }

        pane.setPartialSideWu(pane.getPartialSideWu() + zoomUnit);
        pane.refreshMap();
    }
}
