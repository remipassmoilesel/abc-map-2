package org.abcmap.gui.components.color;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.GuiIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Open a color pickup dialog.
 * To listent it use getListenerHandler().add(WaitingForColor object)
 *
 * @author remipassmoilesel
 */
public class ColorDialogButton extends JButton implements HasListenerHandler<ColorEventListener> {

    private Color activeColor;
    private ListenerHandler<ColorEventListener> listenerHandler;

    public ColorDialogButton() {
        super(GuiIcons.CUSTOM_COLOR_BUTTON);
        listenerHandler = new ListenerHandler<ColorEventListener>();

        setCursor(GuiCursor.HAND_CURSOR);

        addActionListener(new CustomAL());
    }

    private class CustomAL implements ActionListener, Runnable {

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(this);
        }

        @Override
        public void run() {

            Window frameParent = SwingUtilities.windowForComponent(ColorDialogButton.this);
            final Color color = JColorChooser.showDialog(frameParent, "", Color.white);

            activeColor = color;

            ThreadManager.runLater(new Runnable() {
                public void run() {
                    listenerHandler.fireEvent(new ColorEvent(color, ColorDialogButton.this));
                }
            });

        }
    }

    public Color getActiveColor() {
        return activeColor;
    }

    /**
     * Use instead getListenerHandler()
     */
    @Deprecated
    @Override
    public void addActionListener(ActionListener l) {
        super.addActionListener(l);
    }

    ;

    @Override
    public ListenerHandler<ColorEventListener> getListenerHandler() {
        return listenerHandler;
    }
}
