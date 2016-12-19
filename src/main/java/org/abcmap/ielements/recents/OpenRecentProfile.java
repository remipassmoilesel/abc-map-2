package org.abcmap.ielements.recents;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.fileselection.FileSelectionPanel;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenRecentProfile extends InteractionElement {

    private FileSelectionPanel fileSelectionPanel;
    private RecentManagerListener recentManagerListener;

    public OpenRecentProfile() {

        label = "Ouvrir un profil récent";
        help = "...";

        displayInHideableElement = true;

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        this.fileSelectionPanel = new FileSelectionPanel();
        fileSelectionPanel.addActionButtonListener(new ProfileOpener());
        fileSelectionPanel.addResetButtonListener(new RecentHistoryReseter(Mode.PROFILES));

        this.recentManagerListener = new RecentManagerListener();

        notifm.setDefaultListener(recentManagerListener);
        recentsm.getNotificationManager().addObserver(this);

        panel.add(fileSelectionPanel, "width 98%!");

        recentManagerListener.run();

        return panel;

    }

    private class ProfileOpener implements ActionListener {

//        private OpenProfile opener;

        public ProfileOpener() {
//            this.opener = new OpenProfile();
        }

        @Override
        public void actionPerformed(ActionEvent e) {

			/*
            File activFile = fileSelectionPanel.getActiveFile();
			if (activFile != null && activFile.getAbsolutePath() != null) {
				opener.openProfile(activFile);
				ThreadManager.runLater(opener);
			}
			*/

        }

    }

    private class RecentManagerListener extends FormUpdater {

        protected void updateFields() {
            /*

            // recuperer la liste des fichiers
            ArrayList<File> files = recentsm.getProfileHistory();

            // effacer les entrées précédentes
            fileSelectionPanel.clearFileList();

            // ajouter les entrées actuelles
            fileSelectionPanel.addFiles(files);

            */
        }

    }

}
