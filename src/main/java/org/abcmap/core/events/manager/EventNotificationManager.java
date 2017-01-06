package org.abcmap.core.events.manager;

import org.abcmap.core.events.monitoringtool.NotificationHistoryElement;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.PrintUtils;
import org.abcmap.gui.utils.GuiUtils;

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
public class EventNotificationManager implements EventListener {

    private static final CustomLogger logger = LogManager.getLogger(EventNotificationManager.class);

    /**
     * Maximum events saved in history
     */
    public static final Integer MAX_EVENT_SAVED_DEBUG = 200;

    /**
     * Event history
     */
    private static ArrayList<NotificationHistoryElement> lastTransmittedEvents;

    /**
     * Liste of object which observe this
     */
    protected final ArrayList<EventNotificationManager> observers;

    /**
     * Default object to update
     */
    protected EventListener defaultListener;

    /**
     * The owner of this notification manager
     * <p>
     * Owner can be a HasNotificationManager object or not, and can be null.
     */
    protected Object owner;

    private static boolean debugMode = false;

    public EventNotificationManager(Object owner) {

        if (owner == null) {
            throw new NullPointerException("Owner is null");
        }

        this.owner = owner;
        this.observers = new ArrayList<>(10);
        this.defaultListener = null;
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
        Notifier noti = new Notifier(notif);
        ThreadManager.runLater(noti);

    }

    /**
     * Default updatable object.
     *
     * @param listener
     */
    public void setDefaultListener(EventListener listener) {

        if (defaultListener == this) {
            throw new IllegalArgumentException("Notifier cannot notify itself. This: " + this + ", Object: " + listener);
        }

        this.defaultListener = listener;
    }

    /**
     * Add an observer watching this object
     *
     * @param observer
     */
    public void addObserver(HasEventNotificationManager observer) {
        addObserverIfNecessary(observer.getNotificationManager());
    }

    /**
     * Allow to register a simple listener without implementing interface HasNotificationManager
     *
     * @param owner
     * @param listener
     */
    public EventNotificationManager addSimpleListener(Object owner, EventListener listener) {

        if (listener == null) {
            throw new NullPointerException("Listener is null: " + listener);
        }

        EventNotificationManager notifm = new EventNotificationManager(owner);
        notifm.setDefaultListener(listener);

        addObserverIfNecessary(notifm);

        return notifm;
    }

    /**
     * Prevent double event firing by checking if observer is already here before
     *
     * @param notifm
     */
    private void addObserverIfNecessary(EventNotificationManager notifm) {
        if (observers.contains(notifm) == false) {
            observers.add(notifm);
        }
    }

    /**
     * Add observer list
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
     * Owner can be a HasNotificationManager object or not, and can be null.
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
     * Set default updatable object or override this.
     */
    @Override
    public void notificationReceived(Event arg) {
        if (defaultListener != null) {
            defaultListener.notificationReceived(arg);
        }
    }

    /**
     * Notify observers in a separated Thread
     *
     * @author remipassmoilesel
     */
    private class Notifier implements Runnable {

        private Event event;

        public Notifier(Event event) {
            this.event = event;
        }

        @Override
        public void run() {

            // check we are not in EDT
            GuiUtils.throwIfOnEDT();

            // Check notification is not null or throw
            if (event == null) {
                throw new IllegalStateException("Event is null: " + EventNotificationManager.this);
            }

            // iterate all observers
            for (EventNotificationManager om : new ArrayList<>(observers)) {
                try {
                    if (om != null) {
                        om.notificationReceived(event);
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
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
        cehe.setNotification(notif);
        cehe.setOwner(owner);
        cehe.setReceivers(observers);
        cehe.setObserverManager(this);
        lastTransmittedEvents.add(cehe);

        // remove last elements
        while (lastTransmittedEvents.size() > MAX_EVENT_SAVED_DEBUG) {
            lastTransmittedEvents.remove(0);
        }

    }

}
