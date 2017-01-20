package org.abcmap.gui.dialogs.simple;

import javax.swing.*;
import java.awt.*;

/**
 * Simple information dialog with a text field
 * <p>
 * Buttons can be changed by overriding getButtons()
 */
public class InformationTextFieldDialog extends SimpleInformationDialog {

    protected String textFieldValue;
    private JTextField textField;

    public static void showLater(final Window parent, final String message, final String textFieldValue) {
        SwingUtilities.invokeLater(() -> {
            InformationTextFieldDialog itd = new InformationTextFieldDialog(parent, message, textFieldValue);
            itd.setVisible(true);
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

    @Override
    public void reconstruct() {

        super.reconstruct();

        contentPane.remove(getButtonsPanel());

        textField = new JTextField();
        textField.setEditable(false);
        textField.setText(textFieldValue);

        contentPane.add(textField, "width 80%");

        createButtonPanel();

        contentPane.revalidate();
        contentPane.repaint();

        pack();

        setLocationRelativeTo(null);

    }

}
