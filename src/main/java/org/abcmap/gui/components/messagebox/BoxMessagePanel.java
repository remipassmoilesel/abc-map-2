package org.abcmap.gui.components.messagebox;

import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Message box which an appear on screen to inform user and disappear after a little time
 */
public class BoxMessagePanel extends HtmlLabel {

    private float transparency = 0.9f;

    public BoxMessagePanel() {
        super();

        this.setLayout(new BorderLayout());

        setOpaque(true);
        setBorder(null);
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        setFont(new Font(Font.DIALOG, Font.BOLD, 16));

        // default colors
        setForeground(Color.white);
        setBackground(Color.black);

        setPreferredSize(new Dimension(600, 60));

    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D) g).setComposite(GuiUtils.createTransparencyComposite(transparency));
        super.paintComponent(g);
    }

    /**
     * Set transparency
     *
     * @param transparency
     */
    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

    /**
     * Get current transparency
     *
     * @return
     */
    public float getTransparency() {
        return transparency;
    }

    /**
     * Add value to current transparency
     *
     * @param increment
     */
    public void addTransparencyValue(float increment) {

        transparency += increment;

        // check interval
        if (transparency > 1) {
            transparency = 1;
        } else if (transparency < 0) {
            transparency = 0;
        }
    }
}
