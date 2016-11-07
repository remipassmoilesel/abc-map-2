package abcmap.cancel.memento;

import java.util.ArrayList;

import abcmap.managers.Log;

/**
 * Objet de sauvegarde et de restautation d'état. Les méthode saveStateToRedo()
 * et saveStateToRestore() doivent être appelées pour sauvegarder les différents
 * états.
 * 
 * @author remipassmoilesel
 *
 * @param <E>
 */
public class MementoManager<E> {

	/** L'index courant de l'état */
	private int index = -1;

	/** L'historique des états successifs */
	private ArrayList<HistoryElement<E>> history;

	public MementoManager() {
		this.history = new ArrayList<HistoryElement<E>>();
	}

	/**
	 * Avant une action, sauvegarder <b>l'etat de depart</b> pour annuler au
	 * besoin
	 */
	public void saveStateToRestore() {

		// supprimer les elements d'historique qui deviendront obsolètes
		if (index > -1) {
			cutHistory();
		}

		// sauvegarder l'etat de l'objet
		E st = null;
		try {
			st = saveState();
		} catch (Exception e) {
			Log.error(e);
			return;
		}

		// puis le conserver
		history.add(new HistoryElement<E>(st, null));
		index++;

	}

	/**
	 * Apres une action, sauvegarder <b>l'etat d'arrivee</b> pour refaire au
	 * besoin
	 */
	public void saveStateToRedo() {

		// supprimer les elements d'historique qui deviendront obsolètes
		if (index > -1) {
			cutHistory();
		}

		// sauvegarder l'etat de l'objet
		E st = null;
		try {
			st = saveState();
		} catch (Exception e) {
			Log.error(e);
			return;
		}

		HistoryElement<E> lastElement = null;
		try {
			lastElement = history.get(index);
		} catch (IndexOutOfBoundsException e) {
			Log.error(e);
			return;
		}

		// puis le conserver
		if (lastElement != null && lastElement.toRedo == null) {
			lastElement.toRedo = st;
		}

		else {
			Log.error(new Exception("Invalid cancel affectation"));
		}
	}

	/**
	 * Tronquage de l'historique si necessaire
	 */
	private void cutHistory() {
		for (int i = index + 1; i < history.size(); i++) {
			try {
				history.remove(i);
			} catch (IndexOutOfBoundsException e) {
				Log.error(e);
			}
		}
	}

	/**
	 * Annuler
	 */
	public void restore() {

		HistoryElement<E> h = history.get(index);
		if (h.restored < 1) {
			h.restored++;
			h.redone = 0;
		}

		else if (h.restored >= 1) {
			h.restored = 0;
			h.redone = 0;
			index--;

			h = history.get(index);
			h.restored++;
		}

		// application des changements
		if (index > -1 && index < history.size()) {
			try {
				setState(h.toRestore);
			} catch (Exception e) {
				Log.error(e);
			}
		}
	}

	/**
	 * Refaire
	 */
	public void redo() {

		HistoryElement<E> h = history.get(index);
		if (h.redone < 1) {
			h.redone++;
			h.restored = 0;
		}

		else if (h.redone >= 1) {
			h.redone = 0;
			h.restored = 0;
			index++;

			h = history.get(index);
			h.redone++;
		}

		// application des changements
		if (index > -1 && index < history.size()) {
			try {
				setState(h.toRedo);
			} catch (Exception e) {
				Log.error(e);
			}
		}

	}

	/**
	 * Methode a overrider. Sauvegarde d'un objet.
	 */
	protected E saveState() {
		throw new IllegalStateException("This method must be overrided");
	}

	/**
	 * Methode a overrider. Application des changements.
	 */
	protected void setState(E st) {
		throw new IllegalStateException("This method must be overrided");
	}
}
