package org.abcmap.core.events.monitoringtool;

import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventNotificationManager;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Action of notification saved in history for debug purposes.
 * <p>
 * Save events and concerned observers
 */
public class NotificationHistoryElement {

    /**
     * Notification manager which have propagate event
     */
    private EventNotificationManager om;

    /**
     * Owner of notification manager which have propagate event
     */
    private Object owner;

    /**
     * Event to propagate
     */
    private Event ev;

    /**
     * List of receivers
     */
    private ArrayList<EventNotificationManager> receivers;

    public NotificationHistoryElement() {
        this.om = null;
        this.ev = null;
        this.owner = null;
        this.receivers = new ArrayList<>();
    }

    /**
     * Add
     *
     * @param observers
     */
    public void setReceivers(ArrayList<EventNotificationManager> observers) {
        receivers.addAll(observers);
    }

    /**
     * Get source of event
     *
     * @return
     */
    public EventNotificationManager getObserverManager() {
        return om;
    }

    /**
     * Set source of event
     *
     * @param om
     */
    public void setObserverManager(EventNotificationManager om) {
        this.om = om;
    }

    /**
     * Get event propagated
     *
     * @return
     */
    public Event getEvent() {
        return ev;
    }

    /**
     * Set event propagated
     *
     * @return
     */
    public void setEvent(Event ev) {
        this.ev = ev;
    }

    /**
     * Get owner of notification manager which have propagated event
     *
     * @return
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * Get owner of notification manager which have propagated event
     *
     * @return
     */
    public void setOwner(Object owner) {
        this.owner = owner;
    }

    /**
     * Create a visual panel from this element
     *
     * @return
     */
    public JPanel getPanel() {
        return new NotificationHistoryPanel(this);
    }

    /**
     * Get list of receivers
     *
     * @return
     */
    public ArrayList<EventNotificationManager> getObservers() {
        return receivers;
    }

}
