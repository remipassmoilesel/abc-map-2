package org.abcmap.ielements.profiles;

import org.abcmap.core.configuration.CFNames;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;

public class SaveProfile extends InteractionElement {

    public SaveProfile() {
        this.label = "Sauvegarder le profil courant";
        this.help = "Cliquez ici pour sauvegarder le profil courant. Si le profil n'a jamais été enregistré,"
                + " une boite de dialogue vous demandera un emplacement d'enregistrement.";
    }

    @Override
    public void run() {

        // no run on EDT
        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            // get configuration path
            String path = configm().getConfiguration().getValue(CFNames.PROFILE_PATH);

            // path is empty, profile was never saved
            if (path.equals("")) {
                new SaveAsProfile().run();
                return;
            }

            // otherwise save profile at specified location
            try {
                configm().saveCurrentProfile();
                dialm().showMessageInBox("Le profil a été enregistré");
            } catch (IOException e) {
                logger.error(e);
                dialm().showProfileWritingError();
            }
        } finally {
            releaseOperationLock();
        }
    }

}
