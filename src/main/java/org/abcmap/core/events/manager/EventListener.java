package org.abcmap.core.events.manager;

/**
 * This interface have to be implemented by objects which want to be notified when a notification is sent by a notification manager.
 *
 * @author remipassmoilesel
 */
public interface EventListener {

    /**
     * Event come from Observed object
     *
     * @param arg
     */
    public void notificationReceived(Event arg);
}
