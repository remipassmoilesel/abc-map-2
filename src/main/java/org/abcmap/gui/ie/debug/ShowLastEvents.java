package org.abcmap.gui.ie.debug;

import org.abcmap.core.notifications.monitoringtool.NotificationManagerTool;
import org.abcmap.gui.ie.InteractionElement;
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
