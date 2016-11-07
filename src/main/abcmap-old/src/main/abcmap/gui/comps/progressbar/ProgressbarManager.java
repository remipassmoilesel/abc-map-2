package abcmap.gui.comps.progressbar;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;

public class ProgressbarManager {

	/** Attente avant modification de la barre de progression, en ms */
	public final static Integer WAITING_TIME = 300;

	private JProgressBar progressbar;

	/** Etiquette de texte associée à la barre de progression */
	private JLabel label;

	/** Liste des taches. 0 est la tache courante */
	private ArrayList<ProgressbarTask> tasks;

	private ProgressbarUpdater progressbarUpdater;

	private boolean showLabel;

	public ProgressbarManager() {
		this.progressbar = new JProgressBar();
		this.label = new JLabel();
		this.tasks = new ArrayList<ProgressbarTask>();
		this.progressbarUpdater = new ProgressbarUpdater();
		this.showLabel = true;
	}

	private class ProgressbarUpdater implements Runnable {

		@Override
		public void run() {

			GuiUtils.throwIfNotOnEDT();

			// pas de taches: cacher les composants puis terminer
			if (tasks.size() <= 0) {
				setComponentsVisible(false);
				return;
			}

			// recuperer la tache en cours
			ProgressbarTask task = tasks.get(0);

			// verifier le temps de mise à jour, sauf pour la derniere mise à
			// jour
			if (task.getCurrentValue() < task.getMaxValue()
					&& task.getEllapsedTimeSinceUpdated() < WAITING_TIME) {
				return;
			}

			// afficher la barre si necessaire
			if (progressbar.isVisible() == false) {
				progressbar.setVisible(true);
			}

			// afficher l'etiquette si necessaire
			if (showLabel && label.isVisible() == false) {
				label.setVisible(true);
			}

			// afficher l'etiuette de texte
			if (showLabel) {

				StringBuilder txt = new StringBuilder(20);

				txt.append("<html>");

				// afficher le nombre de tâches si > 1
				if (tasks.size() > 1) {
					txt.append(Integer.toString(tasks.size()) + " tâches en cours.");
				}

				// afficher l'etiquette de la tache
				txt.append(" " + task.getLabel());

				txt.append("</html>");

				if (label.getText().equals(txt) == false) {
					label.setText(txt.toString());
				}

			}

			// la barre doit être en mode indetermine
			if (task.isIndeterminate()) {
				if (progressbar.isIndeterminate() == false) {
					progressbar.setIndeterminate(true);
				}
			}

			// la valeur en cours est inférieure au maximum
			else if (task.getMaxValue() >= task.getCurrentValue()) {

				// repasser en mode normal si necessaire
				if (progressbar.isIndeterminate() == true)
					progressbar.setIndeterminate(false);

				// ajuster les valeurs min, max, et actuelles
				if (progressbar.getMinimum() != task.getMinValue())
					progressbar.setMinimum(task.getMinValue());

				if (progressbar.getMaximum() != task.getMaxValue())
					progressbar.setMaximum(task.getMaxValue());

				if (progressbar.getValue() != task.getCurrentValue())
					progressbar.setValue(task.getCurrentValue());

			}

			// fin de la tache
			else {

				try {
					tasks.remove(task);
				} catch (IndexOutOfBoundsException e) {
					if (MainManager.isDebugMode())
						Log.error(e);
				}

				// plus de tache, cacher les composants
				if (tasks.size() <= 0) {
					setComponentsVisible(false);
				}
			}

			// enregistrer l'heure de mise à jour
			task.touch();

		}

	}

	/**
	 * Mettre à jour la barre de progression en fonction du temps de dernier
	 * rafraichissment de la tâche fournit en paramètres.
	 * <p>
	 * Threadsafe
	 * 
	 * @param task
	 * @return
	 */
	public boolean updateProgressbarLater(ProgressbarTask task) {

		// verifier le temps de mise à jour, sauf pour la derniere mise à jour
		if (task.getCurrentValue() < task.getMaxValue()
				&& task.getEllapsedTimeSinceUpdated() < WAITING_TIME) {
			return false;
		}

		// lancer la mise à jour
		updateProgressbarLater();
		return true;
	}

	private void updateProgressbarLater() {
		SwingUtilities.invokeLater(progressbarUpdater);
	}

	public void removeTask(ProgressbarTask task) {
		tasks.remove(task);
		updateProgressbarLater();
	}

	public void addTask(ProgressbarTask task) {
		tasks.add(task);
		updateProgressbarLater();
	}

	public ProgressbarTask addTask(String label, boolean indeterminate, int min, int max,
			int current) {

		ProgressbarTask pbt = new ProgressbarTask(label, indeterminate, min, max, current);
		tasks.add(pbt);

		updateProgressbarLater();
		return pbt;
	}

	public JProgressBar getProgressbar() {
		return progressbar;
	}

	public JLabel getLabel() {
		return label;
	}

	public void setComponentsVisible(boolean b) {

		GuiUtils.throwIfNotOnEDT();

		if (progressbar != null)
			progressbar.setVisible(b);

		if (label != null)
			label.setVisible(b);
	}

	/**
	 * ThreadSafe
	 * 
	 * @param b
	 */
	public void setComponentsVisibleThreadSafe(final boolean b) {
		if (progressbar != null) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					progressbar.setVisible(b);
				}
			});
		}
	}

}
