package org.abcmap.gui.shapes;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

/**
 * Handle which show on map that interactions are possible.
 */
public class Handle {

    /**
     * Handle used to move objects
     */
    public static final String FOR_MOVING = "FOR_MOVING";

    /**
     * Handle used to resize objects
     */
    public static final String FOR_RESIZING = "FOR_RESIZING";

    private static final Color COLOR_FOR_MOVING = new Color(51, 215, 241);
    private static final Color COLOR_FOR_RESIZING = new Color(227, 84, 16);
    private static final Color COLOR_FOR_SHADOW = Color.lightGray;

    private int totalWidth;
    private int gapBetweenShapes;
    private int inThick;
    private int outThick;
    private int interactionAreaMarginPx;

    private Ellipse2D shpOut;
    private Ellipse2D shpIn;
    private Point position;
    private String type;
    private Color color;

    /**
     * Area where interactions are
     */
    private Rectangle interactionArea;

    private boolean drawInteractionArea;

    public Handle(String type) {

        this.interactionAreaMarginPx = 5;
        this.totalWidth = 17;
        this.gapBetweenShapes = 2;
        this.inThick = 6;
        this.outThick = 4;

        this.drawInteractionArea = false;

        // position of handle on panel
        this.position = new Point();

        // this handle will be used to move
        if (FOR_MOVING.equals(type)) {
            this.type = FOR_MOVING;
            this.color = COLOR_FOR_MOVING;
        }

        // this handle will be used to resize
        else if (FOR_RESIZING.equals(type)) {
            this.type = FOR_RESIZING;
            this.color = COLOR_FOR_RESIZING;
        }

        // invalid arg
        else {
            throw new IllegalArgumentException("Invalid type: " + type);
        }

        refreshShape();

    }

    public void draw(Graphics2D g) {

        if (drawInteractionArea) {
            g.setPaint(Color.red);
            g.fill(interactionArea);
        }

        // white border for dark backgrounds
        g.setColor(COLOR_FOR_SHADOW);
        g.setStroke(new BasicStroke(outThick));
        g.draw(shpOut);

        g.setColor(color);
        g.setStroke(new BasicStroke(inThick));
        g.draw(shpIn);
    }

    /**
     * Refresh all shapes of handle
     */
    public void refreshShape() {

        int halfWidthOut = Math.round(totalWidth / 2);
        int widthIn = totalWidth - (gapBetweenShapes + outThick) * 2;

        int rx = position.x - halfWidthOut;
        int ry = position.y - halfWidthOut;
        int rw = totalWidth;
        int rh = totalWidth;
        shpOut = new Ellipse2D.Double(rx, ry, rw, rh);

        int r2x = position.x - widthIn / 2;
        int r2y = position.y - widthIn / 2;
        int r2w = widthIn;
        int r2h = widthIn;
        shpIn = new Ellipse2D.Double(r2x, r2y, r2w, r2h);

        int halfMargin = interactionAreaMarginPx / 2;
        Rectangle b = shpOut.getBounds();

        interactionArea = new Rectangle(b.x - halfMargin, b.y - halfMargin, b.width + interactionAreaMarginPx,
                b.height + interactionAreaMarginPx);

    }

    /**
     * Return true if point is on handle
     * @param p
     * @return
     */
    public boolean isPointOn(Point p) {
        return interactionArea.contains(p);
    }

    public void setPosition(Point p) {
        position = new Point(p);
    }

    public void setPosition(int x, int y) {
        position = new Point(x, y);
    }

    public Point getPosition() {
        return position;
    }

    public Shape getInteractionArea() {
        return interactionArea;
    }

    public String getType() {
        return type;
    }

    public void setInteractionAreaMarginPx(int interactionAreaMarginPx) {
        this.interactionAreaMarginPx = interactionAreaMarginPx;
    }

    public int getInteractionAreaMarginPx() {
        return interactionAreaMarginPx;
    }

    /**
     * Only use position and type
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Handle handle = (Handle) o;
        return Objects.equals(position, handle.position) &&
                Objects.equals(type, handle.type);
    }

    /**
     * Only use position and type
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(position, type);
    }
}
