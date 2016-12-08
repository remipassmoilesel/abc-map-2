package org.abcmap.gui.ie;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.core.threads.ThreadManager;
import org.reflections.Reflections;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


/**
 * Interaction element represent a possible action in this software. This action can be accessible then in menu bar, in docks, or in command search.
 */
public abstract class InteractionElement implements Runnable, ActionListener, HasNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(TempFilesManager.class);

    /**
     * Name of element
     */
    protected String label;

    /**
     * One line help to describe action
     */
    protected String help;

    /**
     * Icon to display in dock
     */
    protected ImageIcon blockIcon;

    /**
     * Icon to display in menu bars
     */
    protected ImageIcon menuIcon;

    /**
     * Keyboard shortcut associated with
     */
    protected KeyStroke accelerator;

    /**
     * If set to true, this element should be display in command search forms as a simple clickable element. If set to false, it can contains
     * a more complex GUI
     */
    protected boolean displaySimplyInSearch;

    /**
     * If set to true, this element will be displayed in a hideable block
     */
    protected boolean displayInHideableElement;

    /**
     * If command search display is disabled, this message will be shown
     */
    protected String noSearchMessage;

    protected static final String DEFAULT_NO_SEARCH_MESSAGE = "Vous ne pouvez pas utiliser cette "
            + "commande directement à partir du menu de recherche. Cette commande est présente "
            + "dans le logiciel, reportez vous à la documentation pour plus de précision.";

    protected static final String SUBMENU_NO_SEARCH_MESSAGE = "Ce sous-menu n'est pas directement "
            + "accessible de puis la recherche. Vous le trouverez dans la barre de menu haute "
            + "ou dans les menus de coté du logiciel.";

    /**
     * GUI designed to be displayed in JMenu
     */
    private Component menuGUI;

    /**
     * GUI designed to be displayed in Docks
     */
    private Component blockGUI;

    /**
     * Primary GUI is normally a JPanel with needed controls. It should be wrapped in other Swing components.
     */
    private Component primaryGUI;

    /**
     * If set to true, this element will be shown only in debug mode
     */
    protected boolean onlyDebugMode;

    /**
     * Last action event received is stored here
     */
    protected ActionEvent lastActionEvent;

    /**
     * If set to true, element will be hidden in detachable windows
     */
    protected boolean hiddenInDetachedWIndows;

    protected String wrap15 = "wrap 15, ";
    protected String gapLeft = "gapleft 15px, ";

    protected ProjectManager projectm;
    protected GuiManager guim;
    protected DrawManager drawm;
    protected ShortcutManager shortcuts;
    protected RecentManager recentsm;
    protected ConfigurationManager configm;
    protected MapManager mapm;
    protected ClipboardManager clipboardm;
    protected CancelManager cancelm;
    protected ImportManager importm;
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

        // display as a simple clickable element by default
        this.displaySimplyInSearch = true;

        // gestionnaire d'evenements
        this.notifm = new NotificationManager(this);

        /**
         // gestion des accés aux opérations longues
         this.threadAccess = new ThreadAccessControl();
         threadAccess.setDefaultAccessTimeOut(10000);
         threadAccess.setRefusedAccessAction(new Runnable() {
        @Override public void run() {
        guim.showErrorInBox("Cette opération est déjà en cours, veuillez patienter.");
        }
        });
         */

    }

    public boolean isOnlyDebugMode() {
        return onlyDebugMode;
    }

    public String getNoSearchMessage() {
        return noSearchMessage;
    }

    /**
     * Return a simple GUI, without container
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
     * Return a GUI element designed to be used in docks
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
     * Return a GUI element designed to be used in menu
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
     * Optionally override this method to create a custom primary GUI
     *
     * @return
     */
    protected Component createPrimaryGUI() {
        return null;
    }

    /**
     * Create a GUI designed to be used in docks
     *
     * @return
     */
    protected Component createBlockGUI() {

        if (primaryGUI == null) {
            primaryGUI = createPrimaryGUI();
        }

        // if primary GUI is null create a default GUI
        if (primaryGUI == null) {
            return new ClickableBlockItem(this);
        }

        if (displayInHideableElement) {
            return HideableBlockItem.create(this, getPrimaryGUI(), false);
        } else {
            return SimpleBlockItem.create(this, primaryGUI);
        }
    }

    /**
     * Create default menu GUI
     *
     * @return
     */
    protected Component createMenuGUI() {

        if (primaryGUI == null) {
            primaryGUI = createPrimaryGUI();
        }

        // if primary GUI is null create a default GUI
        if (primaryGUI == null) {
            return new ClickableMenuItem(this);
        }

        return new DialogMenuItem(this);

    }

    /**
     * Override this method to add a default action to element
     */
    @Override
    public void run() {

    }

    /**
     * Return menu icon of element
     *
     * @return
     */
    public ImageIcon getMenuIcon() {
        return menuIcon;
    }

    /**
     * Return label of element
     *
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return one line help of element
     *
     * @return
     */
    public String getHelp() {
        return help;
    }

    /**
     * Return block icon of element
     *
     * @return
     */
    public ImageIcon getBlockIcon() {
        return blockIcon;
    }

    /**
     * Set label of element
     *
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Return keyboard shortcut of element
     *
     * @return
     */
    public KeyStroke getAccelerator() {
        return accelerator;
    }

    /**
     * un default action of element if an action event is received
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        this.lastActionEvent = e;
        ThreadManager.runLater(this);
    }

    @Override
    public NotificationManager getNotificationManager() {
        return notifm;
    }

    /**
     * Return true if this element should appear in command search
     *
     * @return
     */
    public boolean isDisplayableSimpleInSearch() {
        return displaySimplyInSearch;
    }

    /**
     * Return one instance of each interaction element available
     *
     * @return
     */
    public static ArrayList<InteractionElement> getAllAvailablesInteractionElements() {
        return getIEFromPackage(ConfigurationConstants.IE_PACKAGE_ROOT);
    }

    /**
     * Return one instance of each plugin element available
     *
     * @return
     */
    public static ArrayList<InteractionElement> getAllAvailablesPlugins() {
        return getIEFromPackage(ConfigurationConstants.PLUGINS_PACKAGE_ROOT);
    }

    /**
     * List all interaction elements of a package and return a list of instances
     *
     * @param pkgname
     * @return
     */
    private static ArrayList<InteractionElement> getIEFromPackage(String pkgname) {

        // get all classes availables
        Reflections reflections = new Reflections(pkgname);
        Set<Class<? extends InteractionElement>> classes = reflections
                .getSubTypesOf(InteractionElement.class);

        // instantiate classes
        ArrayList<InteractionElement> ielements = new ArrayList<InteractionElement>();
        for (Class<? extends InteractionElement> cl : classes) {

            // ignore abstract classes
            if (Modifier.isAbstract(cl.getModifiers()))
                continue;

            // ignore internal non static classes
            if (cl.isMemberClass() && Modifier.isStatic(cl.getModifiers()) == false) {
                continue;
            }

            try {
                InteractionElement ie = InteractionElement.getSimpleInstanceOf(cl);
                if (ie.isOnlyDebugMode() == false || (ie.isOnlyDebugMode() && MainManager.isDebugMode())) {
                    ielements.add(ie);
                }
            } catch (InstantiationException e) {
                logger.error(e);
            }
        }

        return ielements;
    }

    /**
     * Return a simple instance of specified class
     *
     * @param cl
     * @return
     * @throws InstantiationException
     */
    public static InteractionElement getSimpleInstanceOf(Class<? extends InteractionElement> cl) throws InstantiationException {

        if (Modifier.isAbstract(cl.getModifiers())) {
            throw new InstantiationException("Abstract class: " + cl.getName());
        }

        if (cl.isMemberClass() && Modifier.isStatic(cl.getModifiers()) == false) {
            throw new InstantiationException("Nested class: " + cl.getName());
        }

        InteractionElement instance = null;
        try {
            instance = cl.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new InstantiationException("Error with: " + cl.getName() + " - " + e.getMessage());
        }

        return instance;
    }

    /**
     * Check if project is initialized and return active layer. If project is not initialized, display an error message to user.
     *
     * @return
     */
    protected AbstractLayer checkProjectAndGetActiveLayer() {

        /*
        // le projet est initialisé, retourner le calque actif
        if (projectm.isInitialized()) {
            try {
                return projectm.getActiveLayer();
            } catch (MapLayerException e) {
                logger.error(e);
            }
        }

        // le projet n'est pas initialisé, afficher un message d'erreur et
        // retourner null.
        guim.showProjectNonInitializedError();

        */

        return null;

    }

    /**
     * Return the last action command string received or null
     *
     * @return
     */
    public String getLastActionCommand() {
        String action = lastActionEvent != null ? lastActionEvent.getActionCommand() : null;
        return action;
    }

    /**
     * Return true if this element should be hidden in detached window
     *
     * @return
     */
    public boolean isHiddenInDetachedWindow() {
        return hiddenInDetachedWIndows;
    }

}
