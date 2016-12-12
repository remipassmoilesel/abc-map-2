package org.abcmap.gui.ie.draw;

import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.UpdatableByNotificationManager;
import org.abcmap.gui.components.help.ToolHelpPanel;
import org.abcmap.gui.ie.InteractionElement;

import java.awt.*;

public class ShowToolHelp extends InteractionElement {

    private ToolHelpPanel toolHelpPanel;

    public ShowToolHelp() {

        label = "Fonctionnement de l'outil actif";
        help = "Cliquez sur cet élément pour visualiser une aide rapide concernant les principales "
                + "fonctions de l'outil actif.";

        displaySimplyInSearch = false;

        displayInHideableElement = true;

    }

    @Override
    protected Component createPrimaryGUI() {

        toolHelpPanel = new ToolHelpPanel();
        toolHelpPanel.setMessageNoHelp("<html><i>Aide sur l'outil indisponible.</i></html>");

        InteractionUpdater interactionUpdater = new InteractionUpdater();
        notifm.setDefaultUpdatableObject(interactionUpdater);

        drawm.getNotificationManager().addObserver(this);

        interactionUpdater.run();

        return toolHelpPanel;
    }

    public class InteractionUpdater implements Runnable, UpdatableByNotificationManager {

        @Override
        public void run() {

            /*
            if (toolHelpPanel == null) {
                return;
            }

            //ToolContainer currentTC = MainManager.getDrawManager().getCurrentToolContainer();

            toolHelpPanel.constructWith(currentTC);
            */

        }

        /**
         * Reception d'un evenement
         */
        @Override
        public void notificationReceived(Notification arg) {
            /*
            if (DrawManagerEvent.TOOL_CHANGED.equals(arg.getName())) {
                SwingUtilities.invokeLater(this);
            }
            */
        }

    }

}