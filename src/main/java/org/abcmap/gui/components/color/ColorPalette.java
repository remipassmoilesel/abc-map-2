package org.abcmap.gui.components.color;

import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.GuiCursor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Clickable JPanel where are painted several colors
 *
 * @author remipassmoilesel
 */
public class ColorPalette extends JPanel implements HasListenerHandler<ActionListener> {

    /**
     * Where colors are painted
     */
    private BufferedImage backgroundImage;

    /**
     * Last color chosen
     */
    private Color activeColor;

    private ListenerHandler<ActionListener> listenersHandler;

    /**
     * Size of panel side
     */
    private int size;

    public ColorPalette() {

        this.size = 200;

        setBorder(BorderFactory.createLineBorder(Color.gray, 2));

        listenersHandler = new ListenerHandler<>();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(GuiCursor.CROSS_CURSOR);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Point p = e.getPoint();
                setActiveColor(new Color(backgroundImage.getRGB(p.x, p.y)));
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        if (backgroundImage == null) {
            buildBackground();
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(backgroundImage, 0, 0, Math.round(size), Math.round(size), null);
    }

    public void buildBackground() {

        float h = 0f;
        float s = 1f;
        float b = 1f;

        backgroundImage = new BufferedImage(Math.round(size), Math.round(size), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = (Graphics2D) backgroundImage.getGraphics();

        // A: value in TSL space
        float stepA = 0.01f;

        // B: value in component space
        float stepB = stepA * size;

        for (int i = 0; i < size; i += stepB) {
            h += stepA;
            for (int j = 0; j < size; j += stepB) {
                b -= stepA;
                if (b < 0)
                    b = 0f;
                g2d.setColor(Color.getHSBColor(h, s, b));
                g2d.fillRect(i, j, Math.round(stepB), Math.round(stepB));
            }
            b = 1f;
        }

    }

    /**
     * Change component size
     *
     * @param size
     */
    public void setSideSize(int size) {
        this.size = size;
        buildBackground();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Math.round(size), Math.round(size));
    }

    /**
     * Change active color and fire an event
     *
     * @param color
     */
    public void setActiveColor(Color color) {
        this.activeColor = color;
        listenersHandler.fireEvent(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }

    /**
     * Return active color
     *
     * @return
     */
    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * Add a listener to color picker. Listener will be notified when color change
     *
     * @param al
     */
    public void addActionListener(ActionListener al) {
        listenersHandler.add(al);
    }


    @Override
    public ListenerHandler<ActionListener> getListenerHandler() {
        return listenersHandler;
    }

}
