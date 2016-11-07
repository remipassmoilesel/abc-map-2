package abcmap.utils.sandbox;

import java.util.ArrayList;

/**
 * Classe de calcul de temps pour effectuer une action
 * 
 * @author remipassmoilesel
 *
 */
public abstract class ExecutionTimeComputing {

	/** Le nombrede fois ou l'action doit être éxécutée */
	private int timesToDo;

	/** Les mesures résultat */
	private ArrayList<Long> timesMeasured;

	/** La position courante lors des mesures */
	protected int currentPos;

	public ExecutionTimeComputing() {

		// nombre d'execution de l'action
		timesToDo = 100;

		// les temps enregistrés
		timesMeasured = new ArrayList<Long>(100);

	}

	public void setTimesToDo(int timesToDo) {
		this.timesToDo = timesToDo;
	}

	public void launch() {

		System.out.println("Début des mesures...");

		beforeMeasures();

		currentPos = 0;

		for (currentPos = 0; currentPos < timesToDo; currentPos++) {

			// enregistrer le temps 1
			Long t1 = System.currentTimeMillis();

			// executer l'action
			try {
				actionToMeasure();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// enregistrer le temps 2
			Long t2 = System.currentTimeMillis();

			// ajouter aux temps
			timesMeasured.add(t2 - t1);
		}

		afterMeasures();

	}

	public void launchAndPrintAll() {
		launch();
		printAllResults();
	}

	public void launchAndPrintResume() {
		launch();
		printResumeResults();
	}

	public void printResumeResults() {

		if (timesMeasured.size() <= 0) {
			System.out.println("Aucun résultat à afficher. ");
			return;
		}

		// calculs
		long average = 0l;
		long max = 0l;
		long min = timesMeasured.get(0);

		// parcourir la liste
		for (Long time : timesMeasured) {

			// ajouter à la moyenne
			average += time;

			// conserver le max
			if (time > max)
				max = time;

			// conserver le min
			if (time < min)
				min = time;

		}

		// calcul de la moyenne
		average /= timesMeasured.size();

		System.out.println(timesMeasured.size() + " essais.");
		System.out.println("Moyenne: " + msToSec(average) + " s.");
		System.out.println("Minimum: " + msToSec(min) + " s.");
		System.out.println("Maximum: " + msToSec(max) + " s.");

	}

	public void printAllResults() {

		if (timesMeasured.size() <= 0) {
			System.out.println("Aucun résultat à afficher. ");
			return;
		}

		printResumeResults();

		System.out.println();
		System.out.println("-----------");
		System.out.println();

		// parcourir la liste
		int i = 1;
		for (Long time : timesMeasured) {
			System.out.println("Essai #" + i + ": " + msToSec(time) + " s.");
			i++;
		}

	}

	private double msToSec(long ms) {
		return ms / 1000d;
	}

	/**
	 * L'action à mesurer
	 */
	protected abstract void actionToMeasure();

	protected void afterMeasures() {

	}

	protected void beforeMeasures() {

	}

}
