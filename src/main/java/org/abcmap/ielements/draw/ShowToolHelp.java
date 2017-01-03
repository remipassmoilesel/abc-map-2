package org.abcmap.ielements.draw;

import org.abcmap.core.events.manager.*;
import org.abcmap.gui.components.help.ToolHelpPanel;
import org.abcmap.ielements.InteractionElement;

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
        notifm.setDefaultListener(interactionUpdater);

        drawm().getNotificationManager().addObserver(this);

        interactionUpdater.run();

        return toolHelpPanel;
    }

    public class InteractionUpdater implements Runnable, EventListener {

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
        public void notificationReceived(org.abcmap.core.events.manager.Event arg) {
            /*
            if (DrawManagerEvent.TOOL_CHANGED.equals(arg.getName())) {
                SwingUtilities.invokeLater(this);
            }
            */
        }

    }

}
