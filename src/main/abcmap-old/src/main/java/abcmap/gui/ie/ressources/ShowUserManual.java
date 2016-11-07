package abcmap.gui.ie.ressources;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;

public class ShowUserManual extends InteractionElement {

	private File manualFile;

	public ShowUserManual() {

		label = "Afficher le manuel hors ligne";
		help = "Cliquez ici afficher le manuel hors ligne.";

		this.displaySimplyInSearch = true;

		manualFile = new File("./help/manual_" + configm.getConfiguration().LANGUAGE + ".pdf");

	}

	@Override
	public void run() {

		// pas de lancement dans l'edt
		GuiUtils.throwIfOnEDT();

		// affichage du PDF
		try {
			Desktop.getDesktop().open(manualFile);
		}

		// erreur lors de l'affichage
		catch (Exception e) {
			Log.error(e);

			guim.showInformationTextFieldDialog(guim.getMainWindow(),
					"Impossible de lancer votre lecteur PDF. Vous pouvez néanmoins "
							+ "trouver le manuel à l'emplacement: ",
					manualFile.getAbsolutePath());

		}

	}

}
