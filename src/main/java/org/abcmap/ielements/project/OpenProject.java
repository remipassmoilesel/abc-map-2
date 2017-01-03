package org.abcmap.ielements.project;

import org.abcmap.core.managers.Main;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import java.io.IOException;
import java.nio.file.Path;

public class OpenProject extends InteractionElement {

    public OpenProject() {
        this.label = "Ouvrir un projet";
        this.help = "...";
        this.accelerator = Main.getShortcutManager().OPEN_PROJECT;
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
            if (Main.isDebugMode() == false && projectm.isInitialized()) {
                QuestionResult cc = dialm.showProjectClosingConfirmationDialog();

                // user respond cancel or no
                if (cc.isAnswerYes() == false) {
                    return;
                }
            }

            BrowseDialogResult result = dialm.browseProjectToOpenDialog();

            // user canceled operation
            if (result.isActionCanceled()) {
                return;
            }

            openProject(result.getPath());

        } finally {
            releaseOperationLock();
        }

    }

    /**
     * Open operation available in a public method to be used programmatically
     *
     * @param projectToOpen
     */
    public void openProject(Path projectToOpen) {

        try {
            projectm.closeProject();
        } catch (IOException e1) {
            dialm.showErrorInBox("Erreur lors de la fermeture du projet.");
            logger.error(e1);
        }

        boolean opened = false;
        try {
            projectm.openProject(projectToOpen);
            dialm.showErrorInBox("Le projet a été ouvert");
            opened = true;
        }

        // error while openning project
        catch (Exception e) {
            dialm.showErrorInBox("Erreur lors de l'ouverture du projet.");
            logger.error(e);
        }

        // add project in recents
        if (opened) {
            try {
                recentsm.addCurrentProject();
                recentsm.saveHistory();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

}
