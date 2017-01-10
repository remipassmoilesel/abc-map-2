package org.abcmap.ielements.importation.manual;

import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.ielements.display.window.ShowManualImportWindow;
import org.abcmap.ielements.draw.SelectLayer;
import org.abcmap.ielements.importation.SelectCropAreaForScreen;

public class MenuImportManualCapture extends GroupOfInteractionElements {

    public MenuImportManualCapture() {
        this.label = "Capture manuelle d'écran...";
        this.help = "...";

        addInteractionElement(new ShowManualImportWindow());

        addInteractionElement(new SelectCropAreaForScreen());

        addInteractionElement(new SelectLayer());

        addInteractionElement(new LaunchManualImport());

    }

}
