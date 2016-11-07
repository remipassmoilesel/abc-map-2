package abcmap.importation;

import abcmap.events.ImportEvent;
import abcmap.importation.tile.ImportEventListener;
import abcmap.managers.Log;

/**
 * Classe utilitaire d'écoute d'une usine de tuile.
 * 
 * @author remipassmoilesel
 *
 */
public abstract class ImporterAdapter implements ImportEventListener {

	@Override
	public void importEventHapened(ImportEvent event) {

		if (ImportEvent.EXCEPTION_HAPPENED.equals(event.getName())) {
			exceptionHappened(event);
		} 
		
		else if (ImportEvent.FATAL_EXCEPTION_HAPPEND.equals(event.getName())) {
			fatalExceptionHappened(event);
		}

		else if (ImportEvent.IMPORT_ABORTED.equals(event.getName())) {
			importAborted(event);
		}

		else if (ImportEvent.IMPORT_FINISHED.equals(event.getName())) {
			importFinished(event);
		}

		else if (ImportEvent.IMPORT_STARTED.equals(event.getName())) {
			importStarted(event);
		}

		else if (ImportEvent.TILE_REFUSED.equals(event.getName())) {
			tileRefused(event);
		}

		else if (ImportEvent.WAITING_LIST_CHANGED.equals(event.getName())) {
			waitingListChanged(event);
		}

		else {
			Log.debug(new Exception("Unknown event: " + event));
		}

	}

	/**
	 * L'import vient de commencer.
	 * <p>
	 * L'import peut commencer plusieurs fois à partir du même objet TileMaker.
	 * 
	 * @param event
	 */
	public void importStarted(ImportEvent event) {

	}

	/**
	 * L'import vient de finir.
	 * <p>
	 * L'import peut finir plusieurs fois à partir du même objet TileMaker.
	 * 
	 * @param event
	 */
	public void importFinished(ImportEvent event) {

	}

	/**
	 * L'import vient d'être annulé.
	 * <p>
	 * L'import peut finir plusieurs fois à partir du même objet TileMaker.
	 * 
	 * @param event
	 */
	public void importAborted(ImportEvent event) {

	}

	/**
	 * L'import vient d'être annulé.
	 * <p>
	 * L'import peut finir plusieurs fois à partir du même objet TileMaker.
	 * 
	 * @param event
	 */
	public void waitingListChanged(ImportEvent event) {

	}

	/**
	 * Une tuile vient dêtre refusée.
	 * 
	 * @param event
	 */
	public void tileRefused(ImportEvent ev) {

	}

	/**
	 * Une exception vient de se produire, mais elle n'a pas arrété les
	 * opérations.
	 * 
	 * @param event
	 */
	public abstract void exceptionHappened(ImportEvent event);

	/**
	 * Une exception vient de se produire, elle a arrété les opérations.
	 * 
	 * @param event
	 */
	public abstract void fatalExceptionHappened(ImportEvent event);

}
