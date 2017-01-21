package org.abcmap.ielements.project;

import net.miginfocom.swing.MigLayout;
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

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        textField = new JTextArea();

        panel.add(new JScrollPane(textField), "width 80%, height 70px, grow");

        projectListener = new ProjectListener();

        notifm.addEventListener(projectListener);
        projectm().getNotificationManager().addObserver(this);

        //TextFieldDelayedAction.delayedActionFor(textField, new TextFieldListener(), false);

        projectListener.run();

        return panel;

    }

    private class ProjectListener extends FormUpdater {

        @Override
        public void updateFormFields() {

			/*

			// projet non initialisé, retour
			if (projectm.isInitialized() == false) {
				GuiUtils.changeText(textField, "");
				return;
			}

			// commentaire du projet
			String projectComment = projectm.getMetadatas().PROJECT_COMMENT;

			GuiUtils.changeText(textField, projectComment);

			*/
        }

    }


    private class TextFieldListener implements Runnable {

        @Override
        public void run() {

            /*


            // Verifier le projet et obtenir le calque actif, ou afficher un
            // message d'erreur
            MapLayer layer = checkProjectAndGetActiveLayer();
            if (layer == null) {
                return;
            }

            // commentaire du projet
            String projectComment = configm.getConfiguration().PROFILE_COMMENT;

            // recuperer le titre saisi
            String fieldComment = textField.getText();

            if (Utils.safeEquals(projectComment, fieldComment) == false) {
                projectm.getMetadatas().PROJECT_COMMENT = fieldComment;
                projectm.fireMetadatasChanged();
            }

            */

        }

    }

}
