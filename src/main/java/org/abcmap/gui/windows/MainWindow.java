package org.abcmap.gui.windows;

import org.abcmap.core.events.GuiManagerEvent;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.components.StatusBar;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.gui.ie.program.QuitProgram;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

/**
 * Main window of software
 */
public class MainWindow extends AbstractCustomWindow implements HasListenerHandler<GuiManagerEvent> {

    private static final CustomLogger logger = LogManager.getLogger(MainWindow.class);

    private final ListenerHandler<GuiManagerEvent> listenerHandler;
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
     * Modes and associated content panes
     */
    private HashMap<MainWindowMode, Component> modesAndComps;

    /**
     * East dock
     */
    private Dock dockE;

    /**
     * West dock
     */
    private Dock dockW;

    private JPanel mapPanel;

    private GuiManager guim;

    public MainWindow() {

        guim = MainManager.getGuiManager();

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

        listenerHandler = new ListenerHandler<>();

    }

    @Override
    public ListenerHandler<GuiManagerEvent> getListenerHandler() {
        return null;
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
            throw new NullPointerException("Composant cannot be null");
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

        if (modesAndComps == null) {
            updateModesAndComponents();
        }
        Component comp = modesAndComps.get(mode);

        if (comp == null) {
            //throw new NullPointerException("Invalid main window mode: " + mode);
            logger.warning("Invalid main window mode: " + mode);
        }

        // no changes needed, return
        if (Utils.safeEquals(getCenterComponent(), comp)) {
            return;
        }

        this.windowMode = mode;

        if (MainWindowMode.SHOW_MAP.equals(mode)) {
            setCenterComponent(mapPanel);
        } else if (MainWindowMode.SHOW_REFUSED_TILES.equals(mode)) {
            setCenterComponent(refusedTilesPanel);
        } else if (MainWindowMode.SHOW_LAYOUTS.equals(mode)) {
            setCenterComponent(layoutPanel);
        }

        this.revalidate();
        this.repaint();

        notifyWindowModeChanged();

    }

    /**
     * Lazy initialization of components
     */
    private void updateModesAndComponents() {

        modesAndComps = new HashMap<>();
        modesAndComps.put(MainWindowMode.SHOW_MAP, mapPanel);
        modesAndComps.put(MainWindowMode.SHOW_REFUSED_TILES, refusedTilesPanel);
        modesAndComps.put(MainWindowMode.SHOW_LAYOUTS, layoutPanel);

    }

    /**
     * Notify observers of window mode changes
     */
    private void notifyWindowModeChanged() {
        listenerHandler.fireEvent(new GuiManagerEvent(GuiManagerEvent.WINDOW_MODE_CHANGED, null));
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
     * Set map panel of window
     *
     * @param map
     */
    public void setMapPanel(JPanel map) {
        this.mapPanel = map;
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

    public void setWestDock(Dock dockW) {
        this.dockW = dockW;
        getContentPane().add(dockW, BorderLayout.WEST);
    }

    public void setEastDock(Dock dockE) {
        this.dockE = dockE;
        getContentPane().add(dockE, BorderLayout.EAST);
    }

}
