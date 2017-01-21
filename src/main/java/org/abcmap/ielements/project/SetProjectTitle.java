package org.abcmap.ielements.project;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.project.PMConstants;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.textfields.TextFieldDelayedAction;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class SetProjectTitle extends InteractionElement {

    private ProjectListener projectListener;
    private JTextField textField;

    public SetProjectTitle() {

        this.label = "Titre du projet";
        this.help = "...";

    }

    @Override
    protected Component createPrimaryGUI() {

        // create GUI with text fields
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        textField = new JTextField();
        panel.add(textField, "width 80%, grow");

        // listen project metadata changes
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

            String projectTitle = projectm().getProject().getMetadataContainer().getValue(PMConstants.TITLE);
            GuiUtils.changeTextWithoutFire(textField, projectTitle);
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
            String projectTitle = projectm().getProject().getMetadataContainer().getValue(PMConstants.TITLE);

            // get input
            String fieldTitle = textField.getText();

            if (Utils.safeEquals(projectTitle, fieldTitle) == false) {
                projectm().getProject().getMetadataContainer().updateValue(PMConstants.TITLE, fieldTitle);
                projectm().fireMetadatasChanged();
            }


        }

    }

}
