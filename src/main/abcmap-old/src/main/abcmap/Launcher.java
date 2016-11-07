package abcmap;


/**
 * Lancement du programme.
 * 
 * <p>
 * Certaines fonctions sont gérés directement dans le lanceur pour plus de
 * rapidité: <br>
 * - Un splashcreen pour faire patienter l'utilisateur pendant l'initialisation.
 * <br>
 * - La gestion de quelques erreurs
 * 
 * @author remipassmoilesel
 *
 */
public class Launcher {

	public static void main(String[] args) {

		Checks.run();
		Initialisation.doInit(args);
		Initialisation.launchGui();

		// sandBox(args);

		// AB_TimeComputingArea.launch();

		// AC_DrawingArea.launch();

		// AD_SwingTestArea.launch();

		// AE_MemoryMeasureArea.launch();

	}

	private static void sandBox(String[] args) {
	}
}
