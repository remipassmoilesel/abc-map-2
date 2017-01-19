package org.abcmap.core.events.manager;

import org.abcmap.core.events.monitoringtool.NotificationHistoryElement;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.PrintUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper to send and receive notifications. Typically this utility is used for managers which have lot of observers.
 * <p>
 * With this utility, an observer can receive several types of notifications.
 * Also, notifications are sent on different threads to support many observers and avoid long blocking sequences
 * <p>
 * In order to listen notifications you can:
 * - set default updatable object
 * - or override update method
 *
 * @author remipassmoilesel
 */
public class EventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(EventNotificationManager.class);

    /**
     * Maximum events saved in history
     */
    public static final Integer MAX_EVENT_SAVED_ON_DEBUG = 200;

    /**
     * Event history
     */
    private static ArrayList<NotificationHistoryElement> lastTransmittedEvents;

    /**
     * Liste of object which observe this
     */
    protected final ArrayList<EventNotificationManager> observers;

    /**
     * List of listeners called when an event is received
     */
    protected ArrayList<EventListener> eventListeners;

    /**
     * The owner of this notification manager
     * <p>
     * Owner can be a HasNotificationManager object or not, and can be null.
     */
    protected Object owner;

    /**
     * If set to true, events transmitted will be stored
     */
    private static boolean debugMode = false;

    public EventNotificationManager(Object owner) {

        if (owner == null) {
            throw new NullPointerException("Owner is null");
        }

        this.owner = owner;
        this.observers = new ArrayList<>(10);
        this.eventListeners = new ArrayList<>();
    }

    /**
     * Notify observers in a separated thread
     *
     * @param notif
     */
    public void fireEvent(Event notif) {

        // save notification if needed
        if (debugMode) {
            saveTransmittedEvent(notif);
        }

        // notify event in a separated thread
        EventReceivedNotifier noti = new EventReceivedNotifier(observers, notif);
        ThreadManager.runLater(noti);

    }

    /**
     * Add an event listener
     * <p>
     * Each time an event will be received, this listener will be run.
     *
     * @param listener
     */
    public void addEventListener(EventListener listener) {

        if (listener == this) {
            throw new IllegalArgumentException("Notifier cannot notify itself. This: " + this + ", Object: " + listener);
        }

        if (listener == null) {
            throw new NullPointerException("Listener is null");
        }

        this.eventListeners.add(listener);
    }

    /**
     * Add a notification manager watching this manager
     *
     * @param observer
     */
    public void addObserver(HasEventNotificationManager observer) {
        addObserverIfNecessary(observer.getNotificationManager());
    }

    /**
     * Allow to register a simple observer even if it
     * <p>
     * doesn't implements HasNotificationManager interface.
     *
     * @param owner
     * @param listener
     */
    public EventNotificationManager addObserver(Object owner, EventListener listener) {

        // check parameters
        if (owner == null) {
            throw new NullPointerException("Owner cannot be null: " + listener);
        }
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null: " + listener);
        }

        // create a notification manager
        EventNotificationManager notifm = new EventNotificationManager(owner);
        notifm.addEventListener(listener);

        // and observe it
        addObserverIfNecessary(notifm);

        return notifm;
    }

    /**
     * Prevent double event firing by checking if observer is already here before
     *
     * @param notifm
     */
    private void addObserverIfNecessary(EventNotificationManager notifm) {

        if (notifm == null) {
            throw new NullPointerException("Observer is null");
        }

        if (observers.contains(notifm) == false) {
            observers.add(notifm);
        }
    }

    /**
     * Add observers list. All of these objects will be notified if this notification
     * <p>
     * manager send an event
     *
     * @param observers
     */
    public void addObservers(Collection<HasEventNotificationManager> observers) {
        for (HasEventNotificationManager o : new ArrayList<>(observers)) {
            addObserver(o);
        }
    }

    /**
     * Get the owner of this notification manager
     * <p>
     * Owner can implements HasNotificationManager interface or not.
     *
     * @return
     */
    public Object getOwner() {
        return owner;
    }

    /**
     * Remove an observer
     *
     * @param observer
     */
    public void removeObserver(HasEventNotificationManager observer) {
        observers.remove(observer.getNotificationManager());
    }

    /**
     * Remove all observers
     */
    public void clearObservers() {
        observers.clear();
    }

    /**
     * Get all observer list.
     * <p>
     * /!\ Live list
     *
     * @return
     */
    public ArrayList<EventNotificationManager> getObservers() {
        return observers;
    }

    /**
     * Method called when an notification is received.
     * <p>
     * You can override it to execute actions
     */
    protected void eventReceived(Event arg) {

    }

    /**
     * Notify listeners by calling notificationReceived(arg); method
     *
     * @param arg
     */
    protected void notifyListeners(Event arg) {

        for (EventListener listener : eventListeners) {
            try {
                listener.eventReceived(arg);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


    /**
     * Set debug mode for all notification managers
     * <p>
     * Debug mode enable event history
     *
     * @param val
     */
    public static void setDebugMode(boolean val) {
        debugMode = val;
    }

    /**
     * Return true if debug mode is enabled
     *
     * @return
     */
    public static boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Get list of last transmitted events, for debug purposes
     *
     * @return
     */
    public static ArrayList<NotificationHistoryElement> getLastTransmittedEvents() {

        if (debugMode == false) {
            throw new IllegalStateException("Debug mode is disabled, no events are recorded");
        }

        return new ArrayList<>(lastTransmittedEvents);
    }

    /**
     * Save an event in last transmitted list, for debug purposes
     *
     * @param notif
     */
    private void saveTransmittedEvent(Event notif) {

        // create list if needed
        if (lastTransmittedEvents == null) {
            lastTransmittedEvents = new ArrayList<>();
        }

        // create notif history element
        NotificationHistoryElement cehe = new NotificationHistoryElement();
        cehe.setEvent(notif);
        cehe.setOwner(owner);
        cehe.setReceivers(observers);
        cehe.setObserverManager(this);
        lastTransmittedEvents.add(cehe);

        // remove last elements
        while (lastTransmittedEvents.size() > MAX_EVENT_SAVED_ON_DEBUG) {
            lastTransmittedEvents.remove(0);
        }

    }

    /**
     * Print observers in console, for debug purposes
     */
    public void printObservers() {
        PrintUtils.p("%% Observers: ");
        PrintUtils.p("Observer owner: " + owner.getClass().getSimpleName() + " --- " + owner);
        int i = 0;
        for (EventNotificationManager o : new ArrayList<>(observers)) {
            if (o == null) {
                PrintUtils.p(i + " : " + o);
            } else {
                PrintUtils.p(i + " : " + o.getClass().getSimpleName() + " --- " + o);
            }
            i++;
        }
    }
}
