package org.abcmap.ielements.profiles;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;

public class SetProfileComment extends InteractionElement {

    private ProfileListener profileListener;
    private JTextArea textField;

    public SetProfileComment() {
        label = "Commentaire sur le profil";
        help = "...";
    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        textField = new JTextArea();

        panel.add(new JScrollPane(textField), "width 80%, height 70px, grow");

        profileListener = new ProfileListener();

        notifm.setDefaultListener(profileListener);
        configm().getNotificationManager().addObserver(this);

        //TextFieldDelayedAction.delayedActionFor(textField, new CommentListener(), false);

        profileListener.run();

        return panel;

    }

    private class ProfileListener extends FormUpdater {

        @Override
        public void updateFields() {

            /*
            // projet non initialisé, retour
            if (projectm.isInitialized() == false) {
                GuiUtils.changeText(textField, "");
                return;
            }

            // commentaire du profil
            String profileComment = configm.getConfiguration().PROFILE_COMMENT;

            GuiUtils.changeText(textField, profileComment);
            */
        }

    }

    private class CommentListener implements Runnable {

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
                configm.getConfiguration().PROFILE_COMMENT = fieldComment;
                configm.fireParametersUpdated();
            }

            */
        }

    }

}
