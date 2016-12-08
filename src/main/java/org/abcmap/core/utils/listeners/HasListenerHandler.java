package org.abcmap.core.utils.listeners;

/**
 * This interface give access to listener handler, a utility that allow to handle multiple observers.
 *
 * @param <E>
 */
public interface HasListenerHandler<E> {
    public ListenerHandler<E> getListenerHandler();
}
