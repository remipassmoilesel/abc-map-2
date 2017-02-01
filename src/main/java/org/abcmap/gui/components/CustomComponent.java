package org.abcmap.gui.components;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiColors;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.utils.FocusPainter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Simple focusable component. Should be used as a JPanel.
 * <p>
 * JButton is extended here instead of JPanel to facilitate keyboard use/focus.
 */
public class CustomComponent extends JButton {

    private boolean focused;
    private FocusPainter painter;

    public CustomComponent() {
        super();

        painter = new FocusPainter(GuiColors.PANEL_BACKGROUND);

        setCursor(GuiCursor.HAND_CURSOR);

        // style
        setOpaque(true);
        setBorder(null);
        setLayout(new MigLayout("insets 5"));

        // activate keyboard
        setFocusable(true);

        // listeners
        addMouseListener(new CustomMouseAdapter());

    }

    @Override
    protected void paintComponent(Graphics g) {

        // paint background relative to focus status
        painter.draw(g, this, isFocused());

    }

    /**
     * eturn true if element is presently focused
     *
     * @return
     */
    private boolean isFocused() {
        return focused;
    }

    /**
     * Set element focused. Call repaint() after
     *
     * @param hovered
     */
    private void setFocused(boolean hovered) {
        this.focused = hovered;
    }

    /**
     * Change focus state relative to user interactions
     */
    private class CustomMouseAdapter extends MouseAdapter implements FocusListener {

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            // disable focus on click to prevent weird display
            setFocused(false);
            repaint();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setFocused(true);
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setFocused(false);
            repaint();
        }

        @Override
        public void focusGained(FocusEvent e) {
            setFocused(true);
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            setFocused(false);
            repaint();
        }

    }

    /**
     * This component should NOT be used as a button
     */
    @Deprecated
    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
    }

    /**
     * This component should NOT be used as a button
     */
    @Deprecated
    @Override
    public void setText(String text) {
        super.setText(text);
    }

}
