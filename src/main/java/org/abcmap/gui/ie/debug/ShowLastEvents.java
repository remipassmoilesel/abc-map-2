package org.abcmap.gui.ie.debug;

import org.abcmap.gui.ie.InteractionElement;

public class ShowLastEvents extends InteractionElement {

    public ShowLastEvents() {
        label = "Montrer les derniers événements transmis...";
        help = "Cliquez ici pour afficher une fenêtre de suivi des événements internes au programme.";

        this.onlyDebugMode = true;
    }

    @Override
    public void run() {

		/*
        // pas d'envoi dans l'EDT
		GuiUtils.throwIfOnEDT();

		NotificationManagerTool.showLastEventsTransmitted();
		*/
    }

}
