package abcmap.gui.ie.importation.robot;

import abcmap.gui.ie.display.window.ShowAutoImportWindow;
import abcmap.gui.ie.draw.SelectLayer;
import abcmap.gui.ie.importation.SelectCropAreaForScreen;
import abcmap.gui.ie.importation.SelectPictureAnalyseMode;
import abcmap.gui.iegroup.InteractionElementGroup;

public class MenuRobotImport extends InteractionElementGroup {

	public MenuRobotImport() {
		this.label = "Capture automatique d'écran...";
		this.help = "...";

		// affichage de la fenêtre d'import
		addInteractionElement(new ShowAutoImportWindow());

		// configuration zone de capture
		addInteractionElement(new SelectCaptureArea());

		// mode d'analyse
		addInteractionElement(new SelectRobotImportOptions());

		// mode d'analyse
		addInteractionElement(new SelectPictureAnalyseMode());

		// configuration du recadrage
		addInteractionElement(new SelectCropAreaForScreen());

		// calques
		addInteractionElement(new SelectLayer());

		// lancement de l'import auto
		addInteractionElement(new LaunchRobotImport());
	}

}
