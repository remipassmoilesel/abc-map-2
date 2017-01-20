package org.abcmap.ielements.project;

import org.abcmap.core.project.Project;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveAsProject extends InteractionElement {

    public SaveAsProject() {
        this.label = "Enregistrer sous ...";
        this.help = "Cliquez ici pour enregistrer le projet à l'emplacement de votre choix.";
        this.menuIcon = GuiIcons.SMALLICON_SAVEAS;
        this.accelerator = shortcutm().SAVE_PROJECT_AS;
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
            BrowseDialogResult result = dialm().browseProjectToSaveDialog();

            // if user cancel action, stop all
            if (result.isActionCanceled() == true) {
                return;
            }

            Path finalPath = result.getFile().toPath();

            // check extension
            if (Utils.checkExtension(finalPath, "abm") == false) {
                String newPath = finalPath.toAbsolutePath().toString() + ".abm";
                finalPath = Paths.get(newPath);
            }

            // save project
            project.setFinalPath(finalPath);
            try {
                projectm().saveProject();

                // keep in recent history
                try {
                    recentm().addCurrentProject();
                    recentm().fireHistoryChanged();
                    recentm().saveHistory();
                } catch (IOException e) {
                    logger.error(e);
                }

                dialm().showMessageInBox("Le projet a été enregistré");

            } catch (IOException e) {
                logger.error(e);
                dialm().showErrorInBox("Erreur lors de l'enregistrement du projet");
            }

        } finally {
            releaseOperationLock();
        }

    }

}
