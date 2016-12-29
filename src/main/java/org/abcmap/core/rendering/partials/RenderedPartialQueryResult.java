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

    public AffineTransform getWorldToScreenTransform() {
        if (worldToScreenTransform == null) {
            worldToScreenTransform = RendererUtilities.worldToScreenTransform(worldBounds, screenBounds);
        }
        return worldToScreenTransform;
    }

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

    public int getPartialsLoaded() {
        return partialsLoaded;
    }

    public boolean isRenderProcessFinished() {
        return renderingQueue.isFinished();
    }

    public ArrayList<RenderedPartial> getPartials() {
        return partials;
    }

    public boolean isWorkDone() {
        return renderingQueue.isFinished();
    }
}
