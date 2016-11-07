package abcmap.managers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import abcmap.events.GuiManagerEvent;
import abcmap.events.ProjectEvent;
import abcmap.gui.GuiBuilder;
import abcmap.gui.GuiColors;
import abcmap.gui.GuiCursor;
import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.comps.messagebox.MessageBoxManager;
import abcmap.gui.comps.progressbar.HasProgressbarManager;
import abcmap.gui.comps.progressbar.ProgressbarManager;
import abcmap.gui.dialogs.SupportProjectDialog;
import abcmap.gui.dialogs.simple.InformationTextFieldDialog;
import abcmap.gui.dialogs.simple.SimpleErrorDialog;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.windows.DetachedWindow;
import abcmap.gui.windows.MainWindow;
import abcmap.gui.windows.MainWindowMode;
import abcmap.gui.windows.crop.CropConfigurationWindow;
import abcmap.managers.stub.MainManager;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;
import abcmap.utils.threads.ThreadErrorListener;
import abcmap.utils.threads.ThreadManager;

public class GuiManager implements HasNotificationManager {

	public enum Windows {
		/** Fenêtre principale */
		MAIN,

		/** Fenêtre d'import manuel */
		ROBOT_IMPORT,

		/** Fenêtre d'import automatique */
		MANUAL_IMPORT,

		/** Fenêtre de configuration de recadrage */
		CROP_CONFIG,

		/** Fenêtre d'affichage d'assistant */
		DETACHED_WIZARD,

	}

	/** Opérations lancées après la création de l'interface */
	private ArrayList<Runnable> initialisationOperations;

	/** Boite d'affichage de messages */
	private MessageBoxManager messagebox;

	/** La carte, créé ici mais accessible à partir du MapManager */
	private Component map;

	/** La liste de toutes les fenêtres du programme */
	private HashMap<Windows, JFrame> registeredWindows;

	private ProjectManager projectm;
	private NotificationManager notifm;

	public GuiManager() {

		this.projectm = MainManager.getProjectManager();
		this.notifm = new NotificationManager(this);
		notifm.setDefaultUpdatableObject(new GuiUpdater());

		initialisationOperations = new ArrayList<>();

		// ecouter les erreurs en provenance du ThreadManager
		ThreadManager.getListenerHandler().add(new ThreadErrorListener());

		registeredWindows = new HashMap<Windows, JFrame>();
	}

	public void registerWindow(Windows name, JFrame w) {
		registeredWindows.put(name, w);
	}

	/**
	 * Mise à jour du GUI en fonctions d'evenements reçus.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class GuiUpdater implements UpdatableByNotificationManager {

		@Override
		public void notificationReceived(Notification arg) {

			// Au chargement d'un projet, renommer toutes les fenêtres
			if (ProjectEvent.isNewProjectLoadedEvent(arg)) {
				getMainWindow().setTitle(
						projectm.getRealPath().getAbsolutePath());
			}

		}
	}

	/**
	 * Retourne le dock Est de la fenêtre principale.
	 * 
	 * @return
	 */
	public Dock getEastDock() {
		return getMainWindow().getEastDock();
	}

	/**
	 * Retourne le dock West de la fenêtre principale.
	 * 
	 * @return
	 */
	public Dock getWestDock() {
		return getMainWindow().getWestDock();
	}

	/**
	 * Retourne une référence vers la fenêtre d'import manuel
	 * 
	 * @return
	 */
	public Window getManualImportWindow() {
		return registeredWindows.get(Windows.MANUAL_IMPORT);
	}

	/**
	 * Retourne une référence vers la fenêtre d'import automatique
	 * 
	 * @return
	 */
	public Window getAutoImportWindow() {
		return registeredWindows.get(Windows.ROBOT_IMPORT);
	}

	/**
	 * Renvoi une référence vers toutes les fenêtres du programme. Une référence
	 * peut être nulle. Toutes les fenêtres sont instanciées une seule fois.
	 * 
	 * @return
	 */
	public Collection<JFrame> getAllWindows() {
		return registeredWindows.values();
	}

	/**
	 * Ajouter une operations qui serait effectuée après que le GUI ai été
	 * construit et rendu visible.
	 * <p>
	 * Tout est éxécuté sur l'EDT.
	 * 
	 * @param operation
	 */
	public void addInitialisationOperation(Runnable operation) {
		initialisationOperations.add(operation);
	}

	/**
	 * Executer les operations d'initialisation sur l'EDT
	 */
	private void runIntialisationOperations() {

		GuiUtils.throwIfNotOnEDT();

		for (Runnable runnable : initialisationOperations) {
			runnable.run();
		}

	}

	/**
	 * Construire le GUI du programme
	 */
	public void constructGui() {

		GuiUtils.throwIfNotOnEDT();

		// Creer tout le gui
		GuiBuilder gb = new GuiBuilder();
		gb.constructGui();

		// récupérer les objets créés
		gb.registerWindows();

		map = gb.getMap();

		// mode de fenêtre par défaut
		setMainWindowMode(MainWindowMode.SHOW_MAP);

	}

