package abcmap.gui.ie.project;

import java.io.IOException;

import abcmap.gui.GuiIcons;
import abcmap.gui.dialogs.ClosingConfirmationDialog;
import abcmap.gui.dialogs.QuestionResult;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;

public class NewProject extends InteractionElement {

	public NewProject() {
		this.label = Lng.get("new project");
		this.help = Lng.get("new project help");
		this.menuIcon = GuiIcons.SMALLICON_NEWPROJECT;
	}

	@Override
	public void run() {

		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// fermer le projet si ouvert
		if (projectm.isInitialized() == true) {

			QuestionResult cc = ClosingConfirmationDialog
					.showProjectConfirmationAndWait(guim.getMainWindow());

			// cas l'utilisateur annule, retour
			if (cc.isAnswerCancel()) {
				threadAccess.releaseAccess();
				return;
			}

			// l'utilisateur souhaite sauvegarder
			else if (cc.isAnswerYes()) {
				SaveProject saver = new SaveProject();
				saver.run();
			}

			CloseProject closer = new CloseProject();
			closer.closeProject();

		}

		// nouveau projet
		try {
			projectm.newProject();
		} catch (IOException e) {
			guim.showErrorInBox("Erreur lors de la création d'un nouveau projet.");
			Log.error(e);
		}

		threadAccess.releaseAccess();
	}

}
