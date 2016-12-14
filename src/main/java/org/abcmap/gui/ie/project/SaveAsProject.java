package org.abcmap.gui.ie.project;

import org.abcmap.core.project.Project;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import java.awt.*;
import java.io.IOException;

public class SaveAsProject extends InteractionElement {

    public SaveAsProject() {
        this.label = "Enregistrer sous ...";
        this.help = "Cliquez ici pour enregistrer le projet sous...";
        this.menuIcon = GuiIcons.SMALLICON_SAVEAS;
        this.accelerator = shortcuts.SAVE_PROJECT_AS;
    }

    @Override
    public void run() {
        GuiUtils.throwIfOnEDT();

        Project project = getCurrentProjectOrShowMessage();
        if (project == null) {
            return;
        }

        if (getOperationLock() == false) {
            return;
        }

        try {

            // TODO clean project
            // projectm.cleanCurrentProject();

            // display browse dialog
            Window parent = guim.getMainWindow();
            BrowseDialogResult result = dialm.browseProjectToOpenDialog();

            // user cancel action
            if (result.isActionCanceled() == true) {
                return;
            }

            // save project
            boolean saved = false;
            project.setFinalPath(result.getFile().toPath());
            try {
                projectm.saveProject();
                saved = true;
            } catch (IOException e) {
                logger.error(e);
                dialm.showErrorInBox("Erreur lors de l'enregistrement du projet");
            }

            // keep project in recent history
            if (saved) {
                try {
                    recentsm.add(project);
                    recentsm.saveHistory();
                } catch (IOException e) {
                    logger.error(e);
                }
            }

            dialm.showMessageInBox("Le projet a été enregistré");

        } finally {
            releaseOperationLock();
        }

    }

}
