package abcmap.importation.robot;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import abcmap.configuration.Configuration;
import abcmap.events.ImportEvent;
import abcmap.exceptions.MapImportException;
import abcmap.gui.windows.DetachedWindow;
import abcmap.importation.tile.ImportEventListener;
import abcmap.importation.tile.TileMaker;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.GuiManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

public class RobotImporter implements Runnable,
		HasListenerHandler<ImportEventListener> {

	/** Toute première pause, le temps que l'utilisateur lache sa souris */
	private static final int FIRST_BREAK_BEFORE_IMPORT = 1000;

	/** Dimensions de l'ecran */
	private Dimension screen;

	/** La frabrique de tuiles */
	private TileMaker tileMaker;

	/** Flag d'abandon de l'import */
	private boolean abortImport = false;

	/** Capture d'ecrans */
	private Robot robot;

	/** Outil de deplacement de carte */
	private RobotMapMover mover;

	/** Outil de masquage de fenêtres */
	private RobotFrameHidder hidder;

	private ListenerHandler<ImportEventListener> listenerHandler;
	private ConfigurationManager configm;
	private ProjectManager projectm;

	private int screenCatchNumber;

	private GuiManager guim;

	public RobotImporter() throws MapImportException {

		this.guim = MainManager.getGuiManager();
		this.projectm = MainManager.getProjectManager();
		this.configm = MainManager.getConfigurationManager();
		this.screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.listenerHandler = new ListenerHandler<>();

		// utilitaire de masquage d'ecran
		hidder = new RobotFrameHidder();

		try {

			// outil de capture d'ecran
			robot = new Robot();

			// outil de deplacement de carte
			mover = new RobotMapMover();

		} catch (AWTException e) {
			Log.error(e);
			throw new MapImportException(
					MapImportException.ROBOT_INSTATIATION_EXCEPTION);
		}

	}

	@Override
	public void run() {

		// éviter les appels intempestifs
		if (ThreadAccessControl.get(1).askAccess() == false) {
			return;
		}

		// flag d'abandon de l'import
		abortImport = false;

		// projet non initilisé: arret
		if (projectm.isInitialized() == false) {
			abortAndLaunch(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			return;
		}

		// masquer la fenêtre principale, montrer la fenetre d'import
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					DetachedWindow win = guim.getRobotImportWindow();
					win.moveToDefaultPosition();
					guim.showOnlyWindow(win);
				}
			});
		} catch (InvocationTargetException | InterruptedException e1) {
			Log.error(e1);
		}

		// recuperer la configuration
		Configuration cg = configm.getConfiguration();

		// premiere pause, le temps que l'utilisateur lache sa souris
		Utils.sleep(FIRST_BREAK_BEFORE_IMPORT);

		// configurer le deplacement de l'ecran
		mover.setSpaceRectangle(configm.getCropRectangle());
		mover.setRelativeCovering(cg.ROBOT_IMPORT_COVERING);
		mover.setSleepTimeBeforeMoving(cg.ROBOT_IMPORT_MOVING_DELAY);

		// utilitaire de creation de tuiles
		tileMaker = new TileMaker();
		tileMaker.enableCropping(true);
		for (ImportEventListener listener : listenerHandler) {
			tileMaker.getListenerHandler().add(listener);
		}

		// mouvements à prévoir
		int width;
		int height;

		// Imporation à partir du coin haut gauche, pas de deplacement
		// preparatoire
		if (RobotCaptureMode.START_FROM_ULC.equals(cg.ROBOT_IMPORT_MODE)) {
			width = cg.ROBOT_IMPORT_WIDTH;
			height = cg.ROBOT_IMPORT_HEIGHT;
		}

		// Deplacement à partir du centre, se positionner sur le coin superieur
		// gauche
		else {

			// masquer les fentres puis attendre
			hidder.hideVisibleFrames();
			Utils.sleep(cg.WINDOW_HIDDING_DELAY);

			width = cg.ROBOT_IMPORT_WIDTH * 2 + 1;
			height = cg.ROBOT_IMPORT_HEIGHT * 2 + 1;

			// deplacement vers le point le plus à l'ouest
			for (int i = 0; i < cg.ROBOT_IMPORT_WIDTH; i++) {

				fireEvent(ImportEvent.WAITING_LIST_CHANGED, i);

				if (abortImport == true) {
					abortAndLaunch(ImportEvent.IMPORT_ABORTED);
					return;
				}

				// mouvement
				try {
					mover.dragToWest(1);
				}

				// l'utilisateur à annulé, arret
				catch (MapImportException e) {
					fireEvent(ImportEvent.IMPORT_ABORTED);
					ThreadAccessControl.get(1).releaseAccess();
					return;
				}

				// attentre avant deplacement
				Utils.sleep(cg.ROBOT_IMPORT_MOVING_DELAY);

			}

			// deplacement vers le point le plus au nord
			for (int i = 0; i < cg.ROBOT_IMPORT_HEIGHT; i++) {

				if (abortImport == true) {
					abortAndLaunch(ImportEvent.IMPORT_ABORTED);
					return;
				}

				// mouvement
				try {
					mover.dragToNorth(1);
				}

				// l'utilisateur à annulé, arret
				catch (MapImportException e) {
					abortAndLaunch(ImportEvent.IMPORT_ABORTED);
					return;
				}

				Utils.sleep(cg.ROBOT_IMPORT_MOVING_DELAY);
			}

			// masquer les fenetres puis attendre
			hidder.showHiddedFrames();
			Utils.sleep(cg.WINDOW_HIDDING_DELAY);
		}

		// mouvements total necessaires
		int end = width * height;

		// position sur le plan horizontal
		int xPos = 0;

		// position sur le plan vertical
		int yPos = 0;

		for (int i = 0; i < end; i++) {

			fireEvent(ImportEvent.WAITING_LIST_CHANGED);

			// arreter sur demande
			if (abortImport) {
				abortAndLaunch(ImportEvent.IMPORT_ABORTED);
				return;
			}

			// attendre avant capture
			Utils.sleep(cg.ROBOT_IMPORT_CAPTURE_DELAY);

			// masquer les fentres puis attendre
			hidder.hideVisibleFrames();
			Utils.sleep(cg.WINDOW_HIDDING_DELAY);

			// capturer l'ecran et l'ajouter à la fabrique de tuiles
			BufferedImage img = robot.createScreenCapture(new Rectangle(0, 0,
					screen.width, screen.height));

			tileMaker.add(img);

			// mouvements horizontaux
			if (xPos < width - 1) {

				// position y paire: mouvement W -> E
				if (yPos % 2 == 0) {

					// mouvement
					try {
						mover.dragToEast(1);
					}

					// l'utilisateur à annulé, arret
					catch (MapImportException e) {
						abortAndLaunch(ImportEvent.IMPORT_ABORTED);
						return;
					}

				}

				// position y impaire: mouvement E -> W
				else {

					// mouvement
					try {
						mover.dragToWest(1);
					}

					// l'utilisateur à annulé, arret
					catch (MapImportException e) {
						abortAndLaunch(ImportEvent.IMPORT_ABORTED);
						return;
					}

				}

				// mettre à jour l'indicateur de position horizontale
				xPos++;
			}

			// mouvement verticaux
			else if (yPos < height - 1) {

				// se deplacer vers le bas
				try {
					mover.dragToWest(1);
				}

				// l'utilisateur à annulé, arret
				catch (MapImportException e) {
					fireEvent(ImportEvent.IMPORT_ABORTED);
					ThreadAccessControl.get(1).releaseAccess();
					return;
				}

				// comptabiliser la position
				yPos++;

				// raz de l'indicateur horizontal
				xPos = 0;

			}

			// afficher les fenetres entre chaque deplacement
			hidder.showHiddedFrames();
			Utils.sleep(cg.WINDOW_HIDDING_DELAY);
		}

		// fin de l'importation
		fireEvent(ImportEvent.IMPORT_FINISHED);

		ThreadAccessControl.get(1).releaseAccess();

	}

	public void abortImportLater() {

		abortImport = true;

		// arret de l'analyse des tuiles
		if (tileMaker != null) {
			tileMaker.stopImportLater();
		}

	}

	/**
	 * Abandonner l'import et lancer un evenement
	 * 
	 * @param eventName
	 */
	private void abortAndLaunch(String eventName) {

		// montrer les fenetres masquées et attendre
		hidder.showHiddedFrames();

		// Attendre
		Utils.sleep(MainManager.getConfigurationManager().getWindowHidingDelay());

		// reinitialiser les indicateurs
		ThreadAccessControl.get(1).releaseAccess();

		// evenement
		fireEvent(eventName);

		abortImport = false;
	}

	/**
	 * Retourne vrai si l'import est en cours
	 * 
	 * @return
	 */
	public boolean isWorking() {
		return ThreadAccessControl.get(1).isOngoingThread();
	}

	/**
	 * Lancer l'import automatique dans un thread différent
	 */
	public void start() {
		if (isWorking() == false) {
			ThreadManager.runLater(this);
		}
	}

	public void addImporterListener(ImportEventListener listener) {
		listenerHandler.add(listener);
	}

	private void fireEvent(String name) {
		listenerHandler.fireEvent(new ImportEvent(name, null));
	}

	private void fireEvent(String waitingListChanged, int howManyScreenCatched) {
		ImportEvent ev = new ImportEvent(ImportEvent.WAITING_LIST_CHANGED, null);
		ev.setScreenCatchNumber(howManyScreenCatched);
		listenerHandler.fireEvent(ev);
	}

	@Override
	public ListenerHandler<ImportEventListener> getListenerHandler() {
		throw new IllegalStateException(
				"Add listener with addImporterListener()");
	}

	public int getScreenCatchNumber() {
		return screenCatchNumber;
	}

}
