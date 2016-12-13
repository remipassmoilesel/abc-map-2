package org.abcmap.core.events.manager;

/**
 * Object having notification manager have to implement this interface
 */
public interface HasEventNotificationManager {

    /**
     * Get the notification manager associated with this object
     *
     * @return
     */
    public EventNotificationManager getNotificationManager();
}
