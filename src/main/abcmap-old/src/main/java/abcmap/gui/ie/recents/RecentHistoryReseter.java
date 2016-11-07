package abcmap.gui.ie.recents;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.RecentManager;
import abcmap.managers.stub.MainManager;

public class RecentHistoryReseter implements ActionListener {

	public enum Mode {
		PROFILES, PROJECTS
	}

	private RecentManager recentsm;
	private GuiManager guim;
	private Mode mode;

	public RecentHistoryReseter(Mode mode) {

		this.mode = mode;

		this.recentsm = MainManager.getRecentManager();
		this.guim = MainManager.getGuiManager();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		// effacer l'historique
		if (Mode.PROFILES.equals(mode))
			recentsm.clearProfileHistory();

		else if (Mode.PROJECTS.equals(mode))
			recentsm.clearProjectHistory();

		// ecrire un historique vide
		try {
			recentsm.saveHistory();
		}

		// erreur lors de l'ecriture
		catch (IOException e1) {
			Log.debug(e1);
			guim.showErrorInBox("Erreur lors de la réinitialisation de l'historique.");
		}

	}
}
