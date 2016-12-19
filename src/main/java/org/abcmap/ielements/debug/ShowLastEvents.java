package org.abcmap.ielements.debug;

import org.abcmap.core.events.monitoringtool.NotificationManagerTool;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.GuiUtils;

public class ShowLastEvents extends InteractionElement {

    public ShowLastEvents() {
        label = "Montrer les derniers événements transmis...";
        help = "Cliquez ici pour afficher une fenêtre de suivi des événements internes au programme.";

        this.onlyDebugMode = true;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        NotificationManagerTool.showLastEventsTransmitted();

    }

}
