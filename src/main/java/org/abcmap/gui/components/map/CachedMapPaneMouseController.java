package org.abcmap.gui.components.map;

import org.abcmap.core.managers.KeyboardManager;
import org.abcmap.core.managers.Main;
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

    private final KeyboardManager keym;
    private final CachedMapPane pane;
    private Point lastPosition;
    private MouseControlType type;

    public CachedMapPaneMouseController(CachedMapPane pane, MouseControlType type) {
        this.keym = Main.getKeyboardManager();
        this.type = type;
        this.pane = pane;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (MouseControlType.DRAG_WITH_SPACE.equals(type) == false) {
            return;
        }

        // move if space bar down
        if (keym.isSpaceBarDown()) {
            moveMap(e);
            return;
        }

        // reset position at last move
        else if (lastPosition != null) {
            lastPosition = null;
        }

    }

    /**
     * Move map, only in SIMPLE mode
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);

        if (MouseControlType.SIMPLE.equals(type) == false) {
            return;
        }

        // check if left button enabled
        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        moveMap(e);

    }

    /**
     * Reset last position of drag, only in SIMPLE mode
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);

        if (MouseControlType.SIMPLE.equals(type) == false) {
            return;
        }

        // check if left button enabled
        if (SwingUtilities.isLeftMouseButton(e) == false) {
            return;
        }

        lastPosition = null;

    }

    /**
     * Move map relative to mouse position
     *
     * @param e
     */
    public void moveMap(MouseEvent e) {

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
     * Change zoom
     *
     * @param e
     */
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


}
