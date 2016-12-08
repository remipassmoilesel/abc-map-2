package org.abcmap.gui.components.messagebox;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.color.ColorPicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Display a message on screen to inform user. Message disappear after a little time.
 */
public class MessageBoxManager {

    private static final CustomLogger logger = LogManager.getLogger(ColorPicker.class);

    private JFrame frame;
    private BoxMessagePanel messagePanel;
    private JPopupMenu popup;
    private Integer defaultTime;

    public MessageBoxManager() {
        this(null);
    }

    public MessageBoxManager(JFrame parent) {

        this.frame = parent;

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

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

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
            if (frame == null) {
                return;
            }

            if (popup == null) {
                popup = new JPopupMenu();
                popup.add(messagePanel);
                popup.pack();
            }

            // set message
            messagePanel.setMessage("<html><center>" + message + "</center></html>");
            messagePanel.refresh();

            // compute dimensions
            Dimension df = frame.getSize();
            Dimension dm = messagePanel.getPreferredSize();

            int x = (df.width - dm.width) / 2;

            int y = (int) (df.height - (df.height * 0.20f) - dm.height);

            try {
                popup.show(frame.getContentPane(), x, y);
            } catch (Exception e) {
                logger.error(e);
            }

            // launch a closing timer
            ThreadManager.runLater(new ClosingTask(popup), true, defaultTime);

        });
    }

    public void setBackgroundColor(Color background) {
        messagePanel.setBackground(background);
        messagePanel.refresh();
    }

    /**
     * Close message box pop up
     */
    private class ClosingTask implements Runnable {

        private JPopupMenu origin;

        public ClosingTask(JPopupMenu origin) {
            this.origin = origin;
        }

        public void run() {
            if (popup == origin) {
                // TODO: play with transparency
                popup.setVisible(false);
            }

        }
    }

}
