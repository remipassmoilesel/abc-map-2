package org.abcmap.gui.ie.project;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

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

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        textField = new JTextField();
        panel.add(textField, "width 80%, grow");

        projectListener = new ProjectListener();

        notifm.setDefaultListener(projectListener);
        projectm.getNotificationManager().addObserver(this);

        //TextFieldDelayedAction.delayedActionFor(textField, new TextFieldListener(), false);

        projectListener.run();

        return panel;
    }

    private class ProjectListener extends FormUpdater {

        @Override
        public void updateFields() {

            /*

            if (projectm.isInitialized() == false) {
                GuiUtils.changeText(textField, "");
                return;
            }

            // titre du profil
            String projectTitle = projectm.getMetadatas().PROJECT_TITLE;

            GuiUtils.changeText(textField, projectTitle);

            */
        }

    }

    private class TextFieldListener implements Runnable {

        @Override
        public void run() {

            /*

            // titre du profil
            String projectTitle = projectm.getMetadatas().PROJECT_TITLE;

            // titre du champs de texte
            String fieldTitle = textField.getText();

            if (Utils.safeEquals(projectTitle, fieldTitle) == false) {
                projectm.getMetadatas().PROJECT_TITLE = fieldTitle;
                projectm.fireMetadatasChanged();
            }

            */

        }

    }

}
