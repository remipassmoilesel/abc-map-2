package org.abcmap.gui.ie.recents;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.gui.components.fileselection.FileSelectionPanel;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenRecentProject extends InteractionElement implements HasEventNotificationManager {

    private FileSelectionPanel fileSelectionPanel;
    private FileViewUpdater fileViewUpdater;

    public OpenRecentProject() {

        this.label = "Ouvrir un projet récent";
        this.help = "...";

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 5"));

        this.fileSelectionPanel = new FileSelectionPanel();
        fileSelectionPanel.addActionButtonListener(new ProjectOpener());
        fileSelectionPanel.addResetButtonListener(new RecentHistoryReseter(Mode.PROJECTS));

        panel.add(fileSelectionPanel, "width 98%!");

        this.fileViewUpdater = new FileViewUpdater();

        notifm.setDefaultListener(fileViewUpdater);
        recentsm.getNotificationManager().addObserver(this);

        fileViewUpdater.run();

        return panel;
    }

    private class ProjectOpener implements ActionListener {

//        private OpenProject opener;

        public ProjectOpener() {
            // objet d'ouverture de projet unique
//            this.opener = new OpenProject();

        }

        @Override
        public void actionPerformed(ActionEvent e) {

            /*

            File activFile = fileSelectionPanel.getActiveFile();
            if (activFile != null) {
                opener.openProject(activFile);
                ThreadManager.runLater(opener);
            }

            */

        }

    }

    private class FileViewUpdater extends FormUpdater {

        @Override
        protected void updateFields() {

            /*
            // recuperer la liste des fichiers
            ArrayList<File> files = recentsm.getProjectHistory();

            // effacer les entrées precedentes
            fileSelectionPanel.clearFileList();

            // ajouter les entrées actuelles
            fileSelectionPanel.addFiles(files);
            */
        }
    }

}
