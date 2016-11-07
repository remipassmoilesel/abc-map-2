package abcmap;

import java.awt.GraphicsEnvironment;

public class Checks {

	private static Double minJavaVersion = 1.7d;

	public static void run() {

		// verifier la version de java
		checkJavaVersion();

		// vérifier l'envirronnement graphique
		checkGraphics();

	}

	private static void checkGraphics() {

		if (GraphicsEnvironment.isHeadless()) {
			LaunchError.showAndDie(LaunchError.ERRMSG_HEADLESS_ENV);
		}

	}

	/**
	 * Vérifier la version de Java ou afficher une erreur
	 */
	private static void checkJavaVersion() {

		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		pos = version.indexOf('.', pos + 1);

		double ver = -1;
		try {
			ver = Double.parseDouble(version.substring(0, pos));
		} catch (Exception e) {
		}

		if (ver < minJavaVersion) {
			LaunchError.showAndDie(LaunchError.ERRMSG_UPDATE_JAVA_VERSION);
		}
	}

}
