package abcmap.utils.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import abcmap.clipboard.ClipboardListener;
import abcmap.events.ClipboardEvent;
import abcmap.events.ImportEvent;
import abcmap.events.ErrorEvent;
import abcmap.gui.comps.color.ColorEvent;
import abcmap.gui.comps.color.ColorEventListener;
import abcmap.importation.tile.ImportEventListener;
import abcmap.managers.Log;
import abcmap.utils.ErrorListener;

/**
 * Gestionnaire de notification "léger". Permet l'envoi de notifications.
 * 
 * @author remipassmoilesel
 *
 * @param <T>
 */
public class ListenerHandler<T> implements Iterable<T> {

	private ArrayList<T> listeners = new ArrayList<T>();

	public void fireEvent(Object e) {
		for (T listener : listeners) {
			try {
				fire(listener, e);
			} catch (Exception e1) {
				/*
				 * // } catch (Throwable e1) {
				 * 
				 * Ne pas capturer Throwable ici. Throwable doit être capturé
				 * uniquement dans le ThreadManager pour pouvoir gérer les
				 * OutOfMemoryError.
				 */
				Log.error(e1);
			}
		}
	}

	private void fire(T listener, Object e) {

		if (e instanceof ColorEvent && listener instanceof ColorEventListener) {
			((ColorEventListener) listener).colorChanged((ColorEvent) e);
		}

		else if (e instanceof ImportEvent
				&& listener instanceof ImportEventListener) {
			((ImportEventListener) listener)
					.importEventHapened((ImportEvent) e);
		}

		else if (e instanceof ActionEvent && listener instanceof ActionListener) {
			((ActionListener) listener).actionPerformed((ActionEvent) e);
		}

		else if (e instanceof ClipboardEvent
				&& listener instanceof ClipboardListener) {
			((ClipboardListener) listener).clipboardChanged((ClipboardEvent) e);
		}

		else if (e instanceof ErrorEvent && listener instanceof ErrorListener) {
			((ErrorListener) listener).errorHapened((ErrorEvent) e);
		}

		else {
			throw new IllegalArgumentException("Listener '"
					+ listener.getClass().getName() + "' and event '"
					+ e.getClass().getName() + "' unsupported");
		}

	}

	public void add(T listener) {
		listeners.add(listener);
	}

	public void remove(T listener) {
		listeners.remove(listener);
	}

	public void removeAll() {
		listeners.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return listeners.iterator();
	}

}