	/**
	 * Construire et afficher le GUI du programme
	 * 
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public void constructAndShowGui() throws InvocationTargetException,
			InterruptedException {

		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {

				constructGui();

				// montrer la fenetre principale
				getMainWindow().setVisible(true);

				runIntialisationOperations();
			}

		});
	}

	/**
	 * Afficher un message dans une boite de couleur d'information.
	 * <p>
	 * Threadsafe
	 * 
	 * @param message
	 */
	public void showMessageInBox(String message) {
		showMessageInBox(null, message, GuiColors.INFO_BOX_BACKGROUND);
	}

	/**
	 * Afficher un message dans une boite de couleur d'erreur.
	 * <p>
	 * Threadsafe
	 * 
	 * @param message
	 */
	public void showErrorInBox(String message) {
		showMessageInBox(null, message, GuiColors.ERROR_BOX_BACKGROUND);
	}

	/**
	 * Afficher un message dans une boite de couleur d'information.
	 * <p>
	 * Threadsafe
	 * 
	 * @param message
	 */
	public void showMessageInBox(Integer timeMilliSec, String message,
			Color background) {

		// verifier le gestionnaire de message
		if (messagebox == null) {
			messagebox = new MessageBoxManager(getMainWindow());
		}

		// verifier le temps d'affichage
		if (timeMilliSec == null)
			timeMilliSec = messagebox.getDefaultTime();

		// couleur de fond
		messagebox.setBackgroundColor(background);

		// affichage
		messagebox.showMessage(timeMilliSec, message);
	}

	/**
	 * Configure le gestionnaire de rendu de GUI
	 */
	public void configureUiManager() {

		// apparence du systeme par defaut ou métal
		GuiUtils.configureUIManager(UIManager.getSystemLookAndFeelClassName());

		// police par defaut
		GuiUtils.setDefaultUIFont(GuiStyle.DEFAULT_SOFTWARE_FONT);

	}

	/**
	 * Affiche une boite de dialogue de soutien du projet
	 * 
	 * @param parent
	 */
	public void showSupportDialog(Window parent) {

		GuiUtils.throwIfNotOnEDT();

		SupportProjectDialog dial = new SupportProjectDialog(parent);
		dial.setVisible(true);

	}

