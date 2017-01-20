package org.abcmap.ielements.project;

import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;
import java.nio.file.Path;

public class OpenProject extends InteractionElement {

    public OpenProject() {
        this.label = "Ouvrir un projet";
        this.help = "...";
        this.accelerator = Main.getKeyboardManager().OPEN_PROJECT;
        this.menuIcon = GuiIcons.SMALLICON_OPENPROJECT;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            // confirm project closing
            if (requestConfirmationOfClosure() == false) {
                return;
            }

            // browse projects to open
            BrowseDialogResult result = dialm().browseProjectToOpenDialog();

            // user cancel operation
            if (result.isActionCanceled()) {
                return;
            }

            openProject(result.getPath());

        } finally {
            releaseOperationLock();
        }

    }

    /**
     * Request confirmation of closure and propose to save project.
     * <p>
     * Return true if user accept to close project, false otherwise.
     *
     * @return
     */
    private boolean requestConfirmationOfClosure() {

        // confirm project closing
        if (projectm().isInitialized()) {
            QuestionResult cc = dialm().showProjectClosingConfirmationDialog();

            // user answer cancel, return
            if (cc.isAnswerCancel() == true) {
                return false;
            }

            // user answer yes, save project
            else if (cc.isAnswerYes() == true) {
                SaveProject sp = new SaveProject();
                sp.run();
            }
        }

        return true;
    }

    /**
     * Ask confirmation to user and open specified project
     *
     * @param projectToOpen
     */
    public void confirmCloseAndOpenProject(Path projectToOpen) {

        // confirm project closing
        if (requestConfirmationOfClosure() == false) {
            return;
        }

        openProject(projectToOpen);

    }

    /**
     * Open operation available in a public method to be used programmatically
     *
     * @param projectToOpen
     */
    public void openProject(Path projectToOpen) {

        try {
            projectm().closeProject();
        } catch (IOException e1) {
            dialm().showErrorInBox("Erreur lors de la fermeture du projet.");
            logger.error(e1);
        }

        try {
            projectm().openProject(projectToOpen);
            dialm().showMessageInBox("Le projet a été ouvert");

            // add project in recent history
            try {
                recentm().addCurrentProject();
                recentm().fireHistoryChanged();
                recentm().saveHistory();
            } catch (IOException e) {
                logger.error(e);
            }

        }

        // error while openning project
        catch (Exception e) {
            dialm().showErrorInBox("Erreur lors de l'ouverture du projet.");
            logger.error(e);
        }


    }

}
