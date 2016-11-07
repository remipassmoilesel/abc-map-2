package abcmap.utils.lists;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import abcmap.managers.Log;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

/**
 * Conteneur générique emettant des notifications lors de changements.
 * <p>
 * Attention: la fonction iterator renvoi un iterator sur une copie, ce qui
 * permet de modifier la liste d'origine lors d'une itération sans craindre de
 * ConcurrentExceptions
 * 
 * @author remipassmoilesel
 *
 * @param <E>
 */
public class ListenableContainer<E> implements HasNotificationManager,
		Iterable<E> {

	private static final String LISTENABLE_CONTAINER_CHANGED = "LISTENABLE_CONTAINER_CHANGED";

	private NotificationManager om;
	private ArrayList<E> list;
	private Class<? extends Notification> eventClass;
	private boolean notificationsEnabled;
	private String eventName;

	public ListenableContainer() {
		this.om = new NotificationManager(this);
		this.list = new ArrayList<E>();
		this.eventClass = Notification.class;
		this.eventName = LISTENABLE_CONTAINER_CHANGED;
		this.notificationsEnabled = true;
	}

	public void setNotificationManager(NotificationManager om) {
		this.om = om;
	}

	public E get(int index) {
		return list.get(index);
	}

	public int indexOf(E elmt) {
		return list.indexOf(elmt);
	}

	public void add(E elmt) {
		list.add(elmt);
		fireListChanged();
	}

	public void add(E elmt, Integer index) {
		list.add(index, elmt);
		fireListChanged();
	}

	public void addAll(List<E> elmts) {
		list.addAll(elmts);
		fireListChanged();
	}

	public void remove(E elmt) {
		list.remove(elmt);
		fireListChanged();
	}

	public void remove(int i) {
		list.remove(i);
		fireListChanged();
	}

	public void clear() {
		list.clear();
		fireListChanged();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return om;
	}

	/**
	 * Attention: Renvoi un iterator sur une copie, ce qui permet de modifier
	 * directement la liste originale sans craindre de ConcurrentException
	 */
	@Override
	public Iterator<E> iterator() {
		return getCopy().iterator();
	}

	public ArrayList<E> getCopy() {
		return new ArrayList<E>(list);
	}

	/**
	 * Affecter le type d'évenement à lancer lors d'une modification de la
	 * liste.
	 * 
	 * @param eventClass
	 */
	public void setEventClass(Class<? extends Notification> eventClass) {
		this.eventClass = eventClass;
	}

	/**
	 * Affecter le nom de l'evenement à lancer lors de la modification de la
	 * liste
	 * 
	 * @param eventName
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	/**
	 * Créer une instance d'evenement à lancer.
	 * 
	 * @param name
	 * @return
	 */
	private Notification getEventInstance(String name) {
		Constructor<? extends Notification> ev;
		try {
			ev = eventClass.getConstructor(String.class, Object.class);
			return ev.newInstance(name, this);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Log.error(e);
			throw new IllegalStateException();
		}
	}

	/**
	 * Activer ou désactiver le lancement d'evenement lors d'une modification
	 * 
	 * @param notificationsEnabled
	 */
	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}

	public void fireListChanged() {
		if (notificationsEnabled) {
			Notification ev = getEventInstance(eventName);
			om.fireEvent(ev);
		}
	}

	public void add(int i, E elmt) {
		list.add(i, elmt);
		fireListChanged();
	}

	public void addAll(Collection<E> collection) {
		list.addAll(collection);
		fireListChanged();
	}

	public boolean contains(E o) {
		return list.contains(o);
	}

	public Integer size() {
		return list.size();
	}

	/**
	 * Deplace l'element dans la liste à l'index indiqué.
	 * <p>
	 * Attention: deplace le premier element correspondant à l'element fourni
	 * 
	 * @param elmt
	 * @param newIndex
	 */
	public void moveElement(E elmt, int newIndex) {
		list.remove(elmt);
		list.add(newIndex, elmt);
	}

}
