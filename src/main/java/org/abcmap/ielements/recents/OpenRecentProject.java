package org.abcmap.ielements.recents;

import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.fileselection.FileSelectionPanel;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.project.OpenProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class OpenRecentProject extends InteractionElement implements HasEventNotificationManager {

    private FileSelectionPanel fileSelectionPanel;
    private FileViewUpdater fileViewUpdater;

    public OpenRecentProject() {

        this.label = "Ouvrir un projet récent";
        this.help = "...";

    }

    @Override
    protected Component createPrimaryGUI() {

        this.fileSelectionPanel = new FileSelectionPanel();
        fileSelectionPanel.addActionButtonListener(new ProjectOpener());
        fileSelectionPanel.addResetButtonListener(new RecentHistoryReseter(Mode.PROJECTS));

        this.fileViewUpdater = new FileViewUpdater();

        notifm.addEventListener(fileViewUpdater);
        recentm().getNotificationManager().addObserver(this);

        fileViewUpdater.run();

        return fileSelectionPanel;
    }

    /**
     * Open project when user click on buttons
     */
    private class ProjectOpener implements ActionListener {

        private OpenProject openProject = new OpenProject();

        @Override
        public void actionPerformed(ActionEvent e) {

            File activFile = fileSelectionPanel.getActiveFile();
            if (activFile != null) {
                ThreadManager.runLater(() -> {
                    openProject.openProject(activFile.toPath());
                });
            }

            // no files selected
            else {
                dialm().showErrorInBox("Vous devez sélectionner un projet");
            }

        }

    }

    /**
     * Update view when history change
     */
    private class FileViewUpdater extends FormUpdater {

        @Override
        protected void updateFields() {
            ArrayList<String> paths = recentm().getProjectHistory();
            fileSelectionPanel.clearFileList();
            fileSelectionPanel.addPaths(paths);
        }
    }

}
