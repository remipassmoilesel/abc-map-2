package org.abcmap.gui.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;

import abcmap.managers.DrawManager;
import abcmap.managers.stub.MainManager;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.MainManager;

/**
 * Handle which show on map that interactions are possible.
 */
public class Handle {

    public static final String FOR_MOVING = "FOR_MOVING";
    public static final String FOR_RESIZING = "FOR_RESIZING";

    private static final Color COLOR_FOR_MOVING = new Color(51, 215, 241);
    private static final Color COLOR_FOR_RESIZING = new Color(227, 84, 16);
    private static final Color COLOR_FOR_SHADOW = Color.lightGray;

    private static final int WIDTH = 17;
    private static final int GAP = 2;
    private static final int IN_THICK = 6;
    private static final int OUT_THICK = 4;

    private Ellipse2D shpOut;
    private Ellipse2D shpIn;
    private Point position;
    private String type;
    private Color color;

    private Rectangle interactionArea;

    private boolean drawInteractionArea;
    private DrawManager drawm;

    public Handle(String type) {

        this.drawm = MainManager.getDrawManager();

        this.drawInteractionArea = false;

        // position of handle on panel
        this.position = new Point();

        if (FOR_MOVING.equals(type)) {
            this.type = FOR_MOVING;
            this.color = COLOR_FOR_MOVING;
        } else if (FOR_RESIZING.equals(type)) {
            this.type = FOR_RESIZING;
            this.color = COLOR_FOR_RESIZING;
        } else {
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
        g.setStroke(new BasicStroke(OUT_THICK));
        g.draw(shpOut);

        g.setColor(color);
        g.setStroke(new BasicStroke(IN_THICK));
        g.draw(shpIn);
    }

    /**
     * Refresh all shapes of handle
     */
    public void refreshShape() {

        int halfWidthOut = Math.round(WIDTH / 2);
        int widthIn = WIDTH - (GAP + OUT_THICK) * 2;

        int rx = position.x - halfWidthOut;
        int ry = position.y - halfWidthOut;
        int rw = WIDTH;
        int rh = WIDTH;
        shpOut = new Ellipse2D.Double(rx, ry, rw, rh);

        int r2x = position.x - widthIn / 2;
        int r2y = position.y - widthIn / 2;
        int r2w = widthIn;
        int r2h = widthIn;
        shpIn = new Ellipse2D.Double(r2x, r2y, r2w, r2h);

        int margin = drawm.getInteractionAreaMargin();
        int halfMargin = margin / 2;
        Rectangle b = shpOut.getBounds();

        interactionArea = new Rectangle(b.x - halfMargin, b.y - halfMargin, b.width + margin,
                b.height + margin);

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

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Handle == false)
            return false;

        Handle shp = (Handle) obj;

        Object[] toCompare1 = new Object[]{this.position, this.type};
        Object[] toCompare2 = new Object[]{shp.position, shp.type};

        return Arrays.deepEquals(toCompare1, toCompare2);

    }

}