	/**
	 * Affiche une boite de dialogue de soutien du projet
	 * 
	 * @param parent
	 */
	public void showSupportDialogAndWait(final Window parent) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					showSupportDialog(parent);
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}

	}

	/**
	 * Masquer toutes les fenêtres sur le thread appelant.
	 * 
	 * @param val
	 * @throws InvocationTargetException
	 * @throws InterruptedException
	 */
	public void setAllWindowVisibles(final boolean val)
			throws InvocationTargetException, InterruptedException {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					for (final Window w : getAllWindows()) {
						if (w != null) {
							w.setVisible(val);
						}
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			Log.error(e);
		}

	}

	/**
	 * Montre la fenêtre passée en argument et cache toutes les autres.
	 * 
	 * @param winToShow
	 */
	public void showOnlyWindow(Window winToShow) {

		GuiUtils.throwIfNotOnEDT();

		// lister toutes les fenêtres
		Collection<JFrame> allWindows = getAllWindows();

		// verifier la fenêtre
		if (allWindows.contains(winToShow) == false) {
			throw new IllegalArgumentException("Unknown window: "
					+ winToShow.getClass().getName() + " " + winToShow);
		}

		// cacher toutes les fenêtres
		for (Window w : allWindows) {
			if (w.isVisible() == false) {
				continue;
			}
			if (winToShow.equals(w) == false
					&& getWizardDetachedWindow().equals(w) == false) {
				w.setVisible(false);
			}
		}

		// afficher la fenetre si necessaire
		if (winToShow.isVisible() == false) {
			winToShow.setVisible(true);
		}

	}

	/**
	 * Renvoi une référence vers la fenetre principale du logiciel
	 */
	public MainWindow getMainWindow() {
		return (MainWindow) registeredWindows.get(Windows.MAIN);
	}

	/**
	 * Renvoi une référence de toutes les fenêtres visibles du logiciel
	 * 
	 * @return
	 */
	public ArrayList<Component> getVisibleWindows() {

		// liste des fenetres a verifier
		Collection<JFrame> toCheck = getAllWindows();

		// liste a retourner
		ArrayList<Component> output = new ArrayList<Component>();

		// verification avec eventuel ajout à la liste
		for (Component comp : toCheck) {
			if (comp != null && comp.isVisible()) {
				output.add(comp);
			}
		}

		return output;
	}

	/**
	 * Retourne une référence vers la barre de statut de la fenêtre principale
	 * 
	 * @return
	 */
	public ProgressbarManager getStatusProgressBar() {
		return getMainWindow().getStatusBar().getProgressbarManager();
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showProfileWritingError() {
		showErrorInBox("Erreur lors de l'enregistrement du profil de configuration.");
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showProjectWritingError() {
		showErrorInBox("Erreur lors de l'enregistrement du projet.");
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showProjectNonInitializedError() {
		String message = "Vous devez d'abord créer ou ouvrir un projet avant de poursuivre.";
		showErrorInBox(message);
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showOperationAlreadyRunningError() {
		String message = "Cette opération est déjà en cours. Veuillez patienter.";
		showErrorInBox(message);
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showProjectWithoutLayoutError() {
		String message = "Vous devez d'abord mettre en page votre projet avant de pouvoir continuer.";
		showErrorInBox(message);
	}

	/**
	 * Message d'erreur prédéfini
	 */
	public void showErrorInDialog(Window parent, String message, boolean wait) {

		if (wait) {
			GuiUtils.throwIfOnEDT();
			SimpleErrorDialog.showAndWait(parent, message);
		}

		else {
			SimpleErrorDialog.showLater(null, message);
		}

	}

	/**
	 * Retourne une référence vers le panneau de la carte. Préférrer l'accès par
	 * le MapManager.
	 * 
	 * @return
	 */
	@Deprecated
	public Component getMap() {
		return map;
	}

	/**
	 * Curseur prédéfini
	 * 
	 * @return
	 */
	public Cursor getDrawingCursor() {
		return GuiCursor.CROSS_CURSOR;
	}

	/**
	 * Curseur prédéfini
	 * 
	 * @return
	 */
	public Cursor getNormalCursor() {
		return GuiCursor.NORMAL_CURSOR;
	}

	/**
	 * Curseur prédéfini
	 * 
	 * @return
	 */
	public Cursor getMoveCursor() {
		return GuiCursor.MOVE_CURSOR;
	}

	/**
	 * Curseur prédéfini
	 * 
	 * @return
	 */
	public Cursor getClickableCursor() {
		return GuiCursor.HAND_CURSOR;
	}

	/**
	 * Affecte l'icone du logiciel à une fenêtre
	 * 
	 * @param window
	 */
	public void setWindowIconFor(Window window) {
		window.setIconImage(GuiIcons.WINDOW_ICON.getImage());
	}

	/**
	 * Afficher un dialogue d'information
	 * 
	 * @param parent
	 * @param message
	 * @param textFieldValue
	 */
	public void showInformationTextFieldDialog(Window parent, String message,
			String textFieldValue) {
		InformationTextFieldDialog.showLater(parent, message, textFieldValue);
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	/**
	 * Afficher la fenêtre de recadrage
	 * 
	 * @param image
	 */
	public void showCropWindow(BufferedImage image) {

		GuiUtils.throwIfNotOnEDT();

		getCropWindow().setImage(image);
		getCropWindow().setVisible(true);
	}

	/**
	 * Renvoyer une référence vers la fenêtre de recadrage
	 * 
	 * @return
	 */
	public CropConfigurationWindow getCropWindow() {
		return (CropConfigurationWindow) registeredWindows
				.get(Windows.CROP_CONFIG);
	}

	public DetachedWindow getRobotImportWindow() {
		return (DetachedWindow) registeredWindows.get(Windows.ROBOT_IMPORT);
	}

	public ProgressbarManager getRobotWindowProgressBar() {
		return ((HasProgressbarManager) registeredWindows
				.get(Windows.ROBOT_IMPORT)).getProgressbarManager();
	}

	/**
	 * Retourne le mode d'affichage de la fenêtre principale.
	 * 
	 * @return
	 */
	public MainWindowMode getMainWindowMode() {
		return getMainWindow().getWindowMode();
	}

	/**
	 * Change le mode d'affichage de la fenetre principale
	 * 
	 * @param mode
	 */
	public void setMainWindowMode(MainWindowMode mode) {

		getMainWindow().setWindowMode(mode);

		notifyWindowModeChanged();
	}

	public void notifyWindowModeChanged() {
		notifm.fireEvent(new GuiManagerEvent(
				GuiManagerEvent.WINDOW_MODE_CHANGED, null));
	}

	public void showGroupInDock(String className) {

		// lister les docks disponibles
		Dock[] docks = new Dock[] { getEastDock(), getWestDock() };

		for (int i = 0; i < docks.length; i++) {

			// essayer d'afficher le groupe
			Dock d = docks[i];
			boolean r = d.showFirstMenuPanelCorrespondingThis(className);

			// groupe affiché, retour
			if (r) {
				return;
			}
		}

		// pas d'affichage, erreur
		throw new IllegalStateException("Unable to show: " + className);
	}

	/**
	 * Tente d'afficher le groupe passé en paramètre dans l'un des docks du
	 * logiciel. Si aucun affichage, une exception est levée.
	 * 
	 * @param ieGroup
	 */
	public void showGroupInDock(Class<? extends InteractionElementGroup> ieGroup) {
		showGroupInDock(ieGroup.getSimpleName());
	}

	public DetachedWindow getWizardDetachedWindow() {
		return (DetachedWindow) registeredWindows.get(Windows.DETACHED_WIZARD);
	}

}
