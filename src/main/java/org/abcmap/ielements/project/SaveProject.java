package org.abcmap.ielements.project;

import org.abcmap.gui.GuiIcons;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import java.io.IOException;

public class SaveProject extends InteractionElement {

    public SaveProject() {
        this.label = "Enregistrer";
        this.help = "Cliquez ici pour enregistrer le projet";
        this.menuIcon = GuiIcons.SMALLICON_SAVE;
        this.accelerator = shortcutm().SAVE_PROJECT;
    }

    @Override
    public void run() {
        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            // TODO clean project
            // projectm.cleanCurrentProject();

            // project was never saved
            if (projectm().getProject().getFinalPath() == null) {
                // propose save as
                SaveAsProject sap = new SaveAsProject();
                sap.run();
                return;
            }

            // save project
            try {

                // show work in progress
                dialm().showMessageInBox("Le projet est en cours d'enregistrement...");

                projectm().saveProject();
            }

            // error while writing project
            catch (IOException e) {
                dialm().showProjectWritingError();
                logger.error(e);
            }
        } finally {
            releaseOperationLock();
        }

    }

}
