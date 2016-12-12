package org.abcmap.gui.ie.display.windowmode;

import org.abcmap.core.events.GuiManagerEvent;
import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.UpdatableByNotificationManager;
import org.abcmap.gui.components.buttons.DisplayModeSelector;
import org.abcmap.gui.ie.InteractionElement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WindowModeSelector extends InteractionElement {

    private DisplayModeSelector selector;

    public WindowModeSelector() {

        label = "Sélection du mode d'affichage";
        help = "Sélectionnez ici le mode d'affichage de la fenêtre.";

    }

    @Override
    public Component createPrimaryGUI() {

        selector = new DisplayModeSelector();

        selector.addActionListener(new ComboWindowModeListener());

        notifm.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
            @Override
            public void notificationReceived(Notification arg) {
                if (GuiManagerEvent.isWindowModeNotification(arg)) {
                    selector.setSelectedItem(guim.getMainWindow().getWindowMode());
                }
            }
        });

        guim.getNotificationManager().addObserver(this);

        return selector;
    }

    /**
     * Changer le mode de fenetre en fonction de la saisie de l'utilisateur
     *
     * @author remipassmoilesel
     */
    private class ComboWindowModeListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            /*
            // récupérer le mode sélectionné
            JComboBox<MainWindowMode> src = (JComboBox<MainWindowMode>) e
                    .getSource();
            MainWindowMode selectedMode = (abcmap.gui.windows.MainWindowMode) src
                    .getSelectedItem();

            if (Utils.safeEquals(guim.getMainWindowMode(), selectedMode) == false) {
                guim.setMainWindowMode(selectedMode);
            }
            */

        }

    }

}
