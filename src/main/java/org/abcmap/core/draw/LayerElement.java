package org.abcmap.core.draw;

import org.abcmap.gui.shapes.Handle;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

/**
 * Wrap elements of layers
 * <p>
 */
public class LayerElement {

    public static final String RENDER_FOR_PRINTING = "RENDER_FOR_PRINTING";

    public void draw(Graphics2D g, String mode) {

    }

    public LayerElement getSample(int maxSampleWidth, int maxSampleWidth1) {
        return new LayerElement();
    }

    public LayerElement duplicate() {
        return new LayerElement();
    }

    protected void drawLinkMark(Graphics2D g, boolean forceDraw) {

    }

    public void drawLinkMark(boolean val) {

    }

    protected void refreshHandles() {

    }

    protected void drawHandles(Graphics2D g) {

    }

    public Point getPosition() {
        return new Point();
    }

    public void setPosition(Point position) {

    }

    public void setPosition(int x, int y) {
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean val) {
    }

    public ArrayList<Handle> getHandles() {
        return new ArrayList<>();
    }

    public Rectangle getMaximumBounds() {
        return new Rectangle();
    }

    public void drawInteractionArea(boolean v) {

    }

}
