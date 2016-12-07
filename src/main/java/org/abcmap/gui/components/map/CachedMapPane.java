package org.abcmap.gui.components.map;

import org.abcmap.core.partials.RenderedPartial;
import org.abcmap.core.partials.RenderedPartialFactory;
import org.abcmap.core.partials.RenderedPartialQueryResult;
import org.abcmap.core.partials.RenderedPartialStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
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
    private final ReentrantLock lock;

    /**
     * Last time of rendering in ms
     */
    private long lastRender = -1;

    /**
     * Minimum interval between rendering in ms
     */
    private long renderMinIntervalMs = 50;

    /**
     * ULC point to start render map from
     */
    private Point2D worldPosition;

    /**
     * Map to render
     */
    private MapContent map;

    /**
     * If set to true, the partial grid is displayed
     */
    private boolean showGrid = false;

    /**
     * Manage and create partials of a map
     */
    private RenderedPartialFactory partialFactory;

    /**
     * Current set of partials that have to be painted
     */
    private RenderedPartialQueryResult currentPartials;

    public CachedMapPane(RenderedPartialStore store, MapContent map) {

        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        this.map = map;
        this.partialFactory = new RenderedPartialFactory(store, map);
        this.lock = new ReentrantLock();

        this.addComponentListener(new RefreshMapComponentListener());

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // nothing to display
        if (currentPartials == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;

        // get affine transform to set position of partials
        AffineTransform worldToScreen = currentPartials.getWorldToScreenTransform();

        if (showGrid) {
            g2d.setColor(Color.darkGray);
        }

        // iterate current partials
        for (RenderedPartial part : currentPartials.getPartials()) {

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

    /**
     * Refresh list of partials to display in component
     */
    public void refreshMap() {

        // check if this method have not been called few milliseconds before
        if (checkMinimumRenderInterval() == false) {
            return;
        }

        // on thread at a time render map for now
        if (lock.tryLock() == false) {
            System.err.println("Already rendering !");
            return;
        }

        try {

            //System.out.println("Render task launched");

            // get component size
            Dimension dim = CachedMapPane.this.getSize();

            if (dim.width < 1 || dim.height < 1) {
                System.out.println("Screen bounds too small");
                return;
            }

            // search which partials are necessary to display
            currentPartials = partialFactory.intersect(worldPosition, dim, map.getCoordinateReferenceSystem(),
                    () -> {
                        // each time a partial come, map will be repaint
                        CachedMapPane.this.repaint();
                    });

            // repaint component
            repaint();

        } finally {
            lock.unlock();
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
        return partialFactory.getPartialSidePx();
    }

    /**
     * Set the size in world unit of the map rendered on each partial
     * <p>
     * It can be used as a "zoom" value
     *
     * @param side
     */
    public void setPartialSideWu(double side) {
        partialFactory.setPartialSideWu(side);
    }

    /**
     * Get the size in degrees of the map rendered on each partial
     * <p>
     * It can be used as a "zoom" value
     */
    public double getPartialSideDg() {
        return partialFactory.getPartialSideWu();
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
}
