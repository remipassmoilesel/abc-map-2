package org.abcmap.ielements.profiles;

import org.abcmap.core.configuration.CFNames;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.dialogs.simple.BrowseDialogResult;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SaveAsProfile extends InteractionElement {

    public SaveAsProfile() {
        label = "Enregistrez sous le profil de configuration";
        help = "...";
    }

    @Override
    public void run() {

        // no operations on EDT
        GuiUtils.throwIfOnEDT();

        if (getOperationLock() == false) {
            return;
        }

        try {

            BrowseDialogResult result = dialm().browseProfileToSaveDialog();

            // action was canceled
            if (result.isActionCanceled()) {
                return;
            }

            try {

                // save profile at specified path
                Path selectedPath = result.getPath();

                // check extension
                String ext = ConfigurationConstants.PROFILE_EXTENSION;
                if (Utils.checkExtension(selectedPath, ext) == false) {
                    String newPath = selectedPath.toAbsolutePath().toString() + "." + ext;
                    selectedPath = Paths.get(newPath);
                }

                configm().saveCurrentConfiguration(selectedPath);

                // save path
                configm().getConfiguration().updateValue(CFNames.PROFILE_PATH, selectedPath.toString());

                // save profile in recents profiles
                recentm().addProfilePath(selectedPath.toAbsolutePath().toString());
                recentm().saveHistory();

                dialm().showMessageInBox("Le profil a été enregistré");

            } catch (IOException e) {
                dialm().showProfileWritingError();
                logger.error(e);
            }

        } finally {
            releaseOperationLock();
        }

    }
}
