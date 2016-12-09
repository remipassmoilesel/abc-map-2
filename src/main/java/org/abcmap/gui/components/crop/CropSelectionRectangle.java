package org.abcmap.gui.components.crop;

import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.gui.shape.Handle;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Crop selection made by user
 */
public class CropSelectionRectangle {

    public static final Integer MIDDLE_HANDLE_INDEX = 0;
    public static final Integer ULC_HANDLE_INDEX = 1;
    public static final Integer BRC_HANDLE_INDEX = 2;
    private final DrawManager drawm;

    /**
     * Thickness of drawing
     */
    private final int thickness;

    /**
     * Handles used by user for move selection
     */
    protected ArrayList<Handle> handles;

    /**
     * Real bounds of selection. All around this selection must be cropped.
     */
    private Rectangle bounds;

    /**
     * Drawn bounds of selection. Java draw line rectangle. E.g.: thickness = 10 -> 5 before, 5 after
     * But we have to draw shape INSIDE bounds or more precision
     */
    private Rectangle drawingBounds;

    /**
     * Area where user click are significant
     */
    private Area interactionArea;

    private Color fgColor;
    private BasicStroke stroke;

    public CropSelectionRectangle() {
        super();

        drawm = MainManager.getDrawManager();

        this.thickness = 5;
        this.fgColor = Color.green;
        this.stroke = new BasicStroke(thickness);
        this.bounds = new Rectangle();

        this.handles.add(new Handle(Handle.FOR_MOVING));
        this.handles.add(new Handle(Handle.FOR_RESIZING));
        this.handles.add(new Handle(Handle.FOR_RESIZING));
    }

    public void setColor(Color color) {
        fgColor = color;
    }

    protected void refreshHandles() {

        if (handles == null) {
            return;
        }

        handles.get(MIDDLE_HANDLE_INDEX).setPosition((int) bounds.getCenterX(), (int) bounds.getCenterY());
        handles.get(ULC_HANDLE_INDEX).setPosition(new Point(bounds.x, bounds.y));
        handles.get(BRC_HANDLE_INDEX).setPosition(new Point(bounds.x + bounds.width, bounds.y + bounds.height));

        for (Handle h : handles) {
            h.refreshShape();
        }

    }

    public void refreshShape() {

        int margin = drawm.getInteractionAreaMargin() + thickness;
        int halfMargin = Math.round(margin / 2);

        // compute outside bounds of interaction area
        Rectangle2D extEll = new Rectangle2D.Double(bounds.x - halfMargin, bounds.y - halfMargin,
                bounds.width + margin, bounds.height + margin);

        // compute inside bounds of interaction area
        Rectangle2D intEll = new Rectangle2D.Double(bounds.x + halfMargin, bounds.y + halfMargin,
                bounds.width - margin, bounds.height - margin);

        // assemble them
        this.interactionArea = new Area(extEll);
        interactionArea.subtract(new Area(intEll));

        // create special bounds to draw
        // we have to draw a shape INSIDE real bounds
        int hth = getHalfThickness();
        drawingBounds = new Rectangle(bounds.x + hth, bounds.y + hth, bounds.width - thickness, bounds.height - thickness);

        refreshHandles();
    }

    /**
     * Draw selection on screen. Java normally draw thickness around line, but here line
     *
     * @param g
     * @param mode
     */
    public void draw(Graphics2D g, String mode) {

        g.setColor(fgColor);
        g.setStroke(stroke);
        g.draw(drawingBounds);

        for (Handle h : handles) {
            h.draw(g);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setDimensions(Dimension dimensions) {
        this.bounds.setSize(dimensions);
    }

    public void setPosition(Point position) {
        this.bounds.setLocation(position);
    }

    public ArrayList<Handle> getHandles() {
        return handles;
    }

    public Point getPosition() {
        return new Point(bounds.getLocation());
    }

    public Dimension getDimensions() {
        return new Dimension(bounds.getSize());
    }

    public int getHalfThickness() {
        return Math.round(thickness / 0.5f);
    }
}
