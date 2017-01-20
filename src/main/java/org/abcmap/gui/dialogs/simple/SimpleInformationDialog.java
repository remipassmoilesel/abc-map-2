package org.abcmap.gui.dialogs.simple;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
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
     * Panel where buttons are added
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
        contentPane = new JPanel(new MigLayout("fillx, insets 5"));

        JLabel iconLbl = new JLabel(largeIcon);
        iconLbl.setVerticalAlignment(SwingConstants.TOP);
        contentPane.add(iconLbl, "west, gapleft 10px, gaptop 10px, gapright 15px,");

        HtmlLabel title = new HtmlLabel(dialogTitle);
        title.setStyle(GuiStyle.DIALOG_TITLE_1);
        contentPane.add(title, "grow, gaptop 10px, wrap 10px");

        // take care of font changes
        JEditorPane messageArea = new JEditorPane("text/html", message);
        messageArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        GuiStyle.applyStyleTo(GuiStyle.DIALOG_TEXT, messageArea);

        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        contentPane.add(messageArea, "grow, width 300px::500px, wrap 10px,");

        buttonsPanel = createButtonPanel();
        contentPane.add(buttonsPanel, "grow, gapright 10px, wrap 15px");

        setContentPane(contentPane);

        pack();

        setLocationRelativeTo(null);

        refresh();

    }

    /**
     * Create a panel with default buttons and add it to dialog
     * <p>
     * Override it to change buttons configuration
     */
    protected JPanel createButtonPanel() {

        JPanel buttonsPanel = new JPanel(new MigLayout("insets 5"));

        JButton hideButton = new JButton("Masquer ce message");
        hideButton.addActionListener((e) -> {
            dispose();

        });
        buttonsPanel.add(hideButton, "align right");

        return buttonsPanel;
    }

    /**
     * Set message of panel
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = "<html>" + message + "</html>";
    }

    /**
     * Set title of dialog
     *
     * @param title
     */
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

    /**
     * Return current panel where buttons are included
     *
     * @return
     */
    protected JPanel getButtonsPanel() {
        return buttonsPanel;
    }

    /**
     * Revalidate and repaint
     */
    public void refresh() {
        this.revalidate();
        this.repaint();
    }

}
