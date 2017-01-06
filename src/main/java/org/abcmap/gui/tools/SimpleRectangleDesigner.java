package org.abcmap.gui.tools;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility used to draw a rectangle with mouse
 *
 * @author remipassmoilesel
 */
public class SimpleRectangleDesigner extends MouseAdapter {

    /**
     * Rectangle being drawn
     */
    protected Rectangle rectangle;

    /**
     * Origin point of rectangle
     */
    private Point rectangleOrigin;

    /**
     * If true, drawing in progress
     */
    private boolean working;

    /**
     * Delete rectangle when mouse released
     */
    private boolean deleteRectangleOnMouseReleased;

    /**
     * Color of shape
     */
    private Color rectangleColor;

    /**
     * Stroke of shape
     */
    private Stroke rectangleStroke;

    /**
     * If set to true, proportions will be respected if user hold control key
     * <p>
     * (and draw a suare instead of a rectangle)
     */
    private boolean keepProportionsOnControlDown;

    public SimpleRectangleDesigner() {

        keepProportionsOnControlDown = false;

        rectangleColor = Color.black;
        rectangleStroke = new BasicStroke(2);

        deleteRectangleOnMouseReleased = false;

        this.rectangle = null;
        this.rectangleOrigin = null;

        this.working = false;
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {

        Point currentPos = arg0.getPoint();

        // first click, create rectangle
        if (isWorking() == false) {

            rectangle = new Rectangle();
            rectangle.x = currentPos.x;
            rectangle.y = currentPos.y;

            rectangleOrigin = new Point(currentPos);

            setWorking(true);
        }

        // click after the first
        if (working == true) {

            Point originCopy = new Point(rectangleOrigin);

            int w = currentPos.x - originCopy.x;
            int h = currentPos.y - originCopy.y;
            Dimension dim = new Dimension(w, h);

            // always get positive values
            if (dim.width < 0) {
                int x = originCopy.x + dim.width;
                originCopy.setLocation(x, originCopy.y);
                dim.width = -dim.width;
            }

            if (dim.height < 0) {
                int y = originCopy.y + dim.height;
                originCopy.setLocation(originCopy.x, y);
                dim.height = -dim.height;
            }

            // control is down, keep proportions
            if (keepProportionsOnControlDown == true && arg0.isControlDown() == true) {
                dim.width = dim.height;
            }

            // update shape
            rectangle.x = originCopy.x;
            rectangle.y = originCopy.y;
            rectangle.width = dim.width;
            rectangle.height = dim.height;

        }

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {

        if (deleteRectangleOnMouseReleased == true) {
            resetRectangle();
        }

        setWorking(false);

    }

    /**
     * Draw rectangle if necessary
     *
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        if (rectangle != null) {
            g2d.setColor(rectangleColor);
            g2d.setStroke(rectangleStroke);
            g2d.draw(rectangle);
        }
    }

    /**
     * Reset rectangle and internal flags
     */
    protected void resetRectangle() {
        rectangle = null;
        rectangleOrigin = null;
        setWorking(false);
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    /**
     * If true, a rectangle is being drawn
     *
     * @return
     */
    public boolean isWorking() {
        return working;
    }

    /**
     * Set color of rectangle line
     *
     * @param color
     */
    public void setRectangleColor(Color color) {
        this.rectangleColor = color;
    }

    /**
     * If set to true, rectangle will be deleted when mouse released
     *
     * @param val
     */
    public void setDeleteRectangleOnMouseReleased(boolean val) {
        this.deleteRectangleOnMouseReleased = val;
    }

    /**
     * Set rectangle line style
     *
     * @param stroke
     */
    public void setRectangleStroke(Stroke stroke) {
        this.rectangleStroke = stroke;
    }

    /**
     * Return a copy of current rectangle
     * <p>
     * Can return null
     *
     * @return
     */
    public Rectangle getRectangle() {
        return rectangle != null ? new Rectangle(rectangle) : null;
    }

}
