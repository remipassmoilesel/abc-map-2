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

    public enum ConfirmationType {
        PROJECT, PROFILE,
    }

    /**
     * Show a dialog to confirm project closing
     *
     * @param parent
     * @return
     */
    public static QuestionResult showProjectConfirmation(Window parent) {

        GuiUtils.throwIfNotOnEDT();

        ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent, ConfirmationType.PROJECT);
        ccd.setVisible(true);

        return ccd.getResult();
    }

    /**
     * Show a dialog to confirm project closing, on current thread
     *
     * @param parent
     * @return
     */
    public static QuestionResult showProjectConfirmationAndWait(final Window parent) {

        GuiUtils.throwIfOnEDT();

        final QuestionResult[] result = new QuestionResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = showProjectConfirmation(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];

    }

    /**
     * Show a dialog to confirm profile closing
     *
     * @param parent
     * @return
     */
    public static QuestionResult showProfileConfirmation(Window parent) {

        GuiUtils.throwIfNotOnEDT();

        ClosingConfirmationDialog ccd = new ClosingConfirmationDialog(parent, ConfirmationType.PROFILE);
        ccd.setVisible(true);

        return ccd.getResult();
    }

    /**
     * Show a dialog to confirm profile closing, on current thread
     *
     * @param parent
     * @return
     */
    public static QuestionResult showProfileConfirmationAndWait(final Window parent) {

        final QuestionResult[] result = new QuestionResult[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                result[0] = showProfileConfirmation(parent);
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
            return null;
        }

        return result[0];

    }

    /**
     * Construct a confirmation dialog
     *
     * @param parent
     * @param type
     */
    public ClosingConfirmationDialog(Window parent, ConfirmationType type) {
        super(parent);

        if (ConfirmationType.PROJECT.equals(type)) {
            setMessage("Avant de fermer le projet courant, voulez vous l'enregistrer ?");
        } else if (ConfirmationType.PROFILE.equals(type)) {
            setMessage("Avant de fermer le profil de configuration courant, voulez vous l'enregistrer ?");
        }

        setYesText("Enregistrer");
        setNoText("Ne pas enregistrer");
        setCancelText("Annuler l'opération en cours");

        reconstruct();
    }
}
