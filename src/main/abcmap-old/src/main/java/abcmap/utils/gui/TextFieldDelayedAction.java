package abcmap.utils.gui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;

import abcmap.managers.Log;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

/**
 * Classe permettant de declencher une action à la fin de la saisie dans un
 * champs de texte.
 * <p>
 * 
 * Utiliser TextFieldDelayedAction.delayedActionFor(jtf, action);
 * 
 * @author remipassmoilesel
 *
 */
public class TextFieldDelayedAction {

	private int timeToAdd;
	private ArrayList<Runnable> actions;
	private TextFieldTimer timer;
	private boolean executeActionsInEDT;
	private TextListener listener;

	public static TextFieldDelayedAction delayedActionFor(JTextComponent txt,
			Runnable run, Boolean runOnEDT) {

		// verifier les parametres
		if (txt == null) {
			throw new NullPointerException("Textcomponent cannot be null");
		}

		if (run == null) {
			throw new NullPointerException("Runnable action cannot be null");
		}

		// creer une action retardée
		TextFieldDelayedAction tfda = new TextFieldDelayedAction();
		tfda.executeActionsInEDT(runOnEDT);
		tfda.addAction(run);

		// l'ajouter au champ de texte
		txt.addKeyListener(tfda.getListener());

		return tfda;
	}

	/**
	 * Utiliser TextFieldDelayedAction.delayedActionFor(jtf, action);
	 */
	private TextFieldDelayedAction() {

		// temps à ajouter a chaque interaction (ms)
		this.timeToAdd = 200;

		// les actions à effectuer apres la fin des interactions
		this.actions = new ArrayList<>(5);

		// par defaut executer les actions sur le thread courant
		this.executeActionsInEDT = false;

		this.listener = new TextListener();

	}

	/**
	 * Changement dans le champs texte: creer eventuellement un timer et ajouter
	 * du temps.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextListener extends KeyListenerUtil {

		@Override
		public void keyReleased(KeyEvent e) {

			// creer un nouveau compteur à chaque fois
			if (timer == null || timer.isRunning() == false)
				timer = new TextFieldTimer();

			// lors des interactions ajouter du temps avant action
			timer.addTime(timeToAdd);

			// demarrage
			if (timer.isRunning() == false) {
				ThreadManager.runLater(timer);
			}

		}

	}

	public TextListener getListener() {
		return listener;
	}

	public void setTimeToAdd(int timeToAdd) {
		this.timeToAdd = timeToAdd;
	}

	public void addAction(Runnable action) {
		this.actions.add(action);
	}

	public void executeActionsInEDT(boolean inEDT) {
		this.executeActionsInEDT = inEDT;
	}

	/**
	 * Timer pour execution de tache en fin de frappe. A chaque frappe du temps
	 * est ajouté au timer, et lorsque le temps est écoulé le timer lance la
	 * tache.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class TextFieldTimer implements Runnable {

		private int counter;
		private int step;
		private int maxLimit;

		public TextFieldTimer() {

			// compteur avant action en ms
			this.counter = 0;

			// etape en ms
			this.step = 500;

			// la limite de temps maximum
			this.maxLimit = 1000;

		}

		@Override
		public void run() {

			// eviter les appels intempestifs
			if (ThreadAccessControl.get(1).askAccess() == false) {
				return;
			}

			// attendre le temps demandé
			while (counter > 0) {

				// attendre
				boolean error = false;
				try {
					Thread.sleep(step);
				} catch (InterruptedException e) {
					Log.error(e);
					error = true;
				}

				if (error == false) {
					counter -= step;
				}

			}

			// executer les actions
			executeActions();

			ThreadAccessControl.get(1).releaseAccess();
		}

		public void addTime(int timeMs) {
			counter += timeMs;

			if (counter > maxLimit) {
				counter = maxLimit;
			}
		}

		public boolean isRunning() {
			return ThreadAccessControl.get(1).isOngoingThread();
		}

	}

	/**
	 * Declencher les actions et se debarrasse du compteur
	 */
	private void executeActions() {

		// execution des actions
		Runnable runActions = new Runnable() {
			public void run() {
				for (Runnable runnable : actions) {
					runnable.run();
				}
			}
		};

		// dans l'edt
		if (executeActionsInEDT) {
			SwingUtilities.invokeLater(runActions);
		}

		// ou sur le thread courant
		else {
			runActions.run();
		}

		// suppression du compteur
		timer = null;
	}

}
