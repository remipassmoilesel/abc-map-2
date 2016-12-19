package org.abcmap.ielements.program;

import org.abcmap.core.managers.MainManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.project.SaveProject;

import java.io.IOException;

public class QuitProgram extends InteractionElement {

    public QuitProgram() {

        this.label = "Quitter";
        this.help = "Cliquez ici pour quitter le programme.";
        this.accelerator = MainManager.getShortcutManager().QUIT_PROGRAM;
    }

    @Override
    public void run() {

        if (getOperationLock() == false) {
            return;
        }

        try{

            // Check if project should be saved
            // If softare is in debug mode, configmations are not shown
            if (MainManager.isDebugMode() == false && projectm.isInitialized()) {

                QuestionResult cc = dialm.showProjectConfirmationDialog(guim.getMainWindow());

                // user canceled action
                if (cc.isAnswerCancel()) {
                    return;
                }

                // user want to save
                else if (cc.isAnswerYes()) {
                    SaveProject saver = new SaveProject();
                    saver.run();
                }
            }

            // show support project dialog
            if (MainManager.isDebugMode() == false) {
                dialm.showSupportDialogAndWait(guim.getMainWindow());
            }

            // close project
            try {
                projectm.closeProject();
            } catch (IOException e1) {
                logger.error(e1);
            }

            // hide all windows
            guim.setAllWindowVisibles(false);

            // save project
            try {
                recentsm.saveHistory();
            } catch (IOException e) {
                logger.error(e);
            }

            // save configuration
            if (configm.isSaveProfileWhenQuit()) {
                try {
                    configm.saveCurrentProfile();
                } catch (IOException e) {
                    logger.error(e);
                }
            }

            MainManager.enableBackgroundWorker(false);

            // wait a little before quit
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e);
            }

            ThreadManager.shutDown();

            // exit JVM, tchao !
            System.exit(0);


        } finally {
            releaseOperationLock();
        }

    }

}
