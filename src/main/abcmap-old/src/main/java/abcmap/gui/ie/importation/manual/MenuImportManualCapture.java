package abcmap.gui.ie.importation.manual;

import abcmap.gui.ie.display.window.ShowManualImportWindow;
import abcmap.gui.ie.draw.SelectLayer;
import abcmap.gui.ie.importation.SelectCropAreaForScreen;
import abcmap.gui.iegroup.InteractionElementGroup;

public class MenuImportManualCapture extends InteractionElementGroup {

	public MenuImportManualCapture() {
		this.label = "Capture manuelle d'écran...";
		this.help = "...";

		// fenêtre d'import manuel
		addInteractionElement(new ShowManualImportWindow());

		// configuration du recadrage
		addInteractionElement(new SelectCropAreaForScreen());

		// calques
		addInteractionElement(new SelectLayer());

		// bouton démarrer
		addInteractionElement(new LaunchManualImport());

	}

}
