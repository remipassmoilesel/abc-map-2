package org.abcmap.gui.ie.importation.manual;

import org.abcmap.gui.ie.InteractionElementGroup;
import org.abcmap.gui.ie.display.window.ShowManualImportWindow;
import org.abcmap.gui.ie.draw.SelectLayer;
import org.abcmap.gui.ie.importation.SelectCropAreaForScreen;

public class MenuImportManualCapture extends InteractionElementGroup {

    public MenuImportManualCapture() {
        this.label = "Capture manuelle d'écran...";
        this.help = "...";

        addInteractionElement(new ShowManualImportWindow());

        addInteractionElement(new SelectCropAreaForScreen());

        addInteractionElement(new SelectLayer());

        addInteractionElement(new LaunchManualImport());

    }

}
