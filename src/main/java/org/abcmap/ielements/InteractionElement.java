package org.abcmap.ielements;

import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ManagerAccessUtility;
import org.abcmap.core.managers.TempFilesManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.dock.blockitems.ClickableBlockItem;
import org.abcmap.gui.components.dock.blockitems.HideableBlockItem;
import org.abcmap.gui.components.dock.blockitems.SimpleBlockItem;
import org.abcmap.gui.menu.ClickableMenuItem;
import org.abcmap.gui.menu.DialogMenuItem;
import org.reflections.Reflections;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Interaction element represent a possible action in this software.
 * <p>
 * This action can be accessible then in menu bar, in docks, or in command search.
 * <p>
 * This abstract class contains many utility used to execute user actions.
 */
public abstract class InteractionElement extends ManagerAccessUtility implements Runnable, ActionListener, HasEventNotificationManager {

    protected static final CustomLogger logger = LogManager.getLogger(TempFilesManager.class);

    /**
     * General purpose lock
     * <p>
     * Use getOperationLock() to userActionLock it and display a message if lock is refused
     */
    private final ReentrantLock operationLock;

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

    protected EventNotificationManager notifm;

    private String wrap15 = "wrap 15, ";
    private String gapLeft = "gapleft 15px, ";

    protected InteractionElement() {

        this.label = "no label";
        this.help = null;
        this.accelerator = null;
        this.noSearchMessage = null;
        this.displayInHideableElement = false;
        this.hiddenInDetachedWIndows = false;
        this.onlyDebugMode = false;

        // display as a simple clickable element by default
        this.displaySimplyInSearch = true;

        this.notifm = new EventNotificationManager(this);

        this.operationLock = new ReentrantLock();

    }

    protected String wrap15() {
        return wrap15;
    }

    protected String gapLeft() {
        return gapLeft;
    }

    /**
     * Try to get userActionLock. If locked, return true. If not return false and show a message to user.
     *
     * @return
     */
    protected boolean getOperationLock() {

        if (operationLock.tryLock() == false) {
            dialm().showErrorInBox("Cette opération est déjà en cours, veuillez patienter...");
            return false;
        }

        return true;
    }

    /**
     * Return current project if initialized.
     * <p>
     * If not, show a message to user and return null
     *
     * @return
     */
    protected Project getCurrentProjectOrShowMessage() {

        if (projectm().isInitialized() == false) {
            dialm().showErrorInBox("Vous devez d'abord ouvrir ou créer un projet.");
            return null;
        }

        return projectm().getProject();
    }

    /**
     * Release user lock.
     */
    protected void releaseOperationLock() {
        operationLock.unlock();
    }

    /**
     * If return true, this element should be available only in debug mode
     *
     * @return
     */
    public boolean isOnlyDebugMode() {
        return onlyDebugMode;
    }

    public String getNoSearchMessage() {
        return noSearchMessage;
    }

    /**
     * Return a simple GUI, without any container
     * <p>
     * This GUI should be wrapped in search result, dock container, ...
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
    public EventNotificationManager getNotificationManager() {
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
                if (ie.isOnlyDebugMode() == false || (ie.isOnlyDebugMode() && Main.isDebugMode())) {
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
