package abcmap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.SwingUtilities;

import abcmap.draw.links.LinkLibrary;
import abcmap.draw.links.MapLinkMonitor;
import abcmap.draw.styles.BackgroundRenderer;
import abcmap.draw.symbols.SymbolImageLibrary;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.gui.GuiColors;
import abcmap.gui.comps.help.Interaction;
import abcmap.gui.dialogs.SplashScreen;
import abcmap.gui.dialogs.simple.SimpleErrorDialog;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.Lng;
import abcmap.utils.gui.Lng.Langage;

public class Initialisation {

	private static final String LAMB_FASHION_ARG = "-LAMB_FASHION";
	private static Path projectToOpen;

	/**
	 * Initilisation de l'application, sans lancement de GUI
	 *
	 * @param args
	 */
	public static void doInit(String[] args) {

		// parser les arguments
		boolean devMode = false;
		projectToOpen = null;

		if (args != null && args.length > 0) {

			for (String str : args) {

				if (str.equalsIgnoreCase(LAMB_FASHION_ARG)) {
					devMode = true;
				}

				else {
					projectToOpen = Paths.get(str);
				}
			}

		}

		// mode de développement, à placer avant l'initialisation de la
		// journalisation
		MainManager.setDeveloppementMode(devMode);

		// ecran pour patienter
		// if (devMode == false)
		// showSplashScreen();

		// journalisation
		try {
			Log.init();
		} catch (Exception e) {
			e.printStackTrace();
			LaunchError.showErrorAndDie();
		}

		// chargement de la langue
		try {
			Lng.loadLanguage(Langage.FRENCH);
		} catch (IOException e) {
			Log.error(e);
			LaunchError.showErrorAndDie();
		}

		// elements d'interaction pour GUI
		Interaction.init();

		// création des controlleurs
		try {
			MainManager.init();
		} catch (IOException e) {
			Log.error(e);
		}

		// lancement des taches de fond
		MainManager.enableBackgroundWorker(true);

		// charger le profil de configuration
		try {
			MainManager.getConfigurationManager().loadSystemProfile();
		} catch (IOException e2) {
			Log.error(e2);
		}

		// configuration des basiques de l'interface
		GuiManager guim = MainManager.getGuiManager();
		guim.configureUiManager();

		GuiColors.init();

		// chargement des sets de symboles
		try {
			SymbolImageLibrary.init();
		} catch (IOException e) {
			Log.error(e);
			LaunchError.showErrorAndDie();
		}

		// outils de dessin
		ToolLibrary.initializeTools();

		// charger l'historique des projets et profils de configuration
		try {
			MainManager.getRecentManager().loadHistory();
		}
		// erreur lors du chargement, avertissement simple sans arret
		catch (IOException e) {
			Log.error(e);
			SimpleErrorDialog
					.showLater(null,
							"Erreur lors du chargement de l'historique des fichiers récents.");
		}

		// bibliothèque de liens
		LinkLibrary.init();

		// textures
		BackgroundRenderer.init();
	}

	/**
	 * Lancement du GUI
	 */
	public static void launchGui() {

		// lancement du GUI sur ce thread
		try {
			MainManager.getGuiManager().constructAndShowGui();
		} catch (InvocationTargetException | InterruptedException e1) {
			Log.error(e1);
			LaunchError.showErrorAndDie();
		}

		ProjectManager projectm = MainManager.getProjectManager();

		// creer un nouveau projet
		if (projectToOpen == null) {

			try {
				projectm.newProject();
			} catch (IOException e) {
				Log.error(e);
			}

		}

		// ou ouvrir un projet si demandé
		else {

			try {

				// ouverture du projet
				projectm.openProject(projectToOpen.toFile());

				// enregistrement dans les fichiers recents
				MainManager.getRecentManager().addProject(projectToOpen.toFile());

			} catch (IOException e) {
				SimpleErrorDialog.showLater(null,
						"Impossible d'ouvrir le fichier: <br>" + projectToOpen);
				Log.error(e);

				// ouvrir un nouveau projet
				try {
					projectm.newProject();
				} catch (IOException e1) {
					Log.error(e);
				}
			}
		}

		// selectionner le premier outil
		// dans invokeLater pour ne pas executer la commande trop tot
		MainManager.getDrawManager().setCurrentTool(ToolLibrary.SELECTION_TOOL);

		// mettre le gestionnaire de lien à l'ecoute de la carte
		MainManager.getMapManager().registerListenerOnMap(new MapLinkMonitor());

		// mettre à jour le gestionnaie d'import
		MainManager.getImportManager().updateCurrentsDataImportHeaders();

	}

	/**
	 * Afficher un splash screen pour faire patienter user
	 */
	private static void showSplashScreen() {

		System.out.println("Lancement d'Abc-Map, veuillez patienter...");
		System.out.println("Initializing Abc-Map, please wait...");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SplashScreen dial = new SplashScreen();
				dial.setVisible(true);
			}
		});
	}

}
