package org.abcmap.gui.utils;

import org.abcmap.gui.GuiColors;

import java.awt.*;

/**
 * Paint element in terms of focus
 */
public class FocusPainter {

    private Color nonFocusedColor;
    private Color focusedColor;
    private int focusPaintMargins;

    public FocusPainter() {
        this.focusPaintMargins = 3;
    }

    public FocusPainter(Color nonFocus) {
        this.nonFocusedColor = nonFocus;
        this.focusedColor = GuiColors.FOCUS_COLOR_BACKGROUND;
    }

    public FocusPainter(Color nonFocus, Color focus) {
        this.nonFocusedColor = nonFocus;
        this.focusedColor = focus;
    }

    public void draw(Graphics g, Component comp, boolean focused) {

        Graphics2D g2d = (Graphics2D) g;

        Rectangle r = new Rectangle(comp.getSize());

        r.x -= focusPaintMargins;
        r.y -= focusPaintMargins;
        r.width += focusPaintMargins * 2;
        r.height += focusPaintMargins * 2;

        g2d.clearRect(r.x, r.y, r.width, r.height);

        if (focused) {

            Graphics2D g2dT = (Graphics2D) g.create();

            float alpha = 0.2f;
            int type = AlphaComposite.SRC_OVER;
            AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
            g2dT.setComposite(composite);

            Color co = GuiColors.FOCUS_COLOR_BACKGROUND;
            g2dT.setColor(co);
            g2dT.fillRect(r.x, r.y, r.width, r.height);

            g2d.setColor(focusedColor);
            g2d.setStroke(GuiColors.FOCUS_STROKE);
            int t = GuiColors.FOCUS_STROKE_THICKNESS;
            g2d.drawRect(r.x, r.y, r.width - t, r.height - t);

        } else {
            g2d.setColor(nonFocusedColor);
            g2d.fillRect(r.x, r.y, r.width, r.height);
        }

    }

    public void setNonFocusedColor(Color c) {
        this.nonFocusedColor = c;
    }

    public void setFocusedColor(Color c) {
        this.focusedColor = c;
    }

}
