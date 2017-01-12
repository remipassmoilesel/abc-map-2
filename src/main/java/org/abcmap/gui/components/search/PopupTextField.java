package org.abcmap.gui.components.search;

import org.abcmap.gui.utils.KeyAdapter;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Special text field with an associated popup
 */
public class PopupTextField extends JTextField {

    private static final Pattern WHITE_TEXT = Pattern.compile("^\\W*$");
    private JLabel testLabel;
    private InteractivePopupDisplay popup;

    public PopupTextField() {

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkContentAndShowPopup();
            }
        });

        // popup element where are displayed results
        this.popup = new InteractivePopupDisplay(this);

    }

    public synchronized void checkContentAndShowPopup() {

        // no text, hide panel
        Matcher m = WHITE_TEXT.matcher(getText());
        if (m.find()) {
            showPopup(false);
        }

        // text present, launch search
        else {
            userHaveTypedThis(getText());
        }

        // request focus after popup is shown
        SwingUtilities.invokeLater(()->{
            requestFocus();
            requestFocusInWindow();
        });

    }

    /**
     * Launch search when user type text
     *
     * @param text
     */
    protected void userHaveTypedThis(String text) {

        if (testLabel == null) {
            testLabel = new JLabel();
            getPopupContentPane().add(testLabel);
        }

        testLabel.setText(text);
        testLabel.revalidate();
        testLabel.repaint();

        showPopup(true);
        refreshPopup();

    }

    protected void refreshPopup() {
        popup.revalidate();
        popup.repaint();
    }

    protected JPanel getPopupContentPane() {
        return (JPanel) popup.getContentPane();
    }

    protected void showPopup(boolean val) {
        popup.showPopup(val);
    }

    public InteractivePopupDisplay getPopup() {
        return popup;
    }

}
