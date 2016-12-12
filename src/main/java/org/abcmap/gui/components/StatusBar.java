package org.abcmap.gui.components;

import com.vividsolutions.jts.geom.Coordinate;
import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.MapEvent;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.MapManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.notifications.UpdatableByNotificationManager;
import org.abcmap.gui.components.progressbar.HasProgressbarManager;
import org.abcmap.gui.components.progressbar.ProgressbarManager;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class StatusBar extends JPanel implements HasNotificationManager, HasProgressbarManager {

    /**
     * If true display errors
     */
    private boolean showErrors = false;

    private NotificationManager notifm;

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

    /**
     * Current position of mouse
     */
    private Coordinate mouseCoords;

    private MousePositionListener cursorListener;
    private Updater updater;

    private MapManager mapm;
    private ProjectManager projectm;
    private GuiManager guim;

    public StatusBar() {
        super(new MigLayout("insets 5"));

        GuiUtils.throwIfNotOnEDT();

        this.projectm = MainManager.getProjectManager();
        this.mapm = MainManager.getMapManager();
        this.guim = MainManager.getGuiManager();

        mouseCoords = new Coordinate();

        // dimensions of all elements
        int height = 25;
        Dimension statusbarDim = new Dimension(500, 30);
        Dimension labelPositionDim = new Dimension(350, height);
        Dimension progressbarDim = new Dimension(150, height);
        Dimension labelScaleDim = new Dimension(150, height);

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

        cursorListener = new MousePositionListener();

        this.updater = new Updater();

        // watch project and map
        this.notifm = new NotificationManager(this);
        notifm.setDefaultUpdatableObject(updater);

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

        // always remove listener to avoid double registration
        mapm.getMapComponent().removeMouseListener(cursorListener);
        mapm.getMapComponent().removeMouseMotionListener(cursorListener);

        if (val == true) {
            mapm.getMapComponent().addMouseListener(cursorListener);
            mapm.getMapComponent().addMouseMotionListener(cursorListener);
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
    private class Updater implements Runnable, UpdatableByNotificationManager {

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
        public void notificationReceived(Notification arg) {

            // enable / disable mouse when project change
            if (arg instanceof ProjectEvent) {
                if (ProjectEvent.isNewProjectLoadedEvent(arg) && mouseListening == false) {
                    mouseListening(true);
                } else if (ProjectEvent.isCloseProjectEvent(arg) && mouseListening == true) {
                    mouseListening(false);
                }
            }

            if (arg instanceof MapEvent) {
                refresh();
            }
        }

        @Override
        public void run() {

            GuiUtils.throwIfNotOnEDT();

            // display zoom when no georeferencing (CRS2D)
            String txt = "Zoom: " + zoomFormat.format(mapm.getDisplayScale() * 100) + "%";

            // display scale when CRS is available
            if (projectm.isInitialized() && mapm.isGeoreferencementEnabled()) {

                // compute scale

                /*
                try{

                    ArrayList<Coordinate> georefs = mapm.getGeoReferences();
                    if (georefs.size() < 2) {
                        return;
                    }

                    Coordinate ref1 = new Coordinate();
                    Coordinate ref2 = new Coordinate(mapm.getGeoReferences()
                            .get(0));


                    ref2.longitudePx += 10d
                            * ConfigurationConstants.JAVA_RESOLUTION / 25.45d
                            / mapm.getDisplayScale();


                    mapm.transformCoords(GeoConstants.SCREEN_TO_WORLD, ref2);

                    Double dist = mapm.azimuthDistance(ref1, ref2)[1];
                    String unit = "m";
                    DecimalFormat format = meterFormat;

                    if (dist > 1000) {
                        dist /= 1000;
                        unit = "km";
                        format = kmFormat;
                    }

                    txt += " - Echelle: " + format.format(dist) + " " + unit;

                } catch (Exception e) {
                    if (MainManager.isDebugMode() && showErrors) {
                        Log.error(e);
                    }
                }

                */

            }

            // affecter le texte
            labelScale.setText(txt);
            labelScale.revalidate();
            labelScale.repaint();
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
         * Display mouse posisiton
         *
         * @param arg0
         */
        private void reportMousePosition(MouseEvent arg0) {

            GuiUtils.throwIfNotOnEDT();

            // position du curseur à l'echelle
            Point p = mapm.getScaledPoint(arg0.getPoint());

            // latitude longitude
            mouseCoords.x = p.x;
            mouseCoords.y = p.y;

            /*
            // afficher la position en pixels
            String pixelPosStr = mouseCoords.getStringRepresentation(GeoConstants.DISPLAY_PIXELS);

            // afficher en degres
            String degreesPosStr = null;
            if (mapm.isGeoreferencementEnabled()) {
                try {

                    // convertir
                    mapm.transformCoords(GeoConstants.SCREEN_TO_WORLD,
                            mouseCoords);

                    // mettre en forme
                    degreesPosStr = " - Degrés: "
                            + mouseCoords
                            .getStringRepresentation(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC);

                } catch (MapManagerException e) {
                    if (showErrors) {
                        Log.error(e);
                    }
                    degreesPosStr = "Erreur de géoréférencement";
                }
            }

            // afficher le texte
            String positionStr = degreesPosStr == null ? pixelPosStr
                    : degreesPosStr + " - " + pixelPosStr;

            labelPosition.setText("<html>Position du curseur: " + positionStr
                    + "</html>");
            labelPosition.revalidate();
            labelPosition.repaint();

             */

        }
    }

    @Override
    public NotificationManager getNotificationManager() {
        return notifm;
    }

    @Override
    public ProgressbarManager getProgressbarManager() {
        return progressbarManager;
    }


}
