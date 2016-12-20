package org.abcmap.gui.components.map;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.rendering.CachedRenderingEngine;
import org.abcmap.core.rendering.RenderingEvent;
import org.abcmap.gui.components.geo.MapNavigationBar;
import org.abcmap.gui.tools.MapTool;
import org.geotools.geometry.jts.ReferencedEnvelope;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Display a map by using a partial cache system
 * <p>
 * Cache is managed by a RenderedPartialFactory. This partial factory produce portions of map and store it in database.
 * <p>
 */
public class CachedMapPane extends JPanel implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CachedMapPane.class);

    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * Rendering engine associated with pane
     */
    private final CachedRenderingEngine renderingEngine;


    /**
     * If true, it is the first time panel is rendering
     */
    private boolean firstTimeRender;

    /**
     * World envelope (positions) of map rendered on panel
     */
    private ReferencedEnvelope currentWorlEnvelope;

    /**
     * Various mouse listeners which allow user to control map with mouse
     */
    private CachedMapPaneMouseController mouseControl;

    /**
     * Optional navigation bar in bottom of map
     */
    private MapNavigationBar navigationBar;

    /**
     * If set to true, more information are displayed
     */
    private boolean debugMode;

    /**
     * Minimal zoom factor relative to project width
     */
    private double minZoomFactor;

    /**
     * Maximal zoom factor relative to project width
     */
    private double maxZoomFactor;

    private final EventNotificationManager notifm;

    public CachedMapPane(Project p) {
        super(new MigLayout("fill"));

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        addComponentListener(new RefreshMapComponentListener());

        minZoomFactor = 0.1;
        maxZoomFactor = 1.5;

        this.project = p;
        this.renderingEngine = new CachedRenderingEngine(project);

        // first time render whole project
        firstTimeRender = true;

        // repaint when new partials are ready
        notifm = new EventNotificationManager(this);
        notifm.setDefaultListener((ev) -> {
            // new partials are ready, only repaint
            if (ev instanceof RenderingEvent) {
                CachedMapPane.this.repaint();
            }
            // map changed, prepare new and repaint
            else if (ev instanceof ProjectEvent) {
                refreshMap();
            }

        });

        // listen rendering new partials
        renderingEngine.getNotificationManager().addObserver(this);

        // listen map modifications
        MainManager.getProjectManager().getNotificationManager().addObserver(this);

        setDebugMode(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // don't paint before all is ready
        if (firstTimeRender) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".paintComponent(): Call rejected, before first rendering operation");
            }
            return;
        }

        // paint map
        Graphics2D g2d = (Graphics2D) g;
        renderingEngine.paint(g2d);

        // if debug mode enabled, paint world envelope asked
        if (debugMode) {

            AffineTransform worldToScreenTransform = getWorldToScreenTransform();
            if (worldToScreenTransform != null) {

                Point2D blc = new Point2D.Double(currentWorlEnvelope.getMinX(), currentWorlEnvelope.getMinY());
                Point2D urc = new Point2D.Double(currentWorlEnvelope.getMaxX(), currentWorlEnvelope.getMaxY());
                blc = worldToScreenTransform.transform(blc, null);
                urc = worldToScreenTransform.transform(urc, null);

                int x = (int) blc.getX();
                int y = (int) urc.getY();
                int w = (int) Math.abs(urc.getX() - blc.getX());
                int h = (int) Math.abs(urc.getY() - blc.getY());

                g2d.setColor(Color.blue);
                int st = 2;
                g2d.setStroke(new BasicStroke(st));
                g2d.drawRect(x + st, y + st, w - st * 2, h - st * 2);

            }
        }
    }


    /**
     * Refresh list of partials to display in component
     */
    public void refreshMap() {

        Dimension panelDimensions = getSize();

        // panel is not displayed yet, do not render
        if (panelDimensions.getWidth() < 1 || panelDimensions.getHeight() < 1) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".refreshMap(): Call rejected, component too small " + panelDimensions);
            }
            return;
        }
        if (this.isVisible() == false) {
            if (debugMode) {
                logger.warning(this.getClass().getSimpleName() + ".refreshMap(): Call rejected, component not visible");
            }
            return;
        }

        // first time we have to render map,
        // render whole project
        if (firstTimeRender) {
            resetDisplay();
            firstTimeRender = false;
        }

        // ensure that envelope is valid and proportional to component
        checkWorldEnvelope();

        // prepare map to render
        try {
            renderingEngine.prepareMap(currentWorlEnvelope, panelDimensions);
        } catch (Exception e) {
            logger.error(e);
        }

        // repaint component
        repaint();

    }

    /**
     * Ensure that envelope is valid and proportional to component
     */
    private void checkWorldEnvelope() {

        Dimension panelDimensions = getSize();

        double coeffPx = panelDimensions.getWidth() / panelDimensions.getHeight();
        double coeffWu = currentWorlEnvelope.getWidth() / currentWorlEnvelope.getHeight();

        if (Math.abs(coeffPx - coeffWu) > 0.001) {

            double widthWu = currentWorlEnvelope.getWidth();
            double heightWu = widthWu / coeffPx;

            double minx = currentWorlEnvelope.getMinX();
            double miny = currentWorlEnvelope.getMinY();
            double maxx = currentWorlEnvelope.getMaxX();
            double maxy = miny + heightWu;

            currentWorlEnvelope = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorlEnvelope.getCoordinateReferenceSystem());
        }

    }

    /**
     * Pixel scale can be used to translate summary pixels to world unit
     *
     * @return
     */
    public double getScale() {
        return renderingEngine.getScale();
    }

    /**
     * @param direction
     */
    private void zoomEnvelope(int direction) {

        double projectWorldWidth = project.getMaximumBounds().getWidth();

        double zoomStepW = projectWorldWidth / 30;
        double zoomStepH = currentWorlEnvelope.getHeight() * zoomStepW / currentWorlEnvelope.getWidth();

        double minx;
        double maxx;
        double miny;
        double maxy;

        ReferencedEnvelope newEnv;

        // zoom in
        if (direction > 0) {
            minx = currentWorlEnvelope.getMinX() + zoomStepW;
            maxx = currentWorlEnvelope.getMaxX() - zoomStepW;

            miny = currentWorlEnvelope.getMinY() + zoomStepH;
            maxy = currentWorlEnvelope.getMaxY() - zoomStepH;

            newEnv = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorlEnvelope.getCoordinateReferenceSystem());
        }

        // zoom out
        else if (direction < 0) {

            minx = currentWorlEnvelope.getMinX() - zoomStepW;
            maxx = currentWorlEnvelope.getMaxX() + zoomStepW;

            miny = currentWorlEnvelope.getMinY() - zoomStepH;
            maxy = currentWorlEnvelope.getMaxY() + zoomStepH;

            newEnv = new ReferencedEnvelope(minx, maxx, miny, maxy, currentWorlEnvelope.getCoordinateReferenceSystem());

        }

        // invalid
        else {
            throw new IllegalArgumentException("Invalid zoom direction: " + direction);
        }

        if (newEnv.getWidth() > projectWorldWidth * minZoomFactor
                && newEnv.getWidth() < projectWorldWidth * maxZoomFactor) {
            currentWorlEnvelope = newEnv;
        }

        // check if envelope is proportionnal
        checkWorldEnvelope();

    }

    /**
     * Zoom in one increment
     */
    public void zoomIn() {
        zoomEnvelope(1);
    }

    /**
     * Zoom out one increment
     */
    public void zoomOut() {
        zoomEnvelope(-1);
    }

    /**
     * Reset display to show whole width of map, from upper left corner corner
     */
    public void resetDisplay() {

        // TODO center map at a different scale ?
        // this could avoid "blank screen" when layers are large but empty

        // get world x and width
        ReferencedEnvelope projectBounds = project.getMaximumBounds();

        double worldWidthWu = projectBounds.getMaxX() - projectBounds.getMinX();
        double heightWu = getHeight() * worldWidthWu / getWidth();

        double minx = projectBounds.getMinX();
        double maxx = projectBounds.getMaxX();
        double miny = projectBounds.getMaxY() - heightWu;
        double maxy = projectBounds.getMaxY();

        currentWorlEnvelope = new ReferencedEnvelope(minx, maxx, miny, maxy, project.getCrs());

        // first time width: 3000px ?
        //System.out.println("Reset display: " + getSize() + " / " + scale + " / " + worldEnvelope + "");
    }

    /**
     * Enable navigation bar with zoom in/out and center button
     *
     * @param val
     */
    public void setNavigationBarEnabled(boolean val) {

        // add navigation bar
        if (val) {
            navigationBar = new MapNavigationBar(this);
            add(navigationBar, "alignx right, aligny bottom");
        }

        // remove bar if present
        else {
            if (navigationBar != null) {
                remove(navigationBar);
                navigationBar = null;
            }
        }
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        renderingEngine.setDebugMode(debugMode);
    }

    /**
     * Return a utility to transform coordinates
     *
     * @return
     */
    public AffineTransform getWorldToScreenTransform() {
        return renderingEngine.getWorldToScreenTransform();
    }

    /**
     * Return a utility to transform coordinates
     *
     * @return
     */
    public AffineTransform getScreenToWorldTransform() {
        return renderingEngine.getScreenToWorldTransform();
    }

    /**
     * Remove a tool from listener list
     *
     * @param tool
     */
    public void removeToolFromListeners(MapTool tool) {
        removeMouseListener(tool);
        removeMouseMotionListener(tool);
        removeMouseWheelListener(tool);
    }

    /**
     * Remove a tool from listener list
     *
     * @param tool
     */
    public void addToolToListeners(MapTool tool) {
        addMouseListener(tool);
        addMouseMotionListener(tool);
        addMouseWheelListener(tool);
    }

    /**
     * Observe this component and refresh map when needed
     */
    private class RefreshMapComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentShown(ComponentEvent e) {
            // refresh map if needed
            refreshMap();
        }

        @Override
        public void componentHidden(ComponentEvent e) {

        }
    }

    /**
     * Enable or disable mouse control of map, in order to allow user to move or zoom map
     */
    public void setMouseManagementEnabled(boolean enabled) {

        if (enabled == true) {

            if (this.mouseControl != null) {
                return;
            }

            this.mouseControl = new CachedMapPaneMouseController(this);

            this.addMouseListener(mouseControl);
            this.addMouseMotionListener(mouseControl);
            this.addMouseWheelListener(mouseControl);

        } else {

            if (this.mouseControl == null) {
                return;
            }

            this.removeMouseListener(mouseControl);
            this.removeMouseMotionListener(mouseControl);
            this.removeMouseWheelListener(mouseControl);

            mouseControl = null;
        }
    }

    /**
     * Get project associated with rendering engine
     *
     * @return
     */
    public Project getProject() {
        return project;
    }

    /**
     * Set envelope shown by component
     *
     * @param worldEnvelope
     */
    public void setWorldEnvelope(ReferencedEnvelope worldEnvelope) {

        if (worldEnvelope.getCoordinateReferenceSystem().equals(project.getCrs()) == false) {
            throw new IllegalStateException("Coordinate Reference Systems are different: " + worldEnvelope.getCoordinateReferenceSystem() + " / " + project.getCrs());
        }

        this.currentWorlEnvelope = worldEnvelope;
    }

    /**
     * Get specified world envelope to show
     *
     * @return
     */
    public ReferencedEnvelope getWorldEnvelope() {
        return new ReferencedEnvelope(currentWorlEnvelope);
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}