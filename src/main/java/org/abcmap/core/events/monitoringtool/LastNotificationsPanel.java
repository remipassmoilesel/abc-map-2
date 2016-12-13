package org.abcmap.core.events.monitoringtool;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

/**
 * Panel where are displayed last transmitted events
 *
 * @author remipassmoilesel
 */
public class LastNotificationsPanel extends JPanel {

    public LastNotificationsPanel() {
        super(new MigLayout());
    }

    public void refresh() {

        GuiUtils.throwIfNotOnEDT();

        // update all panel
        removeAll();

        List<NotificationHistoryElement> lastEvents = EventNotificationManager.getLastTransmittedEvents();
        Collections.reverse(lastEvents);

        for (NotificationHistoryElement cehe : lastEvents) {
            add(cehe.getPanel(), "width 98%!, wrap");
        }

        revalidate();
        repaint();
    }


}
