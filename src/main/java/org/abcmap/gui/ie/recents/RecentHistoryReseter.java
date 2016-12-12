package org.abcmap.gui.ie.recents;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.RecentManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RecentHistoryReseter implements ActionListener {

    private static final CustomLogger logger = LogManager.getLogger(RecentHistoryReseter.class);

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

        // reset profiles
        if (Mode.PROFILES.equals(mode)) {
            recentsm.clearProfileHistory();
        }

        // reset projects
        else if (Mode.PROJECTS.equals(mode)) {
            recentsm.clearProjectHistory();
        }

        // try write empty history
        try {
            recentsm.saveHistory();
        } catch (IOException e1) {
            logger.debug(e1);
            guim.getDialogManager().showErrorInBox("Erreur lors de la réinitialisation de l'historique.");
        }

    }
}
