package abcmap.utils.notifications;

import java.util.ArrayList;
import java.util.Collection;

import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.PrintUtils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.tool.NotificationHistoryElement;
import abcmap.utils.threads.ThreadManager;

/**
 * Gestionnaire de notification "lourd". Chaque evenement est envoyé dans un
 * thread séparé et peut être sauvegardé pour analyse on mode de debuggage.
 * <p>
 * Permet egalement l'actualisation de l'objet propriétaire à la recption
 * d'évenement:<br>
 * - Soit par l'override de la méthode update,<br>
 * - Soit par l'affectation d'un objet de mise à jour par défaut
 * 
 * @author remipassmoilesel
 *
 */
public class NotificationManager implements UpdatableByNotificationManager {

	/** Nombre max d'evenements enregistrés en mode debuggage */
	public static final Integer MAX_EVENT_SAVED_DEBUG = 200;

	/** Liste des derniers evenements transmis, utilisé en debuggage */
	private static ArrayList<NotificationHistoryElement> lastTransmittedEvents;

	/** La liste des objets observant */
	protected final ArrayList<NotificationManager> observers;

	/** L'objet à lancer par defaut lors d'une notification */
	protected UpdatableByNotificationManager defaultUpdatableObject;

	/** L'objet proprietaire du gestionnaire de notifications */
	protected Object owner;

	public NotificationManager(Object owner) {
		this.owner = owner;
		this.observers = new ArrayList<NotificationManager>(10);
		this.defaultUpdatableObject = null;
	}

	/**
	 * Retoune la liste des derniers evenements transmis pour debuggage.
	 * 
	 * @return
	 */
	public static ArrayList<NotificationHistoryElement> getLastTransmittedEvents() {
		return new ArrayList<NotificationHistoryElement>(lastTransmittedEvents);
	}

	/**
	 * Enregistrer un evenement transmis pour debugage. Les evenements les plus
	 * récents sont ajouté à la fin.
	 * 
	 * @param event
	 */
	private void saveTransmittedEvent(Notification event) {

		// creation de la liste si nulle
		if (lastTransmittedEvents == null){
			lastTransmittedEvents = new ArrayList<NotificationHistoryElement>();
		}

		// enregisrement de l'evenement
		NotificationHistoryElement cehe = new NotificationHistoryElement();
		cehe.setEvent(event);
		cehe.setOwner(owner);
		cehe.setReceivers(observers);
		cehe.setObserverManager(this);
		lastTransmittedEvents.add(cehe);

		// enlever les evenements de trop
		while (lastTransmittedEvents.size() > MAX_EVENT_SAVED_DEBUG) {
			lastTransmittedEvents.remove(0);
		}

	}

	/**
	 * Notifier dans un thread - Owner spécial emprunts
	 * 
	 * @param event
	 */
	public void fireEvent(Notification event) {

		// enregistrer l'emission de l'evenement
		if (MainManager.isDebugMode()) {
			saveTransmittedEvent(event);
		}

		// notification de l'evenement dans un thread séparé
		Notifier noti = new Notifier(event);
		ThreadManager.runLater(noti);

	}

	/**
	 * Objet a notifier par defaut. Evite d'overrider update.
	 * 
	 * @param updatable
	 */
	public void setDefaultUpdatableObject(
			UpdatableByNotificationManager updatable) {

		if (defaultUpdatableObject == this) {
			throw new IllegalArgumentException(
					"Notifier cannot notify itself. This: " + this
							+ ", Object: " + updatable);
		}

		this.defaultUpdatableObject = updatable;
	}

	/**
	 * Ajouter un observateur. Si l'observateur est déjà présent, pas de
	 * deuxième ajout.
	 * 
	 * @param observer
	 */
	public void addObserver(HasNotificationManager observer) {
		if (observers.contains(observer) == false) {
			observers.add(observer.getNotificationManager());
		}
	}

	/**
	 * Ajouter des observateurs
	 * 
	 * @param observers
	 */
	public void addObservers(Collection<HasNotificationManager> observers) {
		for (HasNotificationManager o : observers) {
			addObserver(o);
		}
	}

	/**
	 * retourne le proprietaire de l'objet
	 * 
	 * @return
	 */
	public Object getOwner() {
		return owner;
	}

	/**
	 * Retirer un observateur
	 * 
	 * @param observer
	 */
	public void removeObserver(HasNotificationManager observer) {
		observers.remove(observer.getNotificationManager());
	}

	/**
	 * Retirer tous les observateurs
	 */
	public void clearObservers() {
		observers.clear();
	}

	/**
	 * Afficher textuellement tous les observateurs
	 */
	public void printObservers() {
		PrintUtils.p("%% Observers: ");
		PrintUtils.p("Observer owner: " + owner.getClass().getSimpleName()
				+ " --- " + owner);
		int i = 0;
		for (NotificationManager o : observers) {
			if (o == null) {
				PrintUtils.p(i + " : " + o);
			} else {
				PrintUtils.p(i + " : " + o.getClass().getSimpleName() + " --- "
						+ o);
			}
			i++;
		}
	}

	/**
	 * Liste des observateurs
	 * 
	 * @return
	 */
	public ArrayList<NotificationManager> getObservers() {
		return observers;
	}

	/**
	 * Action lors de la reception d'un evenement. <br>
	 * A overrider ou averti egalement par l'objet par defaut.
	 */
	@Override
	public void notificationReceived(Notification arg) {
		if (defaultUpdatableObject != null) {
			defaultUpdatableObject.notificationReceived(arg);
		}
	}

	/**
	 * Notifie les evenements dans un thread séparé.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class Notifier implements Runnable {

		private Notification event;

		public Notifier(Notification event) {
			this.event = event;
		}

		@Override
		public void run() {

			// pasde lancement dans l'EDT
			GuiUtils.throwIfOnEDT();

			// evenement null: erreur
			if (event == null) {
				throw new IllegalStateException(
						"Cannot notify null event. From: "
								+ NotificationManager.this);
			}

			// parcourir la liste des observateurs
			for (NotificationManager om : observers) {
				try {
					if (om != null) {
						om.notificationReceived(event);
					}
				} catch (Exception e) {

					/*
					 * // } catch (Throwable e1) {
					 * 
					 * Ne pas capturer Throwable ici. Throwable doit être
					 * capturé uniquement dans le ThreadManager pour pouvoir
					 * gérer les OutOfMemoryError.
					 */

					Log.error(e);
				}
			}

		}

	}

}
