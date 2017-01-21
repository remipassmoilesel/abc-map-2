package org.abcmap.ielements.project;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.project.PMNames;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.textfields.TextFieldDelayedAction;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;

public class SetProjectComment extends InteractionElement {

    private ProjectListener projectListener;
    private JTextArea textField;

    public SetProjectComment() {

        this.label = "Commentaire sur le projet";
        this.help = "...";

    }

    @Override
    protected Component createPrimaryGUI() {

        // create GUI with text field
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        textField = new JTextArea();
        panel.add(new JScrollPane(textField), "width 80%, height 70px, grow");

        // listen project changes
        projectListener = new ProjectListener();
        projectListener.addEventNameFilter(ProjectEvent.METADATA_CHANGED);
        projectListener.addEventNameFilter(ProjectEvent.NEW_PROJECT_LOADED);

        notifm.addEventListener(projectListener);
        projectm().getNotificationManager().addObserver(this);

        // listen user input
        TextFieldDelayedAction.delayedActionFor(textField, new TextFieldListener(), false);

        // first update
        projectListener.run();

        return panel;

    }

    /**
     * Listen project changes and change text field
     */
    private class ProjectListener extends FormUpdater {

        @Override
        public void updateFormFields() {

            if (projectm().isInitialized() == false) {
                GuiUtils.changeTextWithoutFire(textField, "");
                return;
            }

            String projectComment = projectm().getProject().getMetadataContainer().getValue(PMNames.COMMENT);
            GuiUtils.changeTextWithoutFire(textField, projectComment);
        }

    }

    /**
     * Listen user input and change project
     */
    private class TextFieldListener implements Runnable {

        @Override
        public void run() {

            // project is not initialized, return
            if (projectm().isInitialized() == false) {
                return;
            }

            // get title from project
            String projectComment = projectm().getProject().getMetadataContainer().getValue(PMNames.COMMENT);

            // get input
            String fieldComment = textField.getText();

            if (Utils.safeEquals(projectComment, fieldComment) == false) {
                projectm().getProject().getMetadataContainer().updateValue(PMNames.COMMENT, fieldComment);
                projectm().fireMetadatasChanged();
            }


        }

    }

}
