package org.abcmap.core.managers;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.project.layers.AbmShapefileLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.GuiColors;
import org.abcmap.gui.components.messagebox.MessageBoxManager;
import org.abcmap.gui.dialogs.ClosingConfirmationDialog;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.dialogs.SupportProjectDialog;
import org.abcmap.gui.dialogs.simple.*;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * All dialogs and message box from software can be called from here.
 * <p>
 * This allow to not specify parent window of dialogs, by default main window will be choosed.
 */
public class DialogManager extends ManagerTreeAccessUtil {

    private static final CustomLogger logger = LogManager.getLogger(DialogManager.class);

    /**
     * Utility used to display messages in boxes on main window
     * <p>
     * These boxes are less invasive than dialogs which require user click to disappear
     */
    private MessageBoxManager messagebox;


    /**
     * Display an message in box
     *
     * @param message
     */
    public void showMessageInBox(String message) {
        showMessageInBox(null, message, GuiColors.INFO_BOX_BACKGROUND);
    }

    /**
     * Display an message in box
     *
     * @param message
     */
    public void showErrorInBox(String message) {
        showMessageInBox(null, message, GuiColors.ERROR_BOX_BACKGROUND);
    }

    /**
     * Display an message in box
     * <p>
     * If timeMilliSec is specified, message will disappear after this time. If not, default time will be used.
     *
     * @param message
     */
    public void showMessageInBox(Integer timeMilliSec, String message, Color background) {

        if (messagebox == null) {
            messagebox = new MessageBoxManager(Main.getGuiManager().getMainWindow());
        }

        if (timeMilliSec == null) {
            timeMilliSec = messagebox.getDefaultTime();
        }

        if (messagebox.isBoxVisible() == true) {
            // TODO: create a message queue ?
            logger.debug("Message already shown");
        }

        messagebox.setBackgroundColor(background);
        messagebox.showMessage(timeMilliSec, message);
    }

    /**
     * Show a dialog box
     *
     * @param
     */
    public void showSupportDialog() {
        showSupportDialog(guim().getMainWindow());
    }

    /**
     * Show a dialog box
     *
     * @param parent
     */
    public void showSupportDialog(Window parent) {

        GuiUtils.throwIfNotOnEDT();

        SupportProjectDialog dial = new SupportProjectDialog(parent);
        dial.setVisible(true);

    }

    /**
     * Show a support dialog of project
     *
     * @param
     */
    public void showSupportDialogAndWait() {
        showSupportDialogAndWait(guim().getMainWindow());
    }

    /**
     * Show a support dialog of project
     *
     * @param parent
     */

    public void showSupportDialogAndWait(final Window parent) {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    showSupportDialog(parent);
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            logger.error(e);
        }

    }

    /**
     * Predefined error message
     */
    public void showProfileWritingError() {
        showErrorInBox("Erreur lors de l'enregistrement du profil de configuration.");
    }

    /**
     * Predefined error message
     */
    public void showProjectWritingError() {
        showErrorInBox("Erreur lors de l'enregistrement du projet.");
    }

    /**
     * Predefined error message
     */
    public void showProjectNonInitializedError() {
        String message = "Vous devez d'abord créer ou ouvrir un projet avant de poursuivre.";
        showErrorInBox(message);
    }

    /**
     * Predefined error message
     */
    public void showOperationAlreadyRunningError() {
        String message = "Cette opération est déjà en cours. Veuillez patienter.";
        showErrorInBox(message);
    }

    /**
     * Predefined error message
     */
    public void showProjectWithoutLayoutError() {
        String message = "Vous devez d'abord mettre en page votre projet avant de pouvoir continuer.";
        showErrorInBox(message);
    }

    /**
     * Predefined error message
     */
    public void showErrorInDialog(String message, boolean wait) {
        showErrorInDialog(guim().getMainWindow(), message, wait);
    }

    /**
     * Predefined error message
     */
    public void showErrorInDialog(Window parent, String message, boolean wait) {

        if (wait) {
            GuiUtils.throwIfOnEDT();
            SimpleErrorDialog.showAndWait(parent, message);
        } else {
            SimpleErrorDialog.showLater(null, message);
        }

    }

    /**
     * Afficher un dialogue d'information
     *
     * @param parent
     * @param message
     * @param textFieldValue
     */
    public void showInformationTextFieldDialog(Window parent, String message, String textFieldValue) {
        InformationTextFieldDialog.showLater(parent, message, textFieldValue);
    }

    /**
     * Show a confirmation which ask user if he wants to save project
     * <p>
     * This confirmation stop current thread.
     *
     * @param parent
     * @return
     */
    public QuestionResult showProjectConfirmationDialog(Window parent) {
        return ClosingConfirmationDialog.showProjectConfirmationAndWait(parent);
    }

    /**
     * Show a browse dialog with project file filter
     *
     * @return
     */
    public BrowseDialogResult browseProjectToOpenDialog() {
        return SimpleBrowseDialog.browseProjectToOpenAndWait(guim().getMainWindow());
    }

    /**
     * Show a browse dialog with a project file filter
     *
     * @return
     */
    public BrowseDialogResult browseProjectToSaveDialog() {
        return SimpleBrowseDialog.browseProfileToSaveAndWait(guim().getMainWindow());
    }

    /**
     * Show a dialog asking user if he want to save project before close it
     *
     * @return
     */
    public QuestionResult showProjectClosingConfirmationDialog() {
        return ClosingConfirmationDialog.showProjectConfirmationAndWait(guim().getMainWindow());
    }

    public void showReprojectShapeFileDialog(AbmShapefileLayer layer, Runnable reprojectLayer, Runnable openAsIs, Runnable operationCanceled) {

        QuestionResult result = SimpleQuestionDialog.askQuestion(guim().getMainWindow(), "Reprojection", "Voulez vous ouvrir une copie " +
                        "de cette couche reprojetée ? Cela peut résoudre des problèmes d'affichage.",
                "Ouvrir une copie reprojetée", "Ouvrir tel quel", "Annuler");

        Runnable toRun;
        if (result.isAnswerYes() == true) {
            toRun = reprojectLayer;
        } else if (result.isAnswerNo() == true) {
            toRun = openAsIs;
        } else {
            toRun = operationCanceled;
        }

        try {
            ThreadManager.runLater(toRun);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void showReprojectShapeFileDialogAndWait(AbmShapefileLayer layer, Runnable reprojectLayer, Runnable openAsIs, Runnable operationCanceled) {

        try {
            SwingUtilities.invokeAndWait(() -> {
                showReprojectShapeFileDialog(layer, reprojectLayer, openAsIs, operationCanceled);
            });
        } catch (InterruptedException | InvocationTargetException e) {
            logger.error(e);
        }

    }
}
