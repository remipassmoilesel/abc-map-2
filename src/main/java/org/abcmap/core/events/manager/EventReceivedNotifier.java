package org.abcmap.core.events.manager;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.utils.GuiUtils;

import java.util.ArrayList;

/**
 * Notify observers in a separated Thread
 *
 * @author remipassmoilesel
 */
class EventReceivedNotifier implements Runnable {

    private static final CustomLogger logger = LogManager.getLogger(EventNotificationManager.class);

    /**
     * Event to notify
     */
    private final Event event;

    /**
     * List of observers to notify
     */
    private final ArrayList<EventNotificationManager> observers;

    public EventReceivedNotifier(ArrayList<EventNotificationManager> observers, Event event) {

        // Check notification is not null or throw
        if (event == null) {
            throw new IllegalStateException("Event is null");
        }

        this.observers = observers;
        this.event = event;
    }

    @Override
    public void run() {

        // check we are not in EDT
        GuiUtils.throwIfOnEDT();

        // iterate all observers
        for (EventNotificationManager om : new ArrayList<>(observers)) {
            try {
                if (om != null) {
                    om.notifyListeners(event);
                    om.eventReceived(event);
                } else {
                    logger.error("Observer is null");
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }

    }

}