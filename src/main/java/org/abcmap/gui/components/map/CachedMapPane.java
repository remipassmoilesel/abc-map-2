package org.abcmap.gui.components.map;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.partials.RenderedPartial;
import org.abcmap.core.partials.RenderedPartialFactory;
import org.abcmap.core.partials.RenderedPartialQueryResult;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.gui.components.geo.MapNavigationBar;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Display a map by using a partial cache system
 * <p>
 * Cache is managed by a RenderedPartialFactory. This partial factory produce portions of map and store it in database.
 * <p>
 * //TODO workaround for first component display ? See computeOptimalPartialSide()
 */
public class CachedMapPane extends JPanel implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CachedMapPane.class);

    /**
     * Lock to prevent too much thread rendering
     */
    private final ReentrantLock renderLock;

    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * List of factories employed to renderer map. Each factory renderer one layer
     */
    private final HashMap<String, RenderedPartialFactory> partialFactories;

    /**
     * List of map content associated with layers
     */
    private final HashMap<String, MapContent> layerMapContents;

    /**
     * World envelope (positions) of map rendered on panel
     */
    private ReferencedEnvelope worldEnvelope;

    /**
     * Last time of rendering in ms
     */
    private long lastRender = -1;

    /**
     * Minimum interval between rendering in ms
     */
    private long renderMinIntervalMs = 50;

    /**
     * Current set of partials that have to be painted
     */
    private HashMap<String, RenderedPartialQueryResult> currentPartials;

    /**
     * Various mouse listeners which allow user to control map with mouse
     */
    private CachedMapPaneMouseController mouseControl;

    /**
     * Current value of rendered map in partials. In world unit.
     */
    private double partialSideWu;

    /**
     * Current side of a partial
     */
    private final double partialSidePx;

    private boolean debugMode = true;
    private MapNavigationBar navigationBar;
    private final EventNotificationManager notifm;

    public CachedMapPane(Project project) {

        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        this.project = project;
        this.renderLock = new ReentrantLock();
        this.partialFactories = new HashMap<>();
        this.layerMapContents = new HashMap<>();
        this.currentPartials = new HashMap<>();

        // FIRST set partial side in pixel
        this.partialSidePx = RenderedPartialFactory.DEFAULT_PARTIAL_SIDE_PX;

        // THEN compute first time display, show whole map
        setWorldEnvelope(project.getMaximumBounds(), true);

        this.addComponentListener(new RefreshMapComponentListener());

        // repaint when partials are added in store
        notifm = new EventNotificationManager(this);
        notifm.setDefaultListener((ev) -> {
            CachedMapPane.this.repaint();
        });
        project.getRenderedPartialsStore().getNotificationManager().addObserver(this);

    }

    @Override
    protected void finalize() throws Throwable {
        // remove observer on finalizing
        project.getRenderedPartialsStore().getNotificationManager().removeObserver(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }

        if (renderLock.isLocked()) {
            logger.debug("Render is in progress, avoid painting");
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        for (AbstractLayer lay : project.getLayersList()) {

            RenderedPartialQueryResult partials = currentPartials.get(lay.getId());

            // list of layer changed before refreshMap called
            if (partials == null) {
                continue;
            }

            // get affine transform to set position of partials
            AffineTransform worldToScreen = partials.getWorldToScreenTransform();

            if (debugMode) {
                g2d.setColor(Color.darkGray);
            }

            // iterate current partials
            for (RenderedPartial part : partials.getPartials()) {

                // compute position of tile on map
                ReferencedEnvelope ev = part.getEnvelope();
                Point2D.Double worldPos = new Point2D.Double(ev.getMinX(), ev.getMaxY());
                Point2D screenPos = worldToScreen.transform(worldPos, null);

                int x = (int) Math.round(screenPos.getX());
                int y = (int) Math.round(screenPos.getY());
                int w = part.getRenderedWidth();
                int h = part.getRenderedHeight();

                // draw partial
                g2d.drawImage(part.getImage(), x, y, w, h, null);

                if (debugMode) {
                    g2d.drawRect(x, y, w, h);
                }

            }

            // draw maximums bounds asked if necessary
            if (debugMode) {
                Point2D.Double ulc = new Point2D.Double(worldEnvelope.getMinX(), worldEnvelope.getMaxY());
                Point2D wp = worldToScreen.transform(ulc, null);
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.red);
                g2d.drawRect((int) wp.getX(), (int) wp.getY(), 3, 3);
            }
        }

    }

    /**
     * Refresh list of partials to display in component
     */
    public void refreshMap() {

        // check if this method have not been called few milliseconds before
        if (checkMinimumRenderInterval() == false) {
            return;
        }

        // on thread at a time renderer map for now
        if (renderLock.tryLock() == false) {
            System.err.println("Already rendering !");
            return;
        }

        try {

            logger.debug("Rendering component: " + this);

            // get component size
            Dimension dim = CachedMapPane.this.getSize();

            if (dim.width < 1 || dim.height < 1) {
                return;
            }

            // iterate layers, sorted by z-index
            for (AbstractLayer lay : project.getLayersList()) {

                String layId = lay.getId();

                // retrieve map content associated with layer

                // if map does no exist, create one
                MapContent map = layerMapContents.get(layId);
                if (map == null) {
                    map = lay.buildMapContent();
                    layerMapContents.put(layId, map);
                }

                // retrieve partial factory associated with layer
                RenderedPartialFactory factory = partialFactories.get(layId);
                if (factory == null) {
                    factory = new RenderedPartialFactory(project.getRenderedPartialsStore(), map, layId);
                    factory.setPartialSideWu(partialSideWu);
                    factory.setDebugMode(debugMode);
                    partialFactories.put(layId, factory);
                }

                // if map is not up to date, create a new one and invalidate cache
                if (GeoUtils.isMapContains(map, lay.getInternalLayer()) == false) {

                    System.out.println("Cache invalidated ! " + layId);

                    map = lay.buildMapContent();
                    layerMapContents.put(layId, map);
                    factory.setMapContent(map);

                    project.getRenderedPartialsStore().deletePartialsForLayer(layId);

                }

                // search which partials are necessary to display
                RenderedPartialQueryResult newPartials = factory.intersect(worldEnvelope,
                        () -> {
                            // each time a partial come, map will be repaint
                            CachedMapPane.this.repaint();
                        });

                // store it to draw it later
                currentPartials.put(layId, newPartials);
            }

            // repaint component
            repaint();

        } finally {
            renderLock.unlock();
        }

    }

    /**
     * Check if a minimum interval of time is respected between rendering operations, to avoid too many calls
     *
     * @return
     */
    private boolean checkMinimumRenderInterval() {

        // check last rendering time
        boolean render = System.currentTimeMillis() - lastRender > renderMinIntervalMs;

        // save time if needed
        if (render) {
            lastRender = System.currentTimeMillis();
        }

        return render;
    }

    public void initializeMap() {
        refreshMap();
    }

    /**
     * Set the reference position of map at ULC corner of component
     *
     * @param ulc
     */
    public void setUlcWorldPosition(Point2D ulc) {

        Dimension panelDimensionsPx = getSize();

        // get width and height in world unit
        double wdg = partialSideWu * panelDimensionsPx.width / partialSidePx;
        double hdg = partialSideWu * panelDimensionsPx.height / partialSidePx;

        // create a new envelope
        double x1 = ulc.getX();
        double y1 = ulc.getY() - hdg; // to BLC
        double x2 = ulc.getX() + wdg;
        double y2 = ulc.getY();

        this.worldEnvelope = new ReferencedEnvelope(x1, x2, y1, y2, project.getCrs());
    }

    /**
     * Get the reference position of map at ULC corner of component
     *
     * @return
     */
    public Point2D getUlcWorldPosition() {
        return new Point2D.Double(worldEnvelope.getMinX(), worldEnvelope.getMaxY());
    }

    /**
     * Get the size in pixel of each partial
     * <p>
     * It can be used as a "zoom" value
     */
    public double getPartialSidePx() {
        return partialSidePx;
    }

    /**
     * Set the size in world unit of the map rendered on each partial
     * <p>
     * It can be used as a "zoom" value
     *
     * @param value
     */
    public void setPartialSideWu(double value) {
        partialSideWu = RenderedPartialFactory.normalizeWorldUnitSideValue(value);
        for (RenderedPartialFactory factory : partialFactories.values()) {
            factory.setPartialSideWu(partialSideWu);
        }
    }

    /**
     * Get current scale in panel
     *
     * @return
     */
    public double getScale() {
        return getPartialSideWu() / getPartialSidePx();
    }

    /**
     * Set current scale in panel
     *
     * @return
     */
    public void setScale(double scale) {
        double partWu = getPartialSidePx() * scale;
        setPartialSideWu(partWu);
    }

    /**
     * Reset display to show whole width of map, from upper left corner corner
     */
    public void resetDisplay() {
        // TODO center map at a different scale ?
        // this could avoid "blank screen" when layers are large but empty
        ReferencedEnvelope projectBounds = project.getMaximumBounds();
        setWorldEnvelope(projectBounds, true);
    }

    /**
     * Get the size in degrees of the map rendered on each partial
     * <p>
     * It can be used as a "zoom" value
     * <p>
     */
    public double getPartialSideWu() {
        return partialSideWu;
    }

    /**
     * Enable navigation bar with zoom in/out and center button
     *
     * @param val
     */
    public void setNavigationBarEnabled(boolean val) {

        // add navigation bar
        if (val) {
            navigationBar = new MapNavigationBar();
            add(navigationBar);
            navigationBar.refreshBoundsFrom(getSize());
        }

        // remove bar if present
        else {
            if (navigationBar != null) {
                remove(navigationBar);
                navigationBar = null;
            }
        }
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

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public Project getProject() {
        return project;
    }

    /**
     * Set envelope shown by component
     *
     * @param worldEnvelope
     */
    public void setWorldEnvelope(ReferencedEnvelope worldEnvelope) {
        setWorldEnvelope(worldEnvelope, false);
    }

    /**
     * Set world envelope shown by component and if specified, compute optimal partial side value ("scale")
     * to show whole map
     *
     * @param worldEnvelope
     * @param computeOptimalPartialSide
     */
    public void setWorldEnvelope(ReferencedEnvelope worldEnvelope, boolean computeOptimalPartialSide) {

        if (worldEnvelope.getCoordinateReferenceSystem().equals(project.getCrs()) == false) {
            throw new IllegalStateException("Cooridnate Reference Systems are different: " + worldEnvelope.getCoordinateReferenceSystem() + " / " + project.getCrs());
        }

        this.worldEnvelope = worldEnvelope;

        if (computeOptimalPartialSide) {
            computeOptimalPartialSide();
        }

    }

    /**
     * Get specified world envelope shown. It can be larger or greater than actual map shown
     *
     * @return
     */
    public ReferencedEnvelope getWorldEnvelope() {
        return worldEnvelope;
    }

    /**
     * Get shown world envelope, corresponding to component size
     *
     * @return
     */
    public ReferencedEnvelope getActualWorldEnvelope() {

        Dimension panelDimensionsPx = getSize();

        // get width and height in world unit
        double wdg = partialSideWu * panelDimensionsPx.width / partialSidePx;
        double hdg = partialSideWu * panelDimensionsPx.height / partialSidePx;

        double x1 = worldEnvelope.getMinX();
        double x2 = worldEnvelope.getMinX() + wdg;
        double y1 = worldEnvelope.getMinY();
        double y2 = worldEnvelope.getMinY() + hdg;

        return new ReferencedEnvelope(x1, x2, y1, y2, project.getCrs());
    }

    /**
     * Compute optimal partial size in world unit, in order to show map on whole component.
     * <p>
     * If component is not visible, fake size of component will be used and result can look weird
     */
    public void computeOptimalPartialSide() {

        double worldWidth = worldEnvelope.getMaxX() - worldEnvelope.getMinX();
        double compWidth = getSize().getWidth();

        if (compWidth < 1) {
            logger.debug("Optimal partial size computing: component not shown, use fake width");
            compWidth = 800;
        }

        double optimalPartialSideWu = worldWidth * getPartialSidePx() / compWidth;

        setPartialSideWu(optimalPartialSideWu);

    }


    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }
    }

    @Override
    public void revalidate() {
        super.revalidate();

        // refresh navigation bar
        if (navigationBar != null) {
            navigationBar.refreshBoundsFrom(getSize());
        }
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

}
