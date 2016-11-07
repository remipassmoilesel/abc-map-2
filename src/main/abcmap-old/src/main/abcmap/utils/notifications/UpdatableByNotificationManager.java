package abcmap.utils.notifications;

/**
 * Action réalisée par l'ObserverManager en cas de réception d'évenement
 * 
 * @author remipassmoilesel
 *
 */
public interface UpdatableByNotificationManager {

	/**
	 * Reception d'une notification à partir d'un objet observé
	 * 
	 * @param arg
	 */
	public void notificationReceived(Notification arg);
}
