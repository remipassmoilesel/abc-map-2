package abcmap.utils.threads;

import abcmap.events.ErrorEvent;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.ErrorListener;

public class ThreadErrorListener implements ErrorListener {

	/** Interval minimum entre erreurs de mémoire */
	private static final int MIN_INTERVAL_BETWEEN_ERRORS = 5 * 1000;

	/**
	 * La dernière fois qu'un message d'erreur pour manque de memoire à été
	 * montré
	 */
	private long lastTimeOutOfMemoryError;

	private GuiManager guim;

	public ThreadErrorListener() {
		lastTimeOutOfMemoryError = 0;
		guim = MainManager.getGuiManager();
	}

	@Override
	public void errorHapened(ErrorEvent e) {

		// l'evenement concerne un manque de mémoire
		if (e.getValue() instanceof OutOfMemoryError) {

			// verifier que trop de messages ne s'affichent pas à la suite
			boolean showMessage = lastTimeOutOfMemoryError + MIN_INTERVAL_BETWEEN_ERRORS < System
					.currentTimeMillis();

			if (showMessage) {

				// TODO Compléter le message
				guim.showErrorInDialog(guim.getMainWindow(),
						"Le programme manque de mémoire, .......... ", false);

				// enregistrer dans le journal
				String memory = Runtime.getRuntime().freeMemory() + " availables on "
						+ Runtime.getRuntime().maxMemory();

				Log.error(new Exception(
						"Out of memory: " + memory + ". Time: " + lastTimeOutOfMemoryError));

			}

		}

	}

}
