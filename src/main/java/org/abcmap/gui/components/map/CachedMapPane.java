package org.abcmap.gui.components.map;

import org.abcmap.core.partials.RenderedPartial;
import org.abcmap.core.partials.RenderedPartialFactory;
import org.abcmap.core.partials.RenderedPartialQueryResult;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.core.utils.GeoUtils;
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
 */
public class CachedMapPane extends JPanel {

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
     * Last time of rendering in ms
     */
    private long lastRender = -1;

    /**
     * Minimum interval between rendering in ms
     */
    private long renderMinIntervalMs = 50;

    /**
     * ULC point to start renderer map from
     */
    private Point2D worldPosition;

    /**
     * If set to true, the partial grid is displayed
     */
    private boolean showGrid = false;

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

    public CachedMapPane(Project project) {

        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        this.project = project;
        this.renderLock = new ReentrantLock();
        this.partialFactories = new HashMap<>();
        this.layerMapContents = new HashMap<>();
        this.currentPartials = new HashMap<>();

        this.partialSideWu = 5;

        this.addComponentListener(new RefreshMapComponentListener());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        for (AbstractLayer lay : project.getLayersList()) {

            RenderedPartialQueryResult partials = currentPartials.get(lay.getId());

            // get affine transform to set position of partials
            AffineTransform worldToScreen = partials.getWorldToScreenTransform();

            if (showGrid) {
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

                if (showGrid) {
                    g2d.drawRect(x, y, w, h);
                }

            }

            // draw maximums bounds asked if necessary
            if (showGrid) {
                Point2D wp = worldToScreen.transform(worldPosition, null);
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

            //System.out.println("Render task launched");

            // get component size
            Dimension dim = CachedMapPane.this.getSize();

            if (dim.width < 1 || dim.height < 1) {
                return;
            }

            // iterate layers, sorted by z-index
            for (AbstractLayer lay : project.getLayersList()) {

                String layId = lay.getId();

                // TODO update map contents if needed: e.g. when TileLayer are updated, the internal layer change
                // TODO Can we use factory.setMapContent ? Will it disturb current rendering operations ?

                // retrieve map content associated with layer

                // if map does no exist, create one
                MapContent map = layerMapContents.get(layId);
                if (map == null){
                    map = lay.buildMapContent();
                    layerMapContents.put(layId, map);
                }

                // retrieve partial factory associated with layer
                RenderedPartialFactory factory = partialFactories.get(layId);
                if (factory == null) {
                    factory = new RenderedPartialFactory(project.getRenderedPartialsStore(), map, layId);
                    factory.setPartialSideWu(partialSideWu);
                    partialFactories.put(layId, factory);
                }

                // if map is not up to date, create a new one and invalidate cache
                if(GeoUtils.isMapContains(map, lay.getInternalLayer()) == false){

                    System.out.println("Invalidated ! " + layId);

                    map = lay.buildMapContent();
                    layerMapContents.put(layId, map);
                    factory.setMapContent(map);

                    project.getRenderedPartialsStore().deletePartialsForLayer(layId);

                }

                // search which partials are necessary to display
                RenderedPartialQueryResult partials = factory.intersect(worldPosition, dim, map.getCoordinateReferenceSystem(),
                        () -> {
                            // each time a partial come, map will be repaint
                            CachedMapPane.this.repaint();
                        });

                // store it to draw it later
                currentPartials.put(layId, partials);
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
        boolean render = System.currentTimeMillis() - lastRender > renderMinIntervalMs;
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
     * @param worldPoint
     */
    public void setWorldPosition(Point2D worldPoint) {
        this.worldPosition = worldPoint;
    }

    /**
     * Get the reference position of map at ULC corner of component
     *
     * @return
     */
    public Point2D getWorldPosition() {
        return new Point2D.Double(worldPosition.getX(), worldPosition.getY());
    }

    /**
     * Get the size in pixel of each partial
     * <p>
     * It can be used as a "zoom" value
     */
    public int getPartialSidePx() {
        return RenderedPartialFactory.DEFAULT_PARTIAL_SIDE_PX;
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
     * Get the size in degrees of the map rendered on each partial
     * <p>
     * It can be used as a "zoom" value
     * <p>
     */
    public double getPartialSideWu() {
        return partialSideWu;
    }

    /**
     * Set to true to show partial grid and marks
     *
     * @param showGrid
     */
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    /**
     * Observe this component and refresh map when needed
     */
    public class RefreshMapComponentListener implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent e) {
            refreshMap();
        }

        @Override
        public void componentMoved(ComponentEvent e) {
            refreshMap();
        }

        @Override
        public void componentShown(ComponentEvent e) {
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
}
