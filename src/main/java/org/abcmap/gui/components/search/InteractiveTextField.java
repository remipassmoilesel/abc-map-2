package org.abcmap.gui.components.search;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractiveTextField extends JTextField {

    private static final Pattern WHITE_TEXT = Pattern.compile("^\\W*$");
    private JLabel testLabel;
    private InteractivePopupDisplay popup;

    public InteractiveTextField() {

        this.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {

                // no text, hide panel
                Matcher m = WHITE_TEXT.matcher(getText());
                if (m.find()) {
                    showPopup(false);
                }

                // text present, launch search
                else {
                    userHaveTypedThis(getText());
                }

                requestFocus();

            }
        });

        // popup element where are displayed results
        this.popup = new InteractivePopupDisplay(this);

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
