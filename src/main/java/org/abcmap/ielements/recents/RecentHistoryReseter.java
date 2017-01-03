package org.abcmap.ielements.recents;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RecentHistoryReseter implements ActionListener {

    private static final CustomLogger logger = LogManager.getLogger(RecentHistoryReseter.class);

    private RecentManager recentsm;
    private GuiManager guim;
    private DialogManager dialm;
    private Mode mode;

    public RecentHistoryReseter(Mode mode) {

        this.mode = mode;

        this.recentsm = Main.getRecentManager();
        this.guim = Main.getGuiManager();
        this.dialm = Main.getDialogManager();
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
            dialm.showErrorInBox("Erreur lors de la réinitialisation de l'historique.");
        }

    }
}
