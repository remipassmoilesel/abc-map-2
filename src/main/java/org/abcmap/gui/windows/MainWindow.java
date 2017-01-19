package org.abcmap.gui.windows;

import org.abcmap.core.events.GuiManagerEvent;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.StatusBar;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.gui.components.map.CachedMapPane;
import org.abcmap.gui.layouts.LayoutListPanel;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.program.QuitProgram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main window of software
 */
public class MainWindow extends AbstractCustomWindow implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(MainWindow.class);

    /**
     * Current display mode of window
     */
    private MainWindowMode windowMode;

    /**
     * Status bar bottom of window
     */
    private StatusBar statusBar;

    /**
     * Main content of frame
     */
    private JPanel contentPane;

    /**
     * Panel where are displayed refused tiles
     */
    private JPanel refusedTilesPanel;

    /**
     * Panel where are displayed layouts
     */
    private JPanel layoutPanel;

    /**
     * East dock
     */
    private Dock dockE;

    /**
     * West dock
     */
    private Dock dockW;

    /**
     * Current map panel, change every time project change
     */
    private CachedMapPane mapPanel;

    /**
     * Empty panel added when no project are loaded
     */
    private JPanel emptyPanel;

    private GuiManager guim;
    private final ProjectManager projectm;
    private final EventNotificationManager notifm;

    public MainWindow() {

        guim = Main.getGuiManager();
        projectm = Main.getProjectManager();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new MainWindowListener());

        // Default size: little less than screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dim.width = dim.width - dim.width / 70;
        dim.height = dim.height - dim.height / 70;
        this.setSize(dim);

        this.setLocationRelativeTo(null);
        this.setExtendedState(MAXIMIZED_BOTH);

        contentPane = new JPanel(new BorderLayout());
        this.setContentPane(contentPane);

        statusBar = new StatusBar();
        contentPane.add(statusBar, BorderLayout.SOUTH);

        emptyPanel = new JPanel();

        // listen project manager to change content when project change
        notifm = new EventNotificationManager(this);
        projectm.getNotificationManager().addObserver(this);
        notifm.addEventListener((notif) -> {

            SwingUtilities.invokeLater(() -> {

                try {
                    // project closed, empty center
                    if (ProjectEvent.isCloseProjectEvent(notif)) {
                        setWindowMode(MainWindowMode.EMPTY);
                        logger.debug("Window mode updated");
                    }

                    // new project, show map and reset display
                    else if (ProjectEvent.isNewProjectLoadedEvent(notif)) {
                        setWindowMode(MainWindowMode.SHOW_MAP);
                        logger.debug("Window mode updated");
                    }
                } catch (Exception e) {
                    logger.debug(e);
                }

            });

        });

    }

    /**
     * Return main map panel. Can be null
     *
     * @return
     */
    public CachedMapPane getMap() {
        return mapPanel;
    }

    /**
     * Quit program on close
     */
    private class MainWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            ThreadManager.runLater(new QuitProgram());
        }
    }

    /**
     * Set center panel of window
     *
     * @param comp
     */
    private void setCenterComponent(Component comp) {

        if (comp == null) {
            throw new NullPointerException("Component cannot be null");
        }

        Component centerComp = getCenterComponent();
        if (centerComp != null) {
            contentPane.remove(centerComp);
        }

        getContentPane().add(comp, BorderLayout.CENTER);

    }

    /**
     * Return center panel of window
     *
     * @return
     */
    private Component getCenterComponent() {
        BorderLayout layout = (BorderLayout) contentPane.getLayout();
        return layout.getLayoutComponent(BorderLayout.CENTER);
    }

    /**
     * Change window mode and notify observers.
     *
     * @param mode
     */
    public void setWindowMode(MainWindowMode mode) {

        GuiUtils.throwIfNotOnEDT();

        windowMode = mode;

        if (projectm.isInitialized()) {

            Project currentProject = projectm.getProject();

            // show map in center of window
            if (MainWindowMode.SHOW_MAP.equals(mode)) {

                // component is not up to date, create a new one
                if (mapPanel == null || mapPanel.getProject().equals(currentProject) == false) {
                    mapPanel = new CachedMapPane(currentProject);
                    mapPanel.setAcceptPaintFromTool(true);
                    mapPanel.setMouseManagementEnabled(true);
                    mapPanel.setNavigationBarEnabled(true);
                }
                setCenterComponent(mapPanel);

            }

            // show refused tiles in center of window
            else if (MainWindowMode.SHOW_REFUSED_TILES.equals(mode)) {

                // component is not up to date, create a new one
                if (refusedTilesPanel == null) {
                    refusedTilesPanel = new JPanel();
                }
                setCenterComponent(refusedTilesPanel);
            }

            // show layout in center of window
            else if (MainWindowMode.SHOW_LAYOUTS.equals(mode)) {

                // component is not up to date, create a new one
                if (layoutPanel == null) {
                    layoutPanel = new LayoutListPanel(projectm.getProject());
                }
                setCenterComponent(layoutPanel);
            }

        }

        // project is not initialized, add empty panel
        else {
            mapPanel = null;
            refusedTilesPanel = null;
            layoutPanel = null;
            setCenterComponent(emptyPanel);
        }

        this.revalidate();
        this.repaint();

        notifyWindowModeChanged();

    }

    /**
     * Notify observers of window mode changes
     */
    private void notifyWindowModeChanged() {
        notifm.fireEvent(new GuiManagerEvent(GuiManagerEvent.WINDOW_MODE_CHANGED, null));
    }

    /**
     * Return current window mode: MAP, REFUSED_TILES or LAYOUTS
     *
     * @return
     */
    public MainWindowMode getWindowMode() {
        return windowMode;
    }

    /**
     * Return status bar of window, where are displayed coordinates, scale, ...
     *
     * @return
     */
    public StatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Set refused tiles panel of window
     *
     * @param refusedTilesPanel
     */
    public void setRefusedTilesPanel(JPanel refusedTilesPanel) {
        this.refusedTilesPanel = refusedTilesPanel;
    }

    /**
     * Set layout panel of window
     *
     * @param layoutPanel
     */
    public void setLayoutPanel(JPanel layoutPanel) {
        this.layoutPanel = layoutPanel;
    }

    /**
     * Return east dock of window
     *
     * @return
     */
    public Dock getEastDock() {
        return dockE;
    }

    /**
     * Return west dock of window
     *
     * @return
     */
    public Dock getWestDock() {
        return dockW;
    }

    /**
     * Set west dock of window
     *
     * @param dockW
     */
    public void setWestDock(Dock dockW) {
        this.dockW = dockW;
        getContentPane().add(dockW, BorderLayout.WEST);
    }

    /**
     * Set east dock of window
     *
     * @param dockE
     */
    public void setEastDock(Dock dockE) {
        this.dockE = dockE;
        getContentPane().add(dockE, BorderLayout.EAST);
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}
