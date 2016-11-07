package abcmap.utils.sandbox;

import java.util.ArrayList;

/**
 * Classe de calcul de temps pour effectuer une action
 * 
 * @author remipassmoilesel
 *
 */
public abstract class MemoryChargeComputing {

	/** Nombre d'executions de l'action */
	private int timesToDo;
	private ArrayList<MemoryChargeSnapshot> measures;

	public MemoryChargeComputing() {

		// nombre d'execution de l'action
		timesToDo = 50;

		// les temps enregistrés
		measures = new ArrayList<MemoryChargeSnapshot>(100);

	}

	public void setTimesToDo(int timesToDo) {
		this.timesToDo = timesToDo;
	}

	public void launchAndMeasure() {

		System.out.println("Début des mesures...");

		for (int i = 0; i < timesToDo; i++) {

			// mesure d'avant
			measures.add(MemoryChargeSnapshot.snap());

			// executer l'action
			try {
				actionToMeasure();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// mesure d'aprés
			measures.add(MemoryChargeSnapshot.snap());

		}

	}

	public void launchAndPrintAll() {
		launchAndMeasure();
		printAllResults();
	}

	public void printResumeResults() {

		if (measures.size() <= 0) {
			System.out.println("Aucun résultat à afficher. ");
		}

		MemoryChargeSnapshot first = measures.get(0);

		// tous les resultats sont affichés en MB
		double mb = 1024 * 1024;

		double minFreeMem = first.getFreeMemory() / mb;
		double minMaxMem = first.getMaxMemory() / mb;
		double minTotalMem = first.getTotalMemory() / mb;

		double maxFreeMem = 0;
		double maxMaxMem = 0;
		double maxTotalMem = 0;

		double avgFreeMem = 0;
		double avgMaxMem = 0;
		double avgTotalMem = 0;

		// parcourir la liste
		for (MemoryChargeSnapshot mem : measures) {

			avgFreeMem += mem.getFreeMemory() / mb;
			avgMaxMem += mem.getMaxMemory() / mb;
			avgTotalMem += mem.getTotalMemory() / mb;

			double cFreeMem = mem.getFreeMemory() / mb;
			double cMaxMem = mem.getMaxMemory() / mb;
			double cTotalMem = mem.getTotalMemory() / mb;

			// conserver le max
			if (cFreeMem > maxFreeMem) {
				maxFreeMem = cFreeMem;
			}
			if (cMaxMem > maxMaxMem) {
				maxMaxMem = cMaxMem;
			}
			if (cTotalMem > maxTotalMem) {
				maxTotalMem = cTotalMem;
			}

			// conserver le min
			if (cFreeMem < minFreeMem) {
				minFreeMem = cFreeMem;
			}
			if (cMaxMem < minMaxMem) {
				minMaxMem = cMaxMem;
			}
			if (cTotalMem < minTotalMem) {
				minTotalMem = cTotalMem;
			}

		}

		// calcul de la moyenne
		avgFreeMem /= measures.size();
		avgMaxMem /= measures.size();
		avgTotalMem /= measures.size();

		// affichage du résumé
		System.out.println(measures.size() + " essais.");

		System.out.println();
		System.out.println("Moyennes (mb):");
		System.out.println("Moyenne mémoire utilisée: " + (avgTotalMem - avgFreeMem));
		System.out.println("Moyenne mémoire libre: " + avgFreeMem);
		System.out.println("Moyenne mémoire totale: " + avgTotalMem);
		System.out.println("Moyenne mémoire max disponible: " + avgMaxMem);

		System.out.println();
		System.out.println("Minimums (mb):");
		System.out.println("Minimum mémoire libre: " + minFreeMem);
		System.out.println("Minimum mémoire totale: " + minTotalMem);
		System.out.println("Minimum mémoire max disponible: " + minMaxMem);

		System.out.println();
		System.out.println("Maximums (mb):");
		System.out.println("Maximum mémoire libre: " + maxFreeMem);
		System.out.println("Maximum mémoire totale: " + maxTotalMem);
		System.out.println("Maximum mémoire max disponible: " + maxMaxMem);

	}

	public void printAllResults() {

		if (measures.size() <= 0) {
			System.out.println("Aucun résultat à afficher. ");
			return;
		}

		printResumeResults();

		System.out.println();
		System.out.println("-----------");
		System.out.println();

		// parcourir la liste
		int i = 1;
		for (MemoryChargeSnapshot mem : measures) {
			System.out.println("Essai #" + i + ": " + mem.getReadableString());
			i++;
		}

	}

	/** L'action à mesurer */
	protected abstract void actionToMeasure();

}
