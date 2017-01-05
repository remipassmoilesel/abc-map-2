package org.abcmap.core.rendering.partials;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.RendererUtilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

/**
 * Wrap result of a partial query. Contains the list of partials and associated information
 * <p>
 * A thing important to remember is that asked world bounds are not rendered bounds, rendered bounds are larger.
 */
public class RenderedPartialQueryResult {

    private static final CustomLogger logger = LogManager.getLogger(RenderedPartialQueryResult.class);

    private final int partialsLoaded;
    private final PartialRenderingQueue renderingQueue;
    private final ReferencedEnvelope worldBounds;
    private final Rectangle screenBounds;
    private ArrayList<RenderedPartial> partials;

    private AffineTransform screenToWorldTransform;
    private AffineTransform worldToScreenTransform;

    public RenderedPartialQueryResult(ArrayList<RenderedPartial> partials, ReferencedEnvelope worldBounds, Rectangle screenBounds,
                                      int partialsLoaded, PartialRenderingQueue renderingQueue) {

        this.partialsLoaded = partialsLoaded;
        this.partials = partials;
        this.renderingQueue = renderingQueue;
        this.worldBounds = worldBounds;
        this.screenBounds = screenBounds;

    }

    /**
     * Return a world to screen transform object corresponding to result set area.
     * <p>
     * Result set area can be greater than area asked.
     *
     * @return
     */
    public AffineTransform getWorldToScreenTransform() {
        if (worldToScreenTransform == null) {
            worldToScreenTransform = RendererUtilities.worldToScreenTransform(worldBounds, screenBounds);
        }
        return worldToScreenTransform;
    }

    /**
     * Return a screen to world transform object corresponding to result set area.
     * <p>
     * Result set area can be greater than area asked.
     *
     * @return
     */
    public AffineTransform getScreenToWorldTransform() {
        if (screenToWorldTransform == null) {
            try {
                screenToWorldTransform = getWorldToScreenTransform().createInverse();
            } catch (NoninvertibleTransformException e) {
                logger.error("Unable to invert transform: " + e);
            }
        }
        return screenToWorldTransform;
    }

    /**
     * Return number of partials already loaded (not being loaded from database or rendering) in this result set
     *
     * @return
     */
    public int getPartialsLoaded() {
        return partialsLoaded;
    }

    /**
     * @return
     */
    public ArrayList<RenderedPartial> getPartials() {
        return partials;
    }

    /**
     * Return true if rendering process is finished
     *
     * @return
     */
    public boolean isWorkDone() {
        return renderingQueue.isFinished();
    }

    /**
     * Stop current rendering, as soon as possible
     */
    public void stopRendering() {
        renderingQueue.stopRendering();
    }
}
