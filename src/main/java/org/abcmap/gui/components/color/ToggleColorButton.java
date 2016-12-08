package org.abcmap.gui.components.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import abcmap.gui.GuiCursor;
import org.abcmap.gui.GuiCursor;

/**
 * Selectable color button
 *
 * @author remipassmoilesel
 */
public class ToggleColorButton extends JToggleButton {

    private Color color;

    private final static int SELECTION_BORDER = 2;
    private final static BasicStroke SELECTION_STROKE = new BasicStroke(SELECTION_BORDER);

    /**
     * Color of border when button is selected
     */
    private static final Color SELECTED_COLOR = Color.red;

    /**
     * Color of border when button is unselected
     */
    private static final Color UNSELECTED_COLOR = Color.gray;

    public ToggleColorButton() {
        this(null);
    }

    public ToggleColorButton(Color color) {
        super(" ");
        this.color = color;
        setCursor(GuiCursor.HAND_CURSOR);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        Dimension dimensions = getSize();

        // draw background
        Color c = color != null ? color : Color.white;
        g2d.setColor(c);
        g2d.fillRect(0, 0, dimensions.width, dimensions.height);

        // draw foreground

        // draw a slash if color is null
        if (color == null) {
            g2d.setColor(Color.darkGray);
            g2d.setStroke(new BasicStroke(SELECTION_BORDER));
            g2d.drawLine(0, 0, dimensions.width, dimensions.height);
        }

        g2d.setColor(Color.white);
        g2d.setStroke(SELECTION_STROKE);
        g2d.drawRect(SELECTION_BORDER, SELECTION_BORDER, dimensions.width - SELECTION_BORDER * 2,
                dimensions.height - SELECTION_BORDER * 2);

        Color cb = isSelected() ? SELECTED_COLOR : UNSELECTED_COLOR;
        g2d.setColor(cb);
        g2d.setStroke(SELECTION_STROKE);
        g2d.drawRect(SELECTION_BORDER / 2, SELECTION_BORDER / 2, dimensions.width - SELECTION_BORDER,
                dimensions.height - SELECTION_BORDER);

    }

    /**
     * Set button color
     *
     * @param color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get button color
     *
     * @return
     */
    public Color getColor() {
        return color;
    }

}