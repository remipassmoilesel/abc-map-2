package org.abcmap.core.notifications;

/**
 * Object having notification manager have to implement this interface
 */
public interface HasNotificationManager {

    /**
     * Get the notification manager associated with this object
     *
     * @return
     */
    public NotificationManager getNotificationManager();
}
