package org.abcmap.gui.components.messagebox;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.components.color.ColorPicker;
import org.abcmap.gui.transition.Transition;

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

    /**
     * Parent frame of message box
     */
    private JFrame parentFrame;

    /**
     * If no time is specified, this one is used
     */
    private Integer defaultTime;

    /**
     * Background of panel
     */
    private Color messagePanelBackground;
    private boolean boxVisible;
    private JPopupMenu lastPopupDialog;

    public MessageBoxManager(JFrame parent) {
        this.parentFrame = parent;
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

    /**
     * Show a message on EDT
     *
     * @param timeMilliSec
     * @param message
     */
    public void showMessage(Integer timeMilliSec, final String message) {
        SwingUtilities.invokeLater(() -> {

            BoxMessagePanel messagePanel = new BoxMessagePanel();
            messagePanel.setBackground(messagePanelBackground);

            // do not show if main window is not visible
            if (parentFrame == null) {
                logger.error("Cannot show message, parent is invisible: " + message);
                return;
            }

            // create popup menu
            JPopupMenu popup = new JPopupMenu();
            popup.setOpaque(false);

            JPanel support = new JPanel(new BorderLayout());
            support.add(messagePanel, BorderLayout.CENTER);
            support.setBorder(null);

            popup.setBorder(null);
            popup.add(support);
            popup.pack();


            // hide on click
            messagePanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    popup.setVisible(false);
                }
            });

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

                lastPopupDialog = popup;

                messagePanel.setTransparency(0);
                popup.show(parentFrame.getContentPane(), x, y);

                // show message
                messagePanel.startTransition(Transition.FADE_OUT, () -> {
                    // hide it after specified time
                    popup.setVisible(false);
                });
            }
            // sometimes errors can be thrown on show
            catch (Exception e) {
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
        this.messagePanelBackground = background;
    }


    /**
     * Return default time value used if no time is specified at display
     *
     * @return
     */
    public Integer getDefaultTime() {
        return defaultTime;
    }

    /**
     * Set default time used if not time is specified
     *
     * @param defaultTime
     */
    public void setDefaultTime(Integer defaultTime) {
        this.defaultTime = defaultTime;
    }

    /**
     * Return true if a message is being show
     *
     * @return
     */
    public boolean isBoxVisible() {
        return lastPopupDialog != null ? lastPopupDialog.isVisible() : false;
    }
}
