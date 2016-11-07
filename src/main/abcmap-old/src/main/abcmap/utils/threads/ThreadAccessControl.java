package abcmap.utils.threads;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import abcmap.managers.Log;

/**
 * Gestion de thread. Permet d'autoriser l'accés à un morceau de code à un
 * nombre limité de thread.
 * 
 * @author remipassmoilesel
 *
 */
public class ThreadAccessControl {

	private static HashMap<String, ThreadAccessControl> registeredControls = new HashMap<String, ThreadAccessControl>();

	/** Le nombre maximum de threads autorisés */
	private int maxAccess;

	/** L'action à éxéuter en cas d'erreur */
	private Runnable refusedAccessAction;

	/** Si vrai, l'action sera exécutée sur l'EDT */
	private boolean refusedAccessActionOnEDT;

	/**
	 * Temps de validité d'une trace de passgae. Si la trace est plus
	 * ancienneque le temps actuel - le timeout, alors elle est ignorée
	 */
	private int defaultAccessTimeOut;

	/** La liste des accés en cours */
	private ArrayList<ThreadAccessTrace> traces;

	public ThreadAccessControl() {
		this.maxAccess = 1;
		this.refusedAccessAction = null;
		this.refusedAccessActionOnEDT = false;
		this.defaultAccessTimeOut = ThreadAccessTrace.NO_TIMEOUT;
		this.traces = new ArrayList<ThreadAccessTrace>();
	}

	/**
	 * Temps d'expiration en ms par défaut d'un accés à un morceau de code.
	 * Exemple: pour "10 000", après un premier accés si une erreur survient, 10
	 * s plus tard un autre thread pourra y accéder.
	 * 
	 * @param timeout
	 */
	public void setDefaultAccessTimeOut(int timeout) {
		this.defaultAccessTimeOut = timeout;
	}

	/**
	 * <p>
	 * Méthode utilitaire rapide de controle de threads.
	 * <p>
	 * Lors du premier appel créé un controlleur avec comme nom la classe
	 * d'instanciation courante + l'identifiant. Les appels suivant utiliseront
	 * le controleur. Reprends les paramètres par défaut cad 1 accés maximum et
	 * pas de timeout.
	 * <p>
	 * <b>Attention:</b> L'identifiant du controlleur sera
	 * "package.classe_method_#", donc attention au nombre de controlleurs
	 * utilisés.
	 * 
	 * @return
	 */
	public static ThreadAccessControl get(int id) {

		StackTraceElement stack = Thread.currentThread().getStackTrace()[2];

		// creer un id à partir de la pile d'appel
		String controlId = stack.getClassName() + "_" + stack.getMethodName()
				+ "_" + id;

		// le controle n'existe pas encore: création puis retour
		ThreadAccessControl control = registeredControls.get(controlId);
		if (control == null) {
			control = new ThreadAccessControl();
			registeredControls.put(controlId, control);
		}

		return control;

	}

	/**
	 * Renvoi le nombre d'accés courants.
	 * 
	 * @return
	 */
	public int howManyAccess() {
		return traces.size();
	}

	/**
	 * Renvoi vrai si au moins un thread à accés au morceau de code.
	 * 
	 * @return
	 */
	public boolean isOngoingThread() {
		return traces.size() > 0;
	}

	/**
	 * Demander l'accés à un morceau de code. Si le nombre de thread autorisé
	 * permet l'accés, enregistre l'accés et retourne vrai. Sinon retourne faux.
	 * 
	 * @return
	 */
	public synchronized boolean askAccess() {
		return askAccess(defaultAccessTimeOut);
	}

	/**
	 * Demander l'accés à un morceau de code. Si le nombre de thread autorisé
	 * permet l'accés, enregistre l'accés et retourne vrai. Sinon retourne faux.
	 * 
	 * @return
	 */
	public synchronized boolean askAccess(int timeOut) {

		// enlever les traces périmées
		purgeTraces();

		// il y a suffisament de place pour le thread, enregistrement
		if (traces.size() < maxAccess) {
			traces.add(new ThreadAccessTrace(Thread.currentThread(),
					defaultAccessTimeOut));
			return true;
		}

		// il n'y a pas assez de place, signalement de l'erreur potentielle
		else {

			// reporter l'accés refusé
			Log.error(new Exception("Access refused: MaxAccess: " + maxAccess
					+ ", Currents threads: " + traces));

			// executer l'action personnalisée en cas de refus
			if (refusedAccessAction != null) {
				if (refusedAccessActionOnEDT == true) {
					SwingUtilities.invokeLater(refusedAccessAction);
				}

				else {
					ThreadManager.runLater(refusedAccessAction);
				}
			}

			return false;
		}
	}

	/**
	 * Signaler la fin de l'accés à un morceau de code
	 */
	public synchronized void releaseAccess() {

		// iterer les traces
		for (ThreadAccessTrace tr : new ArrayList<>(traces)) {

			// comparer les identifiants
			if (tr.getId() == Thread.currentThread().getId()) {
				traces.remove(tr);
			}
		}

	}

	/**
	 * Enlever les traces plus anciennes que le timeout. Une trace peut être
	 * supprimée car plusieurs ancienne pour autoriser à nouveau l'accés à un
	 * code après une éventuelle erreur.
	 */
	private void purgeTraces() {

		// itérer les traces
		for (ThreadAccessTrace tr : new ArrayList<>(traces)) {

			// comparer les temps
			if (tr.isValidNow() == false) {

				// retirer la trace et enregistrer l'action, due à une
				// potentielle erreur
				Log.error(new Exception(
						"Access trace removed because of timeout: " + tr));
				traces.remove(tr);
			}
		}

	}

	/**
	 * Action par défaut à réaliser en cas d'accés refusé. L'action est éxécutée
	 * hors de l'EDT.
	 * 
	 * @param runnable
	 */
	public void setRefusedAccessAction(Runnable runnable) {
		setRefusedAccessAction(runnable, false);
	}

	/**
	 * Action par défaut à réaliser en cas d'accés refusé. L'action est éxécutée
	 * hors ou sur l'EDT.
	 * 
	 * @param runnable
	 */
	public void setRefusedAccessAction(Runnable runnable, boolean onEdt) {
		this.refusedAccessAction = runnable;
		this.refusedAccessActionOnEDT = onEdt;
	}

	public void setMaxAccess(int maxAccess) {
		this.maxAccess = maxAccess;
	}

}
