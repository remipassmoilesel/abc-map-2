package org.abcmap.ielements.project;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.ClosingConfirmationDialog;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;

public class CloseProject extends InteractionElement {

    public CloseProject() {
        this.label = "Fermer le projet";
        this.help = "...";
        this.menuIcon = GuiIcons.SMALLICON_CLOSEPROJECT;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            // confirm closing
            if (projectm().isInitialized()) {

                QuestionResult cc = ClosingConfirmationDialog
                        .showProjectConfirmationAndWait(guim().getMainWindow());

                // user cancel
                if (cc.isAnswerCancel()) {
                    releaseOperationLock();
                    return;
                }

                // save project before
                else if (cc.isAnswerYes()) {
                    SaveProject saver = new SaveProject();
                    saver.run();
                }

            }

            // close project
            try {
                projectm().closeProject();
            } catch (IOException e) {
                dialm().showErrorInBox("Erreur lors de la fermeture du programme");
                logger.error(e);
            }

            // create a new project
            try {
                projectm().createNewProject();
            } catch (IOException e) {
                dialm().showErrorInBox("Erreur lors de la création d'un nouveau projet.");
                logger.error(e);
            }


        } finally {
            releaseOperationLock();
        }

    }

}
