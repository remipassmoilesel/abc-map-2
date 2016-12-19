package org.abcmap.ielements.importation.robot;

import org.abcmap.ielements.InteractionElementGroup;
import org.abcmap.ielements.display.window.ShowAutoImportWindow;
import org.abcmap.ielements.draw.SelectLayer;
import org.abcmap.ielements.importation.SelectCropAreaForScreen;
import org.abcmap.ielements.importation.SelectPictureAnalyseMode;

public class MenuRobotImport extends InteractionElementGroup {

    public MenuRobotImport() {
        this.label = "Capture automatique d'écran...";
        this.help = "...";

        addInteractionElement(new ShowAutoImportWindow());

        addInteractionElement(new SelectCaptureArea());

        addInteractionElement(new SelectRobotImportOptions());

        addInteractionElement(new SelectPictureAnalyseMode());

        addInteractionElement(new SelectCropAreaForScreen());

        addInteractionElement(new SelectLayer());

        addInteractionElement(new LaunchRobotImport());
    }

}
