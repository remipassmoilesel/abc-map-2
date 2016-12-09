package org.abcmap.gui.dialogs;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.dialogs.simple.SimpleQuestionDialog;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class ClosingConfirmationDialog extends SimpleQuestionDialog {

    private static final CustomLogger logger = LogManager.getLogger(ClosingConfirmationDialog.class);

    public static enum ConfirmationType {
        PROJECT, PROFILE,
    }

    public static QuestionResult showProjectConfirmation(Window parent) {

        GuiUtils.throwIfNotOnEDT();

        ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent, ConfirmationType.PROJECT);
        ccd.setVisible(true);

        return ccd.getResult();
    }

    public static QuestionResult showProjectConfirmationAndWait(final Window parent) {

        final QuestionResult result = new QuestionResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(showProjectConfirmation(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;

    }

    public static QuestionResult showProfileConfirmation(Window parent) {

        GuiUtils.throwIfNotOnEDT();

        ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent, ConfirmationType.PROFILE);
        ccd.setVisible(true);

        return ccd.getResult();
    }

    public static QuestionResult showProfileConfirmationAndWait(final Window parent) {

        final QuestionResult result = new QuestionResult();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    result.update(showProfileConfirmation(parent));
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result;

    }

    public ClosingConfirmationDialog(Window parent, ConfirmationType type) {
        super(parent);

        if (ConfirmationType.PROJECT.equals(type)) {
            setMessage("Voulez vous enregistrer le projet courant ?");
        } else if (ConfirmationType.PROFILE.equals(type)) {
            setMessage("Voulez vous enregistrer le profil de configuration courant ?");
        }

        setYesText("Enregistrer");
        setNoText("Ne pas enregistrer");
        setCancelText("Annuler l'opération en cours");

        reconstruct();
    }
}
