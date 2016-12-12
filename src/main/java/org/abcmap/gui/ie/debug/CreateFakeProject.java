package org.abcmap.gui.ie.debug;

import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

import java.io.IOException;

/**
 * Create a fake project for debug purposes
 */
public class CreateFakeProject extends InteractionElement {

    public CreateFakeProject() {
        label = "Créer un faux projet";
        help = "...";

        this.onlyDebugMode = true;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        if (getUserLockOrDisplayMessage() == false) {
            return;
        }

        try {
            try {
                projectm.createFakeProject();
            } catch (IOException e) {
                dialm.showErrorInBox("Erreur lors de la création du faux projet");
            }
        } finally {
            releaseUserLock();
        }


    }
}
