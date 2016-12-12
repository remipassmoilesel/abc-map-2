package org.abcmap.gui.components.messagebox;

import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Message box which an appear on screen to inform user and disappear after a little time
 */
public class BoxMessagePanel extends JPanel {

    private JLabel label;
    private Font font;
    private Color bgColor;
    private Color fgColor;
    private float transparency = 0.8f;

    public BoxMessagePanel() {
        super();

        this.setLayout(new BorderLayout());

        // main label
        label = new JLabel();
        label.setOpaque(false);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        font = new Font(Font.DIALOG, Font.BOLD, 18);
        setMessageFont(font);

        // default colors
        bgColor = Color.black;
        fgColor = Color.white;

        setOpaque(true);
        setBackground(bgColor);
        label.setForeground(fgColor);

        setPreferredSize(new Dimension(600, 60));

        add(label, BorderLayout.CENTER);

    }

    @Override
    protected void paintComponent(Graphics g) {
        if (transparency != 1f) {
            ((Graphics2D) g).setComposite(GuiUtils.createTransparencyComposite(transparency));
        }
        super.paintComponent(g);
    }

    public void setMessage(String text) {
        label.setText(text);
    }

    public void refresh() {

        label.revalidate();
        label.repaint();

        revalidate();
        repaint();

    }

    public void setMessageFont(Font font) {
        this.font = font;
        if (label != null) {
            label.setFont(font);
        }
    }

    @Override
    public void setBackground(Color bg) {
        bgColor = bg;
        super.setBackground(bg);
    }

    public void setTransparency(float transparency) {
        this.transparency = transparency;
    }

}
