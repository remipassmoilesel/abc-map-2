package org.abcmap.core.notifications;

/**
 * This interface have to be implemented by objects which want to be notified when a notification is sent by a notification manager.
 *
 * @author remipassmoilesel
 */
public interface UpdatableByNotificationManager {

    /**
     * Reception d'une notification à partir d'un objet observé
     *
     * @param arg
     */
    public void notificationReceived(Notification arg);
}
