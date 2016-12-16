package org.abcmap.core.rendering;

import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbstractLayer;
import org.abcmap.core.rendering.partials.RenderedPartial;
import org.abcmap.core.rendering.partials.RenderedPartialFactory;
import org.abcmap.core.rendering.partials.RenderedPartialQueryResult;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by remipassmoilesel on 15/12/16.
 */
public class CacheRenderingEngine implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CacheRenderingEngine.class);

    /**
     * Minimal size in world unit of rendered map on partial
     * <p>
     * This value should prevent partial side to be negative
     */
    public static final double MIN_PARTIAL_SIDE_WU = 1d;

    /**
     * Default size in pixel of each partial
     */
    public static final double DEFAULT_PARTIAL_SIDE_PX = 500d;

    /**
     * List of map content associated with layers
     */
    private final HashMap<String, MapContent> layerMapContents;
    /**
     * List of factories employed to renderer map. Each factory renderer one layer
     */
    private final HashMap<String, RenderedPartialFactory> partialFactories;

    /**
     * Current set of partials that have to be painted
     */
    private HashMap<String, RenderedPartialQueryResult> currentPartials;

    /**
     * Last world envelope (positions) of map rendered on panel
     */
    private ReferencedEnvelope worldEnvelope;

    /**
     * Minimum interval between rendering in ms
     */
    private long renderMinIntervalMs = 50;

    /**
     * Current side of a partial
     */
    private final double partialSidePx;

    /**
     * Last time of rendering in ms
     */
    private long lastRender = -1;

    /**
     * Current value of rendered map in partials. In world unit.
     */
    private double partialSideWu;

    /**
     * Lock to prevent too much thread rendering
     */
    private final ReentrantLock renderLock;


    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * If set to true, additional informations will be displayed on map
     */
    private boolean debugMode = true;

    /**
     * Rendered surface size
     */
    private Dimension renderedSizePx;

    private final EventNotificationManager notifm;


    public CacheRenderingEngine(Project p) {
        this.project = p;
        this.renderLock = new ReentrantLock();
        this.partialFactories = new HashMap<>();
        this.layerMapContents = new HashMap<>();
        this.currentPartials = new HashMap<>();

        // default partial size in pixel
        this.partialSidePx = DEFAULT_PARTIAL_SIDE_PX;

        // default world envelope
        this.worldEnvelope = new ReferencedEnvelope();

        // first time set fake pixel dimensions
        this.renderedSizePx = new Dimension(800, 800);

        // high value first
        this.partialSideWu = 500;

        // listen partial store changes
        this.notifm = new EventNotificationManager(this);
        project.getRenderedPartialsStore().getNotificationManager().addObserver(this);

    }

    @Override
    protected void finalize() throws Throwable {
        // remove observer on finalizing
        project.getRenderedPartialsStore().getNotificationManager().removeObserver(this);
    }

    public void paint(Graphics2D g2d) {

        if (renderLock.isLocked()) {
            logger.debug("Render is in progress, avoid painting");
            return;
        }

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
     * Set the reference position of map at ULC corner of component
     *
     * @param ulc
     */
    public void prepareMap(Point2D ulc, Dimension pixelDim, double partialSideWu) throws RenderingException {

        // get width and height in world unit
        double wdg = partialSideWu * pixelDim.width / partialSidePx;
        double hdg = partialSideWu * pixelDim.height / partialSidePx;

        // create a new envelope
        double x1 = ulc.getX();
        double y1 = ulc.getY() - hdg; // to BLC
        double x2 = ulc.getX() + wdg;
        double y2 = ulc.getY();

        prepareMap(new ReferencedEnvelope(x1, x2, y1, y2, project.getCrs()), pixelDim, partialSideWu);
    }

    public void prepareMap(ReferencedEnvelope worldEnvelope, Dimension pixelDim, double partialSideWu) throws RenderingException {

        /*
        System.out.println();
        System.out.println("worldEnvelope");
        System.out.println(worldEnvelope);
        System.out.println("pixelDim");
        System.out.println(pixelDim);
        System.out.println("partialSideWu");
        System.out.println(partialSideWu);
        */

        if (worldEnvelope.getMaxX() - worldEnvelope.getMinX() < 0) {
            throw new RenderingException("Invalid envelope: " + worldEnvelope);
        }

        if (worldEnvelope.getMaxY() - worldEnvelope.getMinY() < 0) {
            throw new RenderingException("Invalid envelope: " + worldEnvelope);
        }

        if (pixelDim.width < 0 || pixelDim.height < 0) {
            throw new RenderingException("Invalid dimensions: " + pixelDim);
        }

        if (worldEnvelope.getCoordinateReferenceSystem().equals(project.getCrs()) == false) {
            throw new RenderingException("Coordinate Reference Systems are different: " + worldEnvelope.getCoordinateReferenceSystem() + " / " + project.getCrs());
        }

        if (partialSideWu < MIN_PARTIAL_SIDE_WU || Double.isInfinite(partialSideWu) || Double.isNaN(partialSideWu)) {
            throw new RenderingException("Invalid partial side world unit value: " + partialSideWu);
        }

        // check if this method have not been called few milliseconds before
        if (checkMinimumRenderInterval() == false) {
            return;
        }

        // on thread at a time renderer map for now
        if (renderLock.tryLock() == false) {
            System.err.println("Already rendering !");
            return;
        }

        // set essential parameters after verifications
        this.worldEnvelope = worldEnvelope;
        this.renderedSizePx = pixelDim;
        this.partialSideWu = partialSideWu;

        try {

            logger.debug("Rendering component: " + this);

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
                RenderedPartialQueryResult newPartials = factory.intersect(worldEnvelope, partialSideWu,
                        () -> {
                            // each time a partial come, map will be repaint
                            notifm.fireEvent(new RenderingEvent(RenderingEvent.NEW_PARTIAL_LOADED));
                        });

                // store it to draw it later
                currentPartials.put(layId, newPartials);
            }


        } finally {
            renderLock.unlock();
        }
    }

    /**
     * Compute optimal partial size in world unit, in order to show map on whole component.
     * <p>
     * If component is not visible, fake size of component will be used and result can look weird
     */
    public static double getOptimalPartialSideWu(ReferencedEnvelope world, Dimension surfacePx, double partialSidePx) {

        double worldWidth = world.getMaxX() - world.getMinX();
        double renderedSurfaceWidth = surfacePx.getWidth();

        return worldWidth * partialSidePx / renderedSurfaceWidth;

    }

    /**
     * Adapt rendering parameters to render all map
     */
    public void setParametersToRenderWholeMap() {

        worldEnvelope = project.getMaximumBounds();

        System.out.println();
        System.out.println(worldEnvelope);
        System.out.println(renderedSizePx);
        System.out.println(partialSidePx);

        partialSideWu = getOptimalPartialSideWu(worldEnvelope, renderedSizePx, partialSidePx);
    }

    public ReferencedEnvelope getWorldEnvelope() {
        return worldEnvelope;
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

    public double getPartialSidePx() {
        return partialSidePx;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public double getPartialSideWu() {
        return partialSideWu;
    }


}
