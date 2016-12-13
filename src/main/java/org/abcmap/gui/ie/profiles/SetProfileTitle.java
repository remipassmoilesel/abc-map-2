package org.abcmap.gui.ie.profiles;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.ie.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;

public class SetProfileTitle extends InteractionElement {

    private ProfileListener profileListener;
    private JTextField textField;

    public SetProfileTitle() {
        label = "Titre du profil de configuration";
        help = "...";

    }

    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        textField = new JTextField();
        panel.add(textField, "width 80%, grow");

        profileListener = new ProfileListener();

        notifm.setDefaultListener(profileListener);
        configm.getNotificationManager().addObserver(this);

        //TextFieldDelayedAction.delayedActionFor(textField, new TextFieldListener(), false);

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

            // titre du profil
            String profileTitle = configm.getConfiguration().PROFILE_TITLE;

            GuiUtils.changeText(textField, profileTitle);

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

            // titre du profil
            String profileTitle = configm.getConfiguration().PROFILE_TITLE;

            // titre du champs de texte
            String fieldTitle = textField.getText();

            if (Utils.safeEquals(profileTitle, fieldTitle) == false) {
                configm.getConfiguration().PROFILE_TITLE = fieldTitle;
                configm.fireParametersUpdated();
            }

            */

        }

    }

}
