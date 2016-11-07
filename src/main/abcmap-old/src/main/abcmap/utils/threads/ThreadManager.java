package abcmap.utils.threads;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abcmap.events.ErrorEvent;
import abcmap.managers.Log;
import abcmap.utils.ErrorListener;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;

/**
 * Classe utilitaire de gestion de threads.
 * 
 * @author remipassmoilesel
 *
 */
public class ThreadManager {

	/** Préfixe par défaut des noms de threads */
	protected static final String DEFAULT_THREAD_PREFIX = "Abc-Map_";

	/** Préfixe de nom de thread anonyme */
	private static final String ANONYM_THREAD_PREFIX = "Anonym_";

	/** Compteur de threads anonymes */
	private static int anonymThreads = 0;

	/** Nombre maximum de threads simultanés */
	protected static final int MAX_THREAD_NBR = 100;

	/** La seule instance de gestionnaire de thread disponible */
	private static ThreadManager mainManager = new ThreadManager();

	/**
	 * Retourne un thread non comptabilisé. Utiliser de préférence la méthode
	 * runLater();
	 * 
	 * @param runnable
	 * @return
	 */
	@Deprecated
	public static Thread getAnonymThread(Runnable runnable) {
		Thread t = new Thread(runnable);
		t.setName(ANONYM_THREAD_PREFIX + DEFAULT_THREAD_PREFIX
				+ ++anonymThreads);
		return t;
	}

	/**
	 * Ecouter la levée d'exception non gérées.
	 * 
	 * @return
	 */
	public static ListenerHandler<ErrorListener> getListenerHandler() {
		return mainManager.listeners;
	}

	/**
	 * Executer une tache dans un thread séparé, à la suite d'éventuelles tâche
	 * en cours.
	 * 
	 * @param runnable
	 */
	public static void runLater(Runnable runnable) {
		runLater(runnable, false);
	}

	/**
	 * Executer une tache dans un thread séparé, à la suite d'éventuelles tâche
	 * en cours.
	 * 
	 * @param runnable
	 */
	public static void runLater(Runnable runnable, boolean onEDT) {

		// ajout de la tache en file d'attente
		mainManager.addTask(runnable, onEDT);

		// demarrage de la distribution des threads si nécéssaire
		mainManager.startDistributionIfNecessary();

	}

	/** La tache qui lance tous les autres threads */
	private Thread distributionThread;

	/** File d'attente des tâches */
	private ArrayList<ManagedThread> threadWaitingList;

	/** La liste des threads actifs */
	private ArrayList<ManagedThread> activeThreadsList;

	/** Liste des objets à l'ecoute des erreurs non traitées */
	private ListenerHandler<ErrorListener> listeners;

	private ThreadManager() {

		threadWaitingList = new ArrayList<ManagedThread>();
		activeThreadsList = new ArrayList<ManagedThread>();
		distributionThread = null;

		listeners = new ListenerHandler<ErrorListener>();
	}

	/**
	 * Démarre la distribution des taches si elle est stoppée.
	 */
	private synchronized void startDistributionIfNecessary() {

		// si le thread est arreté ou null
		if (distributionThread == null || distributionThread.isAlive() == false) {

			// créer une tache de distribution
			distributionThread = getAnonymThread(new DistributionThread(
					mainManager));

			// la nommer
			distributionThread.setName(DEFAULT_THREAD_PREFIX + "-Distribution");

			// puis démarrer
			distributionThread.run();
		}
	}

	protected ArrayList<ManagedThread> getThreadWaitingList() {
		return threadWaitingList;
	}

	protected ArrayList<ManagedThread> getActiveThreadsList() {
		return activeThreadsList;
	}

	/**
	 * Executer une tache apres un temps déterminé minimum. Si de trop nombreux
	 * thread sont présents, le thread attendra.
	 * 
	 * @param timeMilliSec
	 * @param run
	 * @param onEDT
	 */
	public static void runLater(final Integer timeMilliSec, final Runnable run,
			final boolean onEDT) {

		runLater(new Runnable() {
			public void run() {

				// attendre le temps demandé
				try {
					Thread.sleep(timeMilliSec);
				} catch (InterruptedException e) {
					Log.error(e);
				}

				// lancement de la tache
				ThreadManager.runLater(run, onEDT);
			}
		});

	}

	public static void runLater(final Integer timeMilliSec, final Runnable run) {
		runLater(timeMilliSec, run, false);
	}

	/**
	 * 
	 * @param runnable
	 * @param onEDT
	 */
	protected void addTask(Runnable runnable, boolean onEDT) {
		threadWaitingList.add(new ManagedThread(runnable, onEDT, this));
	}

	protected void addActiveThread(ManagedThread listedThread) {
		activeThreadsList.add(listedThread);
	}

	protected void removeActiveThread(ManagedThread listedThread) {
		activeThreadsList.remove(listedThread);
	}

	protected void fireError(Throwable e) {
		listeners.fireEvent(new ErrorEvent(ErrorEvent.NON_CATCHED_ERROR, e));
	}
}
