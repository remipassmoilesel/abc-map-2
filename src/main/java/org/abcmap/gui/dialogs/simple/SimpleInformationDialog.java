package org.abcmap.gui.dialogs.simple;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Information dialog
 */
public class SimpleInformationDialog extends JDialog {

    /**
     * Dialog title
     */
    protected String dialogTitle;

    /**
     * Icon of dialog
     */
    protected ImageIcon largeIcon;

    /**
     * Main message of dialog
     */
    protected String message;

    /**
     * Dimensions of dialog
     */
    protected Dimension dimensions;

    /**
     * Main content of dialog
     */
    protected JPanel contentPane;

    /**
     * Panel where are stored buttons
     */

    protected JPanel buttonsPanel;

    public SimpleInformationDialog(Window parent) {
        super(parent);

        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new CustomWindowListener());

        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);

        this.dialogTitle = "Information";

        this.largeIcon = GuiIcons.DIALOG_INFORMATION_ICON;

        this.message = "Lorem ipsum ....";

        reconstruct();

    }

    public void reconstruct() {

        GuiUtils.throwIfNotOnEDT();

        setTitle(dialogTitle);
        contentPane = new JPanel(new MigLayout("insets 5"));

        JLabel iconLbl = new JLabel(largeIcon);
        iconLbl.setVerticalAlignment(SwingConstants.TOP);
        contentPane.add(iconLbl, "west, gapleft 10px, gaptop 10px, gapright 15px,");

        HtmlLabel title = new HtmlLabel(dialogTitle);
        title.setStyle(GuiStyle.DIALOG_TITLE_1);
        contentPane.add(title, "gaptop 10px, wrap 10px");

        // take care of font changes
        JEditorPane messageArea = new JEditorPane("text/html", message);
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        GuiStyle.applyStyleTo(GuiStyle.DIALOG_TEXT, messageArea);

        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        contentPane.add(messageArea, "width 300px!, wrap 10px,");

        addDefaultButtons();

        setContentPane(contentPane);

        pack();

        setLocationRelativeTo(null);

        refresh();

    }

    /**
     * Get a panel with default buttons
     */
    protected void addDefaultButtons() {

        buttonsPanel = new JPanel(new MigLayout("insets 5"));

        JButton hideButton = new JButton("Masquer ce message");
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonsPanel.add(hideButton, "align right");

        contentPane.add(buttonsPanel, "align right, gapright 15px, wrap 15px,");
    }

    public void setMessage(String message) {
        this.message = "<html>" + message + "</html>";
    }

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        dialogTitle = title;
    }

    private class CustomWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            dispose();
        }
    }

    protected JPanel getButtonsPanel() {
        return buttonsPanel;
    }

    public void refresh() {
        this.revalidate();
        this.repaint();
    }

}
