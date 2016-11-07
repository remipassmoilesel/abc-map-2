package abcmap.utils.threads;

import javax.swing.SwingUtilities;

import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;

/**
 * Lance une tache en la comptabilisant dans la liste des thread actifs: <br>
 * - ajout avant éxécution <br>
 * - puis retrait après éxécution
 * 
 * @author remipassmoilesel
 *
 */
class ManagedThread implements Runnable {

	/** Le manager parent à partir duquel la t^ache se comptabilisera */
	private ThreadManager parent;

	/** La tache à éxécuter */
	private Runnable runnable;

	/** Si vrai la tache sera éxécutée sur l'EDT */
	private boolean onEDT;

	/** La pile d'appel de la tache */
	private StackTraceElement[] callStack;

	/** Si vrai, affiche des informations supplémentaires */
	private boolean debugMode = MainManager.isDebugMode();

	public ManagedThread(Runnable task, boolean onEDT, ThreadManager parent) {

		this.parent = parent;
		this.onEDT = onEDT;
		this.runnable = task;

		this.callStack = Thread.currentThread().getStackTrace();
	}

	/**
	 * Démarrer la tache
	 */
	public void start() {

		// Executer sur l'EDT
		if (onEDT) {
			SwingUtilities.invokeLater(this);
		}

		else {
			Thread thread = ThreadManager.getAnonymThread(this);
			thread.setName(ThreadManager.DEFAULT_THREAD_PREFIX
					+ Utils.getDate("HH-mm-ss_") + System.nanoTime());
			thread.start();
		}

	}

	/**
	 * Utiliser plutot la méthode start()
	 */
	@Deprecated
	@Override
	public void run() {

		// comptabilisation
		parent.addActiveThread(this);

		// execution de la tache
		try {
			runnable.run();
		}

		// erreur eventuelle
		catch (Throwable e) {

			Log.error(e);
			parent.fireError(e);

			// imprimer la trace d'appel du thread
			if (debugMode) {
				Log.error(getStackTraceAsString());
			}
		}

		// retrait de la compta
		finally {
			parent.removeActiveThread(this);
		}

	}

	/**
	 * Afficher la liste des appels préliminaires au Thread
	 * 
	 * @return
	 */
	public String getStackTraceAsString() {

		String rslt = "Task name: " + Thread.currentThread().getName()
				+ " - Creation stack: ";

		for (StackTraceElement ste : callStack) {
			// conserver le \tat ou eclipse ne retrouvera pas les sources lors
			// d'un clic
			rslt += "\tat " + ste;
		}

		return rslt;
	}

}
