package org.abcmap.core.utils.listeners;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.gui.components.color.ColorEvent;
import org.abcmap.gui.components.color.ColorEventListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Light utility to handle observers.
 *
 * @param <T>
 * @author remipassmoilesel
 */
public class ListenerHandler<T> implements Iterable<T> {

    private static final CustomLogger logger = LogManager.getLogger(ListenerHandler.class);

    /**
     * List of all listeners
     */
    private ArrayList<T> listeners = new ArrayList<T>();

    /**
     * Fire an event on this thread
     *
     * @param e
     */
    public void fireEvent(Object e) {
        for (T listener : listeners) {
            try {
                fire(listener, e);
            } catch (Exception e1) {
                logger.error(e1);
            }
        }
    }

    private void fire(T listener, Object e) {

        // color events
        if (e instanceof ColorEvent && listener instanceof ColorEventListener) {
            ((ColorEventListener) listener).colorChanged((ColorEvent) e);
        }

//        // import events
//        else if (e instanceof ImportEvent && listener instanceof ImportEventListener) {
//            ((ImportEventListener) listener).importEventHapened((ImportEvent) e);
//        }

        // action events
        else if (e instanceof ActionEvent && listener instanceof ActionListener) {
            ((ActionListener) listener).actionPerformed((ActionEvent) e);
        }

//        // clipboard events
//        else if (e instanceof ClipboardEvent && listener instanceof ClipboardListener) {
//            ((ClipboardListener) listener).clipboardChanged((ClipboardEvent) e);
//        }
//
//        // error events
//        else if (e instanceof ErrorEvent && listener instanceof ErrorListener) {
//            ((ErrorListener) listener).errorHapened((ErrorEvent) e);
//        }

        // not recognized
        else {
            throw new IllegalArgumentException("Listener '" + listener.getClass().getName() + "' and event '" + e.getClass().getName() + "' unsupported");
        }

    }

    /**
     * Add a listener to this handler
     *
     * @param listener
     */
    public void add(T listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from this handler
     *
     * @param listener
     */
    public void remove(T listener) {
        listeners.remove(listener);
    }

    /**
     * Clear all listeners of this handler
     */
    public void removeAll() {
        listeners.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return listeners.iterator();
    }

}
