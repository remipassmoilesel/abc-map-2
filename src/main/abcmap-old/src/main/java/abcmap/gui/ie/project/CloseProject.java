package abcmap.gui.ie.project;

import java.io.IOException;

import abcmap.gui.GuiIcons;
import abcmap.gui.dialogs.ClosingConfirmationDialog;
import abcmap.gui.dialogs.QuestionResult;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;

public class CloseProject extends InteractionElement {

	public CloseProject() {
		this.label = Lng.get("close project");
		this.help = Lng.get("close project help");
		this.menuIcon = GuiIcons.SMALLICON_CLOSEPROJECT;
	}

	@Override
	public void run() {

		GuiUtils.throwIfOnEDT();

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// threadAccess.releaseAccess();

		// confirmation d'enregistrement de projet uniquement hors debug mode
		if (MainManager.isDebugMode() == false && projectm.isInitialized()) {

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

		}

		// fermer le programme
		closeProject();

		threadAccess.releaseAccess();
	}

	public void closeProject() {
		// fermeture du projet
		try {
			projectm.closeProject();
		}

		catch (IOException e) {
			Log.error(e);
		}
	}

}
