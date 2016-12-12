package org.abcmap.gui.components.color;

import org.abcmap.gui.GuiCursor;

import javax.swing.*;
import java.awt.*;

public class ColorButton extends JButton {

    private Color color;
    private int borderThickness;
    private Color borderColor;

    public ColorButton(Color color) {
        super(" ");

        this.color = color;
        this.borderThickness = 1;
        this.borderColor = new Color(150, 150, 150);

        setCursor(GuiCursor.HAND_CURSOR);

        updateToolTipText();

    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle b = getBounds();

        Color bgColor = color != null ? color : Color.white;
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, b.width, b.height);

        if (color == null) {
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(borderThickness + 1));
            g2d.drawLine(0, 0, b.width, b.height);
        }

        g2d.setStroke(new BasicStroke(borderThickness));
        g2d.setColor(borderColor);
        g2d.drawRect(borderThickness / 2, borderThickness / 2, b.width - borderThickness,
                b.height - borderThickness);

    }

    private void updateToolTipText() {
        setToolTipText("Couleur: " + getStringRGB() + " (RGB)");
    }

    public void setColor(Color color) {
        this.color = color;
        updateToolTipText();
    }

    public Color getColor() {
        return color;
    }

    public String getStringRGB() {
        if (color == null) {
            return "Couleur nulle";
        }
        return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
    }

    @Override
    public void setEnabled(boolean b) {
        if (b) {
            setCursor(GuiCursor.HAND_CURSOR);
        } else {
            setCursor(GuiCursor.NORMAL_CURSOR);
        }
        super.setEnabled(b);
    }

}