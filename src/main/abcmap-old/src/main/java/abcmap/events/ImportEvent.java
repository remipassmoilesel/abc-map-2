package abcmap.events;

import abcmap.utils.Utils;
import abcmap.utils.notifications.Notification;

/**
 * Evenement en provenance d'un objet d'importation. Utilisé rincipalement avec
 * ListenerHandler.
 * 
 * @author remipassmoilesel
 *
 */
public class ImportEvent extends Notification {

	/** Erreur lors de l'import n'ayant pas conduit à l'arret */
	public static final String EXCEPTION_HAPPENED = "EXCEPTION_HAPPENED";

	/** Erreur lors de l'import ayant conduit à l'arret */
	public static final String FATAL_EXCEPTION_HAPPEND = "FATAL_EXCEPTION_HAPPENED";

	/** L'import à été annulé */
	public static final String IMPORT_ABORTED = "IMPORT_ABORTED";

	/** L'import s'est fini correctement */
	public static final String IMPORT_FINISHED = "IMPORT_FINISHED";

	/** L'import vient de commencer */
	public static final String IMPORT_STARTED = "IMPORT_STARTED";

	/** Une tuile à été refusée */
	public static final String TILE_REFUSED = "TILE_REFUSED";

	/** La liste d'attente à changé */
	public static final String WAITING_LIST_CHANGED = "WAITING_LIST_CHANGED";

	/** Le nombre de tuiles importées */
	private int imported;

	/** Le nombre de tuiles refusées */
	private int refused;

	/** Le nombre d'erreurs E/S */
	private int ioErrors;

	/** Le nombre total à importer */
	private int totalToImport;

	private int screenCatchNumber;

	public ImportEvent(String event, Object value) {
		super(event, value);
	}

	public int getImported() {
		return imported;
	}

	public void setImported(int imported) {
		this.imported = imported;
	}

	public int getRefused() {
		return refused;
	}

	public void setRefused(int refused) {
		this.refused = refused;
	}

	public int getIoErrors() {
		return ioErrors;
	}

	public void setIoErrors(int ioErrors) {
		this.ioErrors = ioErrors;
	}

	public int getTotalToImport() {
		return totalToImport;
	}

	public void setTotalToImport(int totalToImport) {
		this.totalToImport = totalToImport;
	}

	public void setScreenCatchNumber(int screenCatched) {
		screenCatchNumber = screenCatched;
	}

	public int getScreenCatchNumber() {
		return screenCatchNumber;
	}

	@Override
	public String toString() {

		Object[] keys = { "totalToImport", "imported", "refused", "ioErrors", };

		Object[] values = { this.totalToImport, this.imported, this.refused, this.ioErrors, };

		return Utils.toString(this, keys, values);
	}

}
