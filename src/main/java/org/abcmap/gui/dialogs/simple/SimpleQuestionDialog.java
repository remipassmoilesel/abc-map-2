package org.abcmap.gui.dialogs.simple;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * Dialog which can
 */
public class SimpleQuestionDialog extends SimpleInformationDialog {

    private static final CustomLogger logger = LogManager.getLogger(SimpleQuestionDialog.class);

    public static QuestionResult askQuestion(Window parent, String message) {
        return askQuestion(parent, "Question", message, null, null, null);
    }

    /**
     * Ask a question with specified parameters
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static QuestionResult askQuestion(Window parent, String title,
                                             String message, String buttonYesText, String buttonNoText, String buttonCancelText) {

        GuiUtils.throwIfNotOnEDT();

        SimpleQuestionDialog sd = new SimpleQuestionDialog(parent);

        if (buttonYesText != null) {
            sd.setYesText(buttonYesText);
        }

        if (buttonNoText != null) {
            sd.setNoText(buttonNoText);
        }

        if (buttonCancelText != null) {
            sd.setCancelText(buttonCancelText);
        }

        sd.setTitle(title);
        sd.setMessage(message);
        sd.reconstruct();
        sd.setVisible(true);

        return sd.getResult();
    }

    /**
     * Ask a question with specified parameters, on current thread
     *
     * @param parent
     * @param title
     * @param message
     * @return
     */
    public static QuestionResult askQuestionAndWait(final Window parent, String title,
                                                    String message, String buttonYesText, String buttonNoText, String buttonCancelText) {

        final QuestionResult[] result = new QuestionResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = askQuestion(parent, title, message, buttonYesText, buttonNoText, buttonCancelText);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
        }

        return result[0];
    }

    /**
     * Text of "YES" button
     */
    private String yesText;

    /**
     * Text of "NO" button
     */
    private String noText;

    /**
     * Text of "CANCEL" button
     */
    private String cancelText;

    /**
     * Result after user answer
     */
    private QuestionResult result;

    public SimpleQuestionDialog(Window parent) {
        super(parent);

        addWindowListener(new ClosingWindowListener());

        setModal(true);

        largeIcon = GuiIcons.DIALOG_QUESTION_ICON;

        this.yesText = "Oui";
        this.noText = "Non";
        this.cancelText = "Annuler";

        result = new QuestionResult();

        reconstruct();
    }

    @Override
    protected JPanel createButtonPanel() {

        JPanel buttonsPanel = new JPanel(new MigLayout("insets 5"));

        JButton buttonYes = new JButton(yesText);
        buttonYes.addActionListener((ev) -> {
            result.setReturnVal(QuestionResult.YES);
            dispose();
        });
        buttonsPanel.add(buttonYes);

        JButton buttonNo = new JButton(noText);
        buttonNo.addActionListener((ev) -> {
            result.setReturnVal(QuestionResult.NO);
            dispose();
        });
        buttonsPanel.add(buttonNo);

        JButton buttonCancel = new JButton(cancelText);
        buttonCancel.addActionListener((ev) -> {
            result.setReturnVal(QuestionResult.CANCEL);
            dispose();
        });
        buttonsPanel.add(buttonCancel);

        return buttonsPanel;
    }

    private class ClosingWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            result.setReturnVal(QuestionResult.CANCEL);
            dispose();
        }
    }

    /**
     * Set text of "YES" button
     *
     * @param okText
     */
    public void setYesText(String okText) {
        this.yesText = okText;
    }

    /**
     * Set text of "NO" button
     *
     * @param noText
     */
    public void setNoText(String noText) {
        this.noText = noText;
    }

    /**
     * Set text of "CANCEL" button
     *
     * @param cancelText
     */
    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    /**
     * Get answer of user
     */
    public QuestionResult getResult() {
        return result;
    }

}
