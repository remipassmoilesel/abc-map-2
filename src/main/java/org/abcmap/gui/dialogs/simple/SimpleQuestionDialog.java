package org.abcmap.gui.dialogs.simple;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

public class SimpleQuestionDialog extends SimpleInformationDialog {

    private static final CustomLogger logger = LogManager.getLogger(SimpleQuestionDialog.class);

    public static QuestionResult askQuestion(Window parent, String message) {
        return askQuestion(parent, "Question", message);
    }

    public static QuestionResult askQuestion(Window parent, String title,
                                             String message) {

        GuiUtils.throwIfNotOnEDT();

        SimpleQuestionDialog sd = new SimpleQuestionDialog(parent);
        sd.setTitle(title);
        sd.setMessage(message);
        sd.reconstruct();
        sd.setVisible(true);

        return sd.getResult();
    }

    public static QuestionResult askQuestionAndWait(final Window parent,
                                                    String title, final String message) {

        final QuestionResult result = new QuestionResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(askQuestion(parent, message));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
        }

        return result;
    }

    private String yesText;
    private String noText;
    private String cancelText;
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
    protected void addDefaultButtons() {

        buttonsPanel = new JPanel(new MigLayout());

        JButton buttonYes = new JButton(yesText);
        buttonYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.setReturnVal(QuestionResult.YES);
                dispose();
            }
        });
        buttonsPanel.add(buttonYes);

        JButton buttonNo = new JButton(noText);
        buttonNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.setReturnVal(QuestionResult.NO);
                dispose();
            }
        });
        buttonsPanel.add(buttonNo);

        JButton buttonCancel = new JButton(cancelText);
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result.setReturnVal(QuestionResult.CANCEL);
                dispose();
            }
        });
        buttonsPanel.add(buttonCancel);

        contentPane.add(buttonsPanel, "align right, gapright 15px, wrap 15px,");

    }

    private class ClosingWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            result.setReturnVal(QuestionResult.CANCEL);
            dispose();
        }
    }

    public void setYesText(String okText) {
        this.yesText = okText;
    }

    public void setNoText(String noText) {
        this.noText = noText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public QuestionResult getResult() {
        return result;
    }

}
