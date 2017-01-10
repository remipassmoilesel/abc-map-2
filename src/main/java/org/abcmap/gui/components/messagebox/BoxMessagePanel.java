package org.abcmap.gui.components.messagebox;

import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.transition.FadeTransition;
import org.abcmap.gui.transition.HasTransition;

import javax.swing.*;
import java.awt.*;

/**
 * Message box which an appear on screen to inform user and disappear after a little time
 */
public class BoxMessagePanel extends HtmlLabel implements HasTransition {

    private final FadeTransition fadeTransition;

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

        this.fadeTransition = new FadeTransition(this);

    }

    @Override
    protected void paintComponent(Graphics g) {

        // set transparency before paint
        fadeTransition.applyTransparency((Graphics2D) g);

        super.paintComponent(g);
    }

    public void startTransition(String t, Runnable whenFinished) {
        fadeTransition.start(t, whenFinished);
    }

    public void setTransparency(float transparency) {
        fadeTransition.setTransparency(transparency);
    }
}
