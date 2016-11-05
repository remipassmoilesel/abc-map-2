package org.abcmap.core.notifications;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.notifications.monitoringtool.NotificationHistoryElement;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.PrintUtils;
import org.abcmap.gui.utils.GuiUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Helper to send and receive notifications.
 * <p>
 * In order to listen notifications you can:
 * - set default updatable object
 * - or override update method
 *
 * @author remipassmoilesel
 */
public class NotificationManager implements UpdatableByNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(NotificationManager.class);

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
    protected final ArrayList<NotificationManager> observers;

    /**
     * Default object to update
     */
    protected UpdatableByNotificationManager defaultUpdatableObject;

    /**
     * The owner of this notification manager
     */
    protected Object owner;

    public NotificationManager(Object owner) {
        this.owner = owner;
        this.observers = new ArrayList<NotificationManager>(10);
        this.defaultUpdatableObject = null;
    }

    public static ArrayList<NotificationHistoryElement> getLastTransmittedEvents() {
        return new ArrayList<NotificationHistoryElement>(lastTransmittedEvents);
    }

    private void saveTransmittedEvent(Notification notif) {

        // create list if needed
        if (lastTransmittedEvents == null) {
            lastTransmittedEvents = new ArrayList<NotificationHistoryElement>();
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

    /**
     * Notify observers in a different thread
     *
     * @param event
     */
    public void fireEvent(Notification event) {

        // save notification if needed
        if (MainManager.isDebugMode()) {
            saveTransmittedEvent(event);
        }

        // notify event in a separated thread
        Notifier noti = new Notifier(event);
        ThreadManager.runLater(noti);

    }

    /**
     * Default updatable object.
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
     * Add an observer watching this object
     *
     * @param observer
     */
    public void addObserver(HasNotificationManager observer) {
        if (observers.contains(observer) == false) {
            observers.add(observer.getNotificationManager());
        }
    }

    /**
     * Add observer list
     *
     * @param observers
     */
    public void addObservers(Collection<HasNotificationManager> observers) {
        for (HasNotificationManager o : observers) {
            addObserver(o);
        }
    }

    /**
     * Get the owner of this notification manager
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
    public void removeObserver(HasNotificationManager observer) {
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
     * Get all observer list.
     * <p>
     * /!\ Live list
     *
     * @return
     */
    public ArrayList<NotificationManager> getObservers() {
        return observers;
    }

    /**
     * Method called when an notification is received.
     * <p>
     * Set default updatable object or override this.
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
     */
    private class Notifier implements Runnable {

        private Notification event;

        public Notifier(Notification event) {
            this.event = event;
        }

        @Override
        public void run() {

            // chec we are not in EDT
            GuiUtils.throwIfOnEDT();

            // Check notification is not null or throw
            if (event == null) {
                throw new IllegalStateException("Event is null: " + NotificationManager.this);
            }

            // iterate all observers
            for (NotificationManager om : observers) {
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

}
