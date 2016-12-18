package org.abcmap.gui.components.messagebox;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.color.ColorPicker;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Display a message on screen to inform user. Message disappear after a little time.
 * <p>
 * // TODO: handle multiple Threads: what happen if multiple simultaneous call ?
 */
public class MessageBoxManager {

    private static final CustomLogger logger = LogManager.getLogger(ColorPicker.class);

    private final int transpInterval = 50;
    private final float transpIncrement = 0.1f;

    /**
     * Parent frame of message box
     */
    private JFrame parentFrame;

    /**
     * Where is displayed message
     */
    private BoxMessagePanel messagePanel;

    /**
     * Popup hold message panel
     */
    private JPopupMenu popup;

    /**
     * If no time is specified, this one is used
     */
    private Integer defaultTime;

    public MessageBoxManager(JFrame parent) {

        this.parentFrame = parent;

        this.messagePanel = new BoxMessagePanel();

        // Hide box on click
        messagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                popup.setVisible(false);
            }
        });

        this.defaultTime = 3000;

    }

    /**
     * Show a message in box
     *
     * @param message
     */
    public void showMessage(String message) {
        showMessage(defaultTime, message);
    }

    public Integer getDefaultTime() {
        return defaultTime;
    }

    public void setDefaultTime(Integer defaultTime) {
        this.defaultTime = defaultTime;
    }

    /**
     * Show a message on EDT
     *
     * @param timeMilliSec
     * @param message
     */
    public void showMessage(Integer timeMilliSec, final String message) {
        SwingUtilities.invokeLater(() -> {

            // do not show if main window is not visible
            if (parentFrame == null) {
                return;
            }

            if (popup == null) {
                popup = new JPopupMenu();
                popup.setOpaque(false);

                JPanel support = new JPanel(new BorderLayout());
                support.add(messagePanel, BorderLayout.CENTER);

                popup.setBorder(null);
                popup.add(support);
                popup.pack();
            }

            // set message
            messagePanel.setText("<center>" + message + "</center>");
            messagePanel.revalidate();
            messagePanel.repaint();

            // compute dimensions
            Dimension df = parentFrame.getSize();
            Dimension dm = messagePanel.getPreferredSize();

            int x = (df.width - dm.width) / 2;
            int y = (int) (df.height - (df.height * 0.20f) - dm.height);

            try {
                messagePanel.setTransparency(0);
                popup.show(parentFrame.getContentPane(), x, y);

                // fade in label
                ThreadManager.runLater(() -> {

                    GuiUtils.throwIfOnEDT();

                    synchronized (this) {
                        while (messagePanel.getTransparency() < 1) {
                            try {
                                wait(transpInterval);
                            } catch (InterruptedException e) {
                                logger.error(e);
                            }
                            messagePanel.addTransparencyValue(transpIncrement);
                            messagePanel.revalidate();
                            messagePanel.repaint();
                        }
                    }
                });

                // hide label
                ThreadManager.runLater(() -> {
                    popup.setVisible(false);
                }, true, timeMilliSec + 1);

            } catch (Exception e) {
                logger.error(e);
            }

        });
    }

    /**
     * Set background color of panel to indicate if it is an error or just information
     *
     * @param background
     */
    public void setBackgroundColor(Color background) {
        messagePanel.setBackground(background);
        messagePanel.revalidate();
        messagePanel.repaint();
    }

}
