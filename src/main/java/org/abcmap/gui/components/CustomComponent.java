package org.abcmap.gui.components;

import abcmap.gui.GuiColors;
import abcmap.utils.gui.FocusPainter;
import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiCursor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Simple focusable component. Must be used as a JPanel.
 * JButton is used here to facilitate keyboard use/focus.
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

        // repeindre selon le focus de l'element
        painter.draw(g, this, isFocused());

    }

    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean hovered) {
        this.focused = hovered;
    }

    private class CustomMouseAdapter extends MouseAdapter implements FocusListener {

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
     * This component must not be used as a button
     */
    @Deprecated
    @Override
    public void setIcon(Icon defaultIcon) {
        super.setIcon(defaultIcon);
    }

    /**
     * This component must not be used as a button
     */
    @Deprecated
    @Override
    public void setText(String text) {
        super.setText(text);
    }

}
