package abcmap.gui.ie;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import abcmap.managers.stub.MainManager;
import org.reflections.Reflections;

import abcmap.configuration.ConfigurationConstants;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.dock.comps.blockitems.ClickableBlockItem;
import abcmap.gui.dock.comps.blockitems.HideableBlockItem;
import abcmap.gui.dock.comps.blockitems.SimpleBlockItem;
import abcmap.gui.menu.ClickableMenuItem;
import abcmap.gui.menu.DialogMenuItem;
import abcmap.managers.CancelManager;
import abcmap.managers.ClipboardManager;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.DrawManager;
import abcmap.managers.GuiManager;
import abcmap.managers.ImportManager;
import abcmap.managers.Log;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.RecentManager;
import abcmap.managers.ShortcutManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

/**
 * Element d' interaction: objet permettant une action avec nom, icone,
 * 
 * @author remipassmoilesel
 *
 */
public abstract class InteractionElement implements Runnable, ActionListener,
		HasNotificationManager {

	/** Nom de l'element */
	protected String label;

	/** Aide succincte sur l'element, possiblement null */
	protected String help;

	/** Icone a afficher dans un dock */
	protected ImageIcon blockIcon;

	/** Icone a afficher dans la barre de menu */
	protected ImageIcon menuIcon;

	/** Raccourcis clavier */
	protected KeyStroke accelerator;

	/** Affichage pendant une recherche: si 'false' afficher dans une boite */
	protected boolean displaySimplyInSearch;

	/**
	 * Si vrai, l'element sera montré de préférence dans un element masquable
	 */
	protected boolean displayInHideableElement;

	/** Message à afficher si pas d'affichage de recherche */
	protected String noSearchMessage;

	protected static final String DEFAULT_NO_SEARCH_MESSAGE = "Vous ne pouvez pas utiliser cette "
			+ "commande directement à partir du menu de recherche. Cette commande est présente "
			+ "dans le logiciel, reportez vous à la documentation pour plus de précision.";

	protected static final String SUBMENU_NO_SEARCH_MESSAGE = "Ce sous-menu n'est pas directement "
			+ "accessible de puis la recherche. Vous le trouverez dans la barre de menu haute "
			+ "ou dans les menus de coté du logiciel.";

	/** GUI adapté à l'affichage en menu SWING */
	private Component menuGUI;

	/** GUI adapté à l'affichage en dock */
	private Component blockGUI;

	/**
	 * GUI primaire, possiblement null, contient les elements necessaires au
	 * lancement de l'action
	 */
	private Component primaryGUI;

	/** Si vrai n'est affiché qu'en mode de debuggage */
	protected boolean onlyDebugMode;

	/** Le dernier evenement Swing reçu */
	protected ActionEvent lastActionEvent;

	/** Si vrai l'élement sera masqué dans une fenêtre détachée */
	protected boolean hiddenInDetachedWIndows;

	/** Contrainte pour miglayout */
	protected String wrap15;

	/** Contrainte pour miglayout */
	protected String gapLeft;

	/** Controle d'accés pour threads */
	protected ThreadAccessControl threadAccess;

	/** Gestionnaire de projets */
	protected ProjectManager projectm;

	/** Gestionnaire d'interface */
	protected GuiManager guim;

	/** Gestionnaire de dessin */
	protected DrawManager drawm;

	/** Raccourcis clavier */
	protected ShortcutManager shortcuts;

	/** Fichiers récents */
	protected RecentManager recentsm;

	/** Gestionnaire de configuration */
	protected ConfigurationManager configm;

	/** Gestionnaire de carte */
	protected MapManager mapm;

	/** Gestionnaire de presse papier */
	protected ClipboardManager clipboardm;

	/** Gestionnaire d'annulation */
	protected CancelManager cancelm;

	/** Gestionnaire d'import */
	protected ImportManager importm;

	/** Gestionnaire de notification */
	protected NotificationManager notifm;

	protected InteractionElement() {

		projectm = MainManager.getProjectManager();
		guim = MainManager.getGuiManager();
		drawm = MainManager.getDrawManager();
		shortcuts = MainManager.getShortcutManager();
		recentsm = MainManager.getRecentManager();
		configm = MainManager.getConfigurationManager();
		mapm = MainManager.getMapManager();
		clipboardm = MainManager.getClipboardManager();
		cancelm = MainManager.getCancelManager();
		importm = MainManager.getImportManager();

		this.label = "no label";
		this.help = null;
		this.accelerator = null;
		this.noSearchMessage = null;
		this.displayInHideableElement = false;
		this.hiddenInDetachedWIndows = false;
		this.onlyDebugMode = false;

		// par defaut, afficher de manière simple comme element clickable
		this.displaySimplyInSearch = true;

		// gestionnaire d'evenements
		this.notifm = new NotificationManager(this);

		// gestion des accés aux opérations longues
		this.threadAccess = new ThreadAccessControl();
		threadAccess.setDefaultAccessTimeOut(10000);
		threadAccess.setRefusedAccessAction(new Runnable() {
			@Override
			public void run() {
				guim.showErrorInBox("Cette opération est déjà en cours, veuillez patienter.");
			}
		});

		// contraintes pour layouts
		wrap15 = "wrap 15, ";
		gapLeft = "gapleft 15px, ";

	}

	public boolean isOnlyDebugMode() {
		return onlyDebugMode;
	}

	public String getNoSearchMessage() {
		return noSearchMessage;
	}

	/**
	 * Retourne un element d'IHM simple, sans conteneur.<br>
	 * 
	 * @return
	 */
	public final Component getPrimaryGUI() {
		if (primaryGUI == null) {
			primaryGUI = createPrimaryGUI();
		}
		return primaryGUI;
	}

	/**
	 * Retourne un element d'IHM adaptés aux docks.<br>
	 * 
	 * @return
	 */
	public final Component getBlockGUI() {
		if (blockGUI == null) {
			blockGUI = createBlockGUI();
		}
		return blockGUI;
	}

	/**
	 * Retourne un element d'IHM adapté auw menus Swing.
	 * 
	 * @return
	 */
	public final Component getMenuGUI() {
		if (menuGUI == null) {
			menuGUI = createMenuGUI();
		}

		return menuGUI;
	}

	/**
	 * A overrider au besoin
	 * 
	 * @return
	 */
	protected Component createPrimaryGUI() {
		return null;
	}

	/**
	 * Retourne un IHM adapté aux dock
	 * 
	 * @return
	 */
	protected Component createBlockGUI() {

		// tenter de créer l'interface primaire
		if (primaryGUI == null) {
			primaryGUI = createPrimaryGUI();
		}

		// l'interface primaire est nulle, retourner un element clicable
		if (primaryGUI == null) {
			return new ClickableBlockItem(this);
		}

		// montrer dans un element masquable
		if (displayInHideableElement) {
			return HideableBlockItem.create(this, getPrimaryGUI(), false);
		}

		// oudans un element simple par defaut
		else {
			return SimpleBlockItem.create(this, primaryGUI);
		}
	}

	/**
	 * Méthode à overrider pour implémenter une interface.
	 * 
	 * @return
	 */
	protected Component createMenuGUI() {

		// tenter de créer l'interface primaire
		if (primaryGUI == null) {
			primaryGUI = createPrimaryGUI();
		}

		// l'interface primaire est nulle, retourner un element clicable
		if (primaryGUI == null) {
			return new ClickableMenuItem(this);
		}

		// sinon, retourner un element simple par defaut
		return new DialogMenuItem(this);

	}

	/**
	 * Action par default de l'element. Méthode à overrider pour attribuer une
	 * action.
	 */
	@Override
	public void run() {

	}

	public ImageIcon getMenuIcon() {
		return menuIcon;
	}

	public String getLabel() {
		return label;
	}

	public String getHelp() {
		return help;
	}

	public ImageIcon getBlockIcon() {
		return blockIcon;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public KeyStroke getAccelerator() {
		return accelerator;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.lastActionEvent = e;
		ThreadManager.runLater(this);
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	public boolean isDisplayableSimpleInSearch() {
		return displaySimplyInSearch;
	}

	/**
	 * Retourne une instance de chaque element d'interaction disponible dans le
	 * package ConfigurationConstants.IE_PACKAGE_ROOT.
	 * <p>
	 * <b>Varie selon si le programme est en mode debuggage ou non.
	 * 
	 * @return
	 */
	public static ArrayList<InteractionElement> getAllAvailablesInteractionElements() {
		return getIEFromPackage(ConfigurationConstants.IE_PACKAGE_ROOT);
	}

	/**
	 * Retourne une instance de chaque element d'interaction disponible dans le
	 * package de plugins
	 * <p>
	 * <b>Varie selon si le programme est en mode debuggage ou non.
	 * 
	 * @return
	 */
	public static ArrayList<InteractionElement> getAllAvailablesPlugins() {
		return getIEFromPackage(ConfigurationConstants.PLUGINS_PACKAGE_ROOT);
	}

	/**
	 * Liste tous les élements d'interaction d'un package et retourne une liste
	 * d'instances.
	 * 
	 * @return
	 */
	private static ArrayList<InteractionElement> getIEFromPackage(String pkgname) {

		// lister toutes les classes disponibles
		Reflections reflections = new Reflections(pkgname);
		Set<Class<? extends InteractionElement>> classes = reflections
				.getSubTypesOf(InteractionElement.class);

		// instancier les classes
		ArrayList<InteractionElement> ielements = new ArrayList<InteractionElement>();
		for (Class<? extends InteractionElement> cl : classes) {

			// ignorer les classes abstraites
			if (Modifier.isAbstract(cl.getModifiers()))
				continue;

			// ignorer les classes internes non statiques
			if (cl.isMemberClass()
					&& Modifier.isStatic(cl.getModifiers()) == false)
				continue;

			// sinon essayer d'instancier
			try {

				InteractionElement ie = InteractionElement
						.getSimpleInstanceOf(cl);
				if (ie.isOnlyDebugMode() == false
						|| (ie.isOnlyDebugMode() && MainManager
								.isDebugMode())) {
					ielements.add(ie);
				}

			} catch (InstantiationException e) {
				Log.error(e);
			}
		}

		return ielements;
	}

	/**
	 * Retourne une instance simple ou null
	 * 
	 * @param cl
	 * @return
	 * @throws InstantiationException
	 */
	public static InteractionElement getSimpleInstanceOf(
			Class<? extends InteractionElement> cl)
			throws InstantiationException {

		// classe abstraite: erreur
		if (Modifier.isAbstract(cl.getModifiers())) {
			throw new InstantiationException("Abstract class: " + cl.getName());
		}

		// ignorer les classes internes
		if (cl.isMemberClass() && Modifier.isStatic(cl.getModifiers()) == false) {
			throw new InstantiationException("Nested class: " + cl.getName());
		}

		InteractionElement instance = null;
		try {
			// tentative de construction simple
			instance = cl.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new InstantiationException("Error with: " + cl.getName()
					+ " - " + e.getMessage());
		}

		return instance;
	}

	/**
	 * Vérifie que le porjet soit bien initialisé, et retourne la calque actif.
	 * <p>
	 * Si le projet n'est pas initialisé affiche un message d'erreur et retourne
	 * null.
	 * 
	 * @return
	 */
	protected MapLayer checkProjectAndGetActiveLayer() {

		// le projet est initialisé, retourner le calque actif
		if (projectm.isInitialized()) {
			try {
				return projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.error(e);
			}
		}

		// le projet n'est pas initialisé, afficher un message d'erreur et
		// retourner null.
		guim.showProjectNonInitializedError();

		return null;

	}

	/**
	 * Retourne la dernière chaine getActionCommand() ou null.
	 * 
	 * @return
	 */
	public String getLastActionCommand() {
		String action = lastActionEvent != null ? lastActionEvent
				.getActionCommand() : null;
		return action;
	}

	public boolean isHiddenInDetachedWindow() {
		return hiddenInDetachedWIndows;
	}

}
