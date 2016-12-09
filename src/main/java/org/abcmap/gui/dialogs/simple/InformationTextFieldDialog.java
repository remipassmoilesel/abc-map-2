package org.abcmap.gui.dialogs.simple;

import java.awt.Window;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Simple information dialog
 * <p>
 * Buttons can be changed by overriding getButtons()
 */
public class InformationTextFieldDialog extends SimpleInformationDialog {

    protected String textFieldValue;
    private JTextField textField;

    public static void showLater(final Window parent, final String message,
                                 final String textFieldValue) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                InformationTextFieldDialog itd = new InformationTextFieldDialog(parent, message,
                        textFieldValue);
                itd.setVisible(true);

            }
        });
    }

    public InformationTextFieldDialog(Window parent, String message, String textFieldValue) {
        super(parent);

        setMessage(message);
        setTextFieldValue(textFieldValue);

        reconstruct();
    }

    public void setTextFieldValue(String textFieldValue) {
        this.textFieldValue = textFieldValue;
    }

    public void reconstruct() {

        super.reconstruct();

        contentPane.remove(getButtonsPanel());

        textField = new JTextField();
        textField.setEditable(false);
        textField.setText(textFieldValue);

        contentPane.add(textField, "width 80%");

        addDefaultButtons();

        contentPane.revalidate();
        contentPane.repaint();

        pack();

        setLocationRelativeTo(null);

    }

}
