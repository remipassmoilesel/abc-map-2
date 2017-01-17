package org.abcmap.core.rendering;

import org.abcmap.core.events.CacheRenderingEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.rendering.partials.RenderedPartial;
import org.abcmap.core.rendering.partials.RenderedPartialFactory;
import org.abcmap.core.rendering.partials.RenderedPartialQueryResult;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Render a map by cut it in several partials. When partials are rendered,
 * they are stored in database in order to be reused, to prevent resources consumption.
 * <p>
 * // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
 * // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
 * // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
 */
public class CachedRenderingEngine implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(CachedRenderingEngine.class);

    /**
     * Default size in pixel of each partial
     */
    public static final double DEFAULT_PARTIAL_SIZE_PX = 500d;

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
     * Minimal size in world unit of map rendered on partial
     */
    private double minPartialSizeWu;

    /**
     * Maximal size in world unit of map rendered on partial
     */
    private double maxPartialSizeWu;

    /**
     * Current side of a partial
     */
    private final double partialSizePx;

    /**
     * Last time of rendering in ms
     */
    private long lastRender = -1;

    /**
     * Current value of rendered map in partials. In world unit.
     */
    private double partialSizeYwu;

    /**
     * Lock to prevent too much thread rendering
     */
    private final ReentrantLock renderLock;


    /**
     * Project associated with this panel
     */
    private final Project project;

    /**
     * If set to true, additional information will be displayed on map
     */
    private boolean debugMode = false;

    /**
     * Rendered surface size
     */
    private Dimension renderedSizePx;

    /**
     * Utility used to trasnform coordinates from world bounds to screen bounds.
     * <p>
     * This utility use different world bounds than those are requested to render, because
     * <p>
     * rendered bounds are always greater
     */
    private AffineTransform worldToScreenTransform;

    /**
     * Utility used to transform coordinates from screen bounds to world bounds.
     * <p>
     * This utility use different world bounds than those are requested to render, because
     * <p>
     * rendered bounds are always greater
     */
    private AffineTransform screenToWorldTransform;

    private final EventNotificationManager notifm;

    public CachedRenderingEngine(Project p) {
        this.project = p;
        this.renderLock = new ReentrantLock();
        this.partialFactories = new HashMap<>();
        this.layerMapContents = new HashMap<>();
        this.currentPartials = new HashMap<>();

        // default partial size in pixel
        this.partialSizePx = DEFAULT_PARTIAL_SIZE_PX;

        // default world envelope
        this.worldEnvelope = project.getMaximumBounds();

        // first display, use default values
        this.renderedSizePx = new Dimension(1000, 1000);
        this.minPartialSizeWu = 0.0001d;
        this.maxPartialSizeWu = 90;
        this.partialSizeYwu = maxPartialSizeWu;

        // listen partial store changes and retransmit events
        this.notifm = new EventNotificationManager(this);
        notifm.setDefaultListener((ev) -> {
            // TODO: check if event concern current area
            notifm.fireEvent(ev);
        });
        project.getRenderedPartialStore().getNotificationManager().addObserver(this);

    }

    @Override
    protected void finalize() throws Throwable {
        // remove observer on finalizing
        if (project != null) {
            project.getRenderedPartialStore().getNotificationManager().removeObserver(this);
        }
    }

    public void paint(Graphics2D g2d) {

        // set graphics properties used for debug mode
        if (debugMode) {
            g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        }

        // paint background
        g2d.setColor(project.getBackgroundColor());
        g2d.fillRect(0, 0, renderedSizePx.width, renderedSizePx.height);

        for (AbmAbstractLayer lay : project.getLayersList()) {

            RenderedPartialQueryResult partialCtr = currentPartials.get(lay.getId());

            // list of layer changed before refreshMap called
            if (partialCtr == null) {
                logger.debug("Partials are null " + lay.getId());
                continue;
            }

            // set transparency (in other graphics !)
            Graphics2D g2dT = (Graphics2D) g2d.create();
            g2dT.setComposite(GuiUtils.createTransparencyComposite(lay.getOpacity()));

            AffineTransform worldToScreenTransform = getWorldToScreenTransform();

            // iterate current partials
            for (RenderedPartial part : partialCtr.getPartials()) {

                // compute position of tile on map
                ReferencedEnvelope ev = part.getEnvelope();
                Point2D.Double worldPos = new Point2D.Double(ev.getMinX(), ev.getMaxY());
                Point2D screenPos = worldToScreenTransform.transform(worldPos, null);

                int x = (int) Math.round(screenPos.getX());
                int y = (int) Math.round(screenPos.getY());
                int w = part.getRenderedWidth();
                int h = part.getRenderedHeight();

                // draw partial with transparency
                g2dT.drawImage(part.getImage(), x, y, w, h, null);

                if (debugMode) {

                    g2d.setColor(Color.darkGray);
                    g2d.drawRect(x, y, w, h);

                    // show index on partial
                    g2d.setColor(Color.BLACK);
                    String index = "#" + partialCtr.getPartials().indexOf(part);
                    g2d.drawString(index, x + w / 2, y + h / 2 + 30);

                }

            }

            // draw maximums bounds asked if necessary
            if (debugMode) {
                Point2D.Double ulc = new Point2D.Double(worldEnvelope.getMinX(), worldEnvelope.getMaxY());
                Point2D wp = worldToScreenTransform.transform(ulc, null);
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.red);
                g2d.drawRect((int) wp.getX(), (int) wp.getY(), 3, 3);
            }
        }
    }

    /**
     * Prepare map to paint
     * <p>
     * /!\ World envelope reference is bottom left corner
     * /!\ Screen reference is upper left corner
     *
     * @param worldEnvelope
     * @param pixelDim
     * @throws RenderingException
     */
    public void prepareMap(ReferencedEnvelope worldEnvelope, Dimension pixelDim) throws RenderingException {

        // on thread at a time renderer map for now
        if (renderLock.tryLock() == false) {
            logger.debug("Abort rendering operations, rendering is already in progress");
            return;
        }

        try {

            if (worldEnvelope == null || pixelDim == null) {
                throw new NullPointerException("Invalid parameter: " + worldEnvelope + " / " + pixelDim);
            }

            if (worldEnvelope.getWidth() < 0) {
                throw new RenderingException("Invalid envelope: " + worldEnvelope);
            }

            if (worldEnvelope.getHeight() < 0) {
                throw new RenderingException("Invalid envelope: " + worldEnvelope);
            }

            if (pixelDim.width < 0 || pixelDim.height < 0) {
                throw new RenderingException("Invalid dimensions: " + pixelDim);
            }

            if (worldEnvelope.getCoordinateReferenceSystem().equals(project.getCrs()) == false) {
                throw new RenderingException("Coordinate Reference Systems are different: " + worldEnvelope.getCoordinateReferenceSystem() + " / " + project.getCrs());
            }

            // check if this method have not been called few milliseconds before
            if (checkMinimumRenderInterval() == false) {
                logger.debug("Ask rendering too early: " + lastRender);
                return;
            }

            // set essential parameters after verifications
            this.worldEnvelope = worldEnvelope;
            this.renderedSizePx = pixelDim;
            this.partialSizeYwu = computePartialSizeWu(worldEnvelope);

            // check minimal and maximum size of map
            checkMinAndMaxPartialSizeWu(partialSizeYwu);

            logger.debug("Rendering component: " + this);

            RenderedPartialQueryResult firstResults = null;

            // iterate layers, sorted by z-index
            for (AbmAbstractLayer lay : project.getLayersList()) {

                String layId = lay.getId();

                // Retrieve map content associated with layer. If content does no exist, create one
                // This map content contains all layers, but only one is visible.
                // This is useful to use Geotools corrections between layers
                MapContent map = layerMapContents.get(layId);
                if (map == null) {
                    map = project.buildMapContent(layId);
                    layerMapContents.put(layId, map);
                }

                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)
                // TODO: check if layers have not changed (e.g.: new tile added to tile layer)

                // retrieve partial factory associated with layer
                RenderedPartialFactory factory = partialFactories.get(layId);
                if (factory == null) {
                    factory = new RenderedPartialFactory(project.getRenderedPartialStore(), map, layId);
                    factory.setDebugMode(debugMode);
                    partialFactories.put(layId, factory);
                }

                // stop eventual previous rendering tasks, if they are not in current result
                RenderedPartialQueryResult oldPartials = currentPartials.get(layId);
                if (oldPartials != null) {
                    oldPartials.stopRendering();
                }

                // search which partials are necessary to display
                RenderedPartialQueryResult newPartials = factory.intersect(worldEnvelope, partialSizeYwu,
                        () -> {

                            // notify observers that new partials come
                            firePartialsUpdated();

                            // notify thread waiters that new partial come
                            synchronized (CachedRenderingEngine.this) {
                                CachedRenderingEngine.this.notifyAll();
                            }

                        });

                // store it to draw it later
                currentPartials.put(layId, newPartials);

                if (firstResults == null) {
                    firstResults = newPartials;
                }
            }

            if (firstResults == null) {
                throw new RenderingException("Invalid bounds, no results founds");
            }

            // TODO: which transformation use ? First layer, active layer ?
            // update transforms
            worldToScreenTransform = firstResults.getWorldToScreenTransform();
            screenToWorldTransform = firstResults.getScreenToWorldTransform();


        } finally {
            renderLock.unlock();
        }
    }


    private void checkMinAndMaxPartialSizeWu(double partialSizeWu) {

        if (partialSizeWu < minPartialSizeWu) {
            this.partialSizeYwu = minPartialSizeWu;
        }

        if (partialSizeWu > maxPartialSizeWu) {
            this.partialSizeYwu = maxPartialSizeWu;
        }
    }

    /**
     * Set the minimum size of rendered map on this partial, in world unit
     *
     * @param minPartialSizeWu
     */
    public void setMinPartialSizeWu(double minPartialSizeWu) {
        this.minPartialSizeWu = minPartialSizeWu;
    }

    /**
     * Set the maximum size of rendered map on this partial, in world unit
     *
     * @param maxPartialSizeWu
     */
    public void setMaxPartialSizeWu(double maxPartialSizeWu) {
        this.maxPartialSizeWu = maxPartialSizeWu;
    }

    /**
     * Fire an event meaning that new partials came, or some repainted
     */
    private void firePartialsUpdated() {
        notifm.fireEvent(new CacheRenderingEvent(CacheRenderingEvent.PARTIALS_UPDATED));
    }

    /**
     * Get last world to screen transform generated
     *
     * @return
     */
    public AffineTransform getWorldToScreenTransform() {
        return worldToScreenTransform;
    }

    /**
     * Get last screen to world transform generated
     *
     * @return
     */
    public AffineTransform getScreenToWorldTransform() {
        return screenToWorldTransform;
    }

    /**
     * Compute size of map rendered on partials
     *
     * @param env
     * @return
     */
    private double computePartialSizeWu(ReferencedEnvelope env) {
        double coeff = renderedSizePx.getWidth() / partialSizePx;
        double worldWidth = env.getMaxX() - env.getMinX();
        return worldWidth / coeff;
    }

    /**
     * Get current world envelope
     *
     * @return
     */
    public ReferencedEnvelope getWorldEnvelope() {
        return new ReferencedEnvelope(worldEnvelope);
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

    /**
     * Return size of each partial rendered in pixel
     *
     * @return
     */
    public double getPartialSizePx() {
        return partialSizePx;
    }

    /**
     * If set to true, more information will be displayed
     *
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    /**
     * Get size of each partials in world unit
     *
     * @return
     */
    public double getPartialSizeYwu() {
        return partialSizeYwu;
    }

    /**
     * Get a coefficient between world unit and pixel unit
     *
     * @return
     */
    public double getScale() {
        return partialSizeYwu / partialSizePx;
    }

    /**
     * Block current thread until all work of rendering is done
     */
    public synchronized void waitForRendering() {

        // iterate all sets of partials
        Iterator<String> keys = currentPartials.keySet().iterator();

        while (keys.hasNext()) {
            String k = keys.next();
            RenderedPartialQueryResult v = currentPartials.get(k);

            // wait until all sets are ready
            while (v.isWorkDone() == false) {
                try {
                    wait(20);
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }

        }

    }

}
