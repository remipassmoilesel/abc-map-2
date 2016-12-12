package org.abcmap.gui.ie.importation.robot;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.display.window.ShowAutoImportWindow;
import org.abcmap.gui.ie.draw.SelectLayer;
import org.abcmap.gui.ie.importation.SelectCropAreaForScreen;
import org.abcmap.gui.ie.importation.SelectPictureAnalyseMode;

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
