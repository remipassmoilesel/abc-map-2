package org.abcmap.ielements.project;

import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.ClosingConfirmationDialog;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;

public class NewProject extends InteractionElement {

    public NewProject() {
        this.label = "Nouveau projet";
        this.help = "...";
        this.menuIcon = GuiIcons.SMALLICON_NEWPROJECT;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if(getOperationLock() == false){
            return;
        }

        try {

            // ask to close project if one is open
            if (projectm().isInitialized() == true) {

                QuestionResult cc = ClosingConfirmationDialog
                        .showProjectConfirmationAndWait(guim().getMainWindow());

                // user cancel
                if (cc.isAnswerCancel() == true) {
                    return;
                }

                // user want save project
                else if (cc.isAnswerYes() == true) {
                    SaveProject saver = new SaveProject();
                    saver.run();
                }

                // close project
                try {
                    projectm().closeProject();
                } catch (IOException e) {
                    dialm().showErrorInBox("Erreur lors de la fermeture du programme");
                    logger.error(e);
                }

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
