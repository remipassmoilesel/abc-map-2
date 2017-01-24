package org.abcmap.ielements.profiles;

import org.abcmap.core.configuration.CFNames;
import org.abcmap.gui.dialogs.QuestionResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

public class CreateNewProfile extends InteractionElement {

    public CreateNewProfile() {
        this.label = "Créer un nouveau profil...";
        this.help = "Cliquez ici pour créer un nouveau profil de configuration.";
    }

    @Override
    public void run() {

        // no operations on EDT
        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {
            QuestionResult result = dialm().showProfileClosingConfirmationDialog();

            // user cancel operation
            if (result.isAnswerCancel()) {
                return;
            }

            // user want to save
            if (result.isAnswerYes()) {
                new SaveProfile().run();
            }

            // reset configuration
            configm().resetConfiguration();

            // reset profile path
            configm().getConfiguration().updateValue(CFNames.PROFILE_PATH, "");

            // fire an event
            configm().fireConfigurationUpdated();

            // save profile
            new SaveAsProfile().run();

            dialm().showMessageInBox("Un nouveau profil a été créé");

        } finally {
            releaseOperationLock();
        }
    }
}
