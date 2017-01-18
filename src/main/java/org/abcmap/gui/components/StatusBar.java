package org.abcmap.gui.components;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.MapManagerEvent;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.Event;
import org.abcmap.core.events.manager.EventListener;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.MapManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.gui.components.progressbar.HasProgressbarManager;
import org.abcmap.gui.components.progressbar.ProgressbarManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StatusBar extends JPanel implements HasEventNotificationManager, HasProgressbarManager {

    /**
     * If true display errors
     */
    private boolean showErrors = false;

    private EventNotificationManager notifm;

    /**
     * Display degrees coordinates
     */
    private JLabel labelPosition;

    /**
     * Display zoom or scale
     */
    private JLabel labelScale;

    /**
     * Main progressbar of software
     */
    private ProgressbarManager progressbarManager;

    /**
     * If true, status bar watch mouse position
     */
    private boolean mouseListening;

    private MousePositionListener cursorListener;
    private Updater updater;

    private MapManager mapm;
    private ProjectManager projectm;
    private GuiManager guim;

    public StatusBar() {
        super(new MigLayout("insets 5"));

        GuiUtils.throwIfNotOnEDT();

        this.projectm = Main.getProjectManager();
        this.mapm = Main.getMapManager();
        this.guim = Main.getGuiManager();

        // dimensions of all elements
        int height = 25;
        Dimension statusbarDim = new Dimension(500, 30);
        Dimension labelPositionDim = new Dimension(350, height);
        Dimension progressbarDim = new Dimension(150, height);
        Dimension labelScaleDim = new Dimension(150, height);

        /*
            Show coordinates
         */

        // border and size
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
        setPreferredSize(statusbarDim);

        // display scale or zoom
        labelScale = new JLabel();
        labelScale.setPreferredSize(labelScaleDim);
        this.add(labelScale);

        labelPosition = new JLabel();
        labelPosition.setPreferredSize(labelPositionDim);
        this.add(labelPosition);

        cursorListener = new MousePositionListener();

        /*
            Progress bar
         */

        // progress bar and its manager
        progressbarManager = new ProgressbarManager();

        JProgressBar progressBar = progressbarManager.getProgressbar();
        progressBar.setPreferredSize(progressbarDim);
        this.add(progressBar);

        // label associated with progress bar
        JLabel progressLabel = progressbarManager.getLabel();
        this.add(progressLabel);

        // hide all when start
        progressbarManager.setComponentsVisible(false);

        /*

         */

        // watch project and map
        this.updater = new Updater();
        this.notifm = new EventNotificationManager(this);
        notifm.setDefaultListener(updater);

        projectm.getNotificationManager().addObserver(this);
        mapm.getNotificationManager().addObserver(this);

        // first initialization when ready
        guim.addInitialisationOperation(() -> {
            updater.run();
            mouseListening(true);
        });
    }

    /**
     * Return true if status bar is listenning mouse
     *
     * @return
     */
    public boolean isMouseListening() {
        return mouseListening;
    }

    /**
     * Set status bar listen mouse and show coords
     *
     * @param val
     */
    public void mouseListening(boolean val) {

        if (mapm.getMainMap() == null) {
            return;
        }

        // always remove listener to avoid double registration
        mapm.getMainMap().removeMouseListener(cursorListener);
        mapm.getMainMap().removeMouseMotionListener(cursorListener);

        if (val == true) {
            mapm.getMainMap().addMouseListener(cursorListener);
            mapm.getMainMap().addMouseMotionListener(cursorListener);
        }

        mouseListening = val;
    }

    /**
     * Refresh status bar content
     */
    public void refresh() {
        SwingUtilities.invokeLater(updater);
    }

    /**
     * Update zoom / scale values
     */
    private class Updater implements Runnable, EventListener {

        private DecimalFormat zoomFormat;
        private DecimalFormat meterFormat;
        private DecimalFormat kmFormat;

        public Updater() {

            zoomFormat = new DecimalFormat("###");
            zoomFormat.setRoundingMode(RoundingMode.UP);

            meterFormat = new DecimalFormat("###");
            meterFormat.setRoundingMode(RoundingMode.UP);

            kmFormat = new DecimalFormat("###.##");
            kmFormat.setRoundingMode(RoundingMode.UP);
        }

        @Override
        public void notificationReceived(Event arg) {

            // enable / disable mouse when project change
            if (arg instanceof ProjectEvent) {
                if (ProjectEvent.isNewProjectLoadedEvent(arg) && mouseListening == false) {
                    mouseListening(true);
                } else if (ProjectEvent.isCloseProjectEvent(arg) && mouseListening == true) {
                    mouseListening(false);
                }
            }

            if (arg instanceof MapManagerEvent) {
                refresh();
            }
        }

        @Override
        public void run() {

            GuiUtils.throwIfNotOnEDT();

        }

    }

    /**
     * Listen mouse position and map and display it
     */
    private class MousePositionListener extends MouseAdapter {

        @Override
        public void mouseExited(MouseEvent arg0) {
            // remove position when mouse leave
            GuiUtils.throwIfNotOnEDT();
            labelPosition.setText("");
        }

        @Override
        public void mouseDragged(MouseEvent arg0) {
            reportMousePosition(arg0);
        }

        @Override
        public void mouseMoved(MouseEvent arg0) {
            reportMousePosition(arg0);
        }

        /**
         * Display mouse position
         *
         * @param arg0
         */
        private void reportMousePosition(MouseEvent arg0) {

            GuiUtils.throwIfNotOnEDT();

            // show position in pixel
            Point pxPoint = arg0.getPoint();
            String text = "Pixel: x=" + pxPoint.x + ", y=" + pxPoint.y;

            // show position on map, if possible
            Point2D wuPoint = mapm.mainmap.screenToWorld(arg0.getPoint());
            if (wuPoint != null) {
                text += " World Unit: x=" + wuPoint.getX() + ", y=" + wuPoint.getY();
            }

            labelPosition.setText(text);
            labelPosition.repaint();
            labelPosition.revalidate();
        }
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    @Override
    public ProgressbarManager getProgressbarManager() {
        return progressbarManager;
    }


}
