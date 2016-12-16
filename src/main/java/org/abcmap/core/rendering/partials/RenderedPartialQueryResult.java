package org.abcmap.core.rendering.partials;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.renderer.lite.RendererUtilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;

/**
 * Wrap result of a partial query. Contains the list of partials and associated affine transform.
 */
public class RenderedPartialQueryResult {

    private final int partialsLoaded;
    private final PartialRenderingQueue renderingQueue;
    private int partialNumberWidth;
    private int partialNumberHeight;
    private ArrayList<RenderedPartial> partials;

    private AffineTransform screenToWorldTransform;
    private AffineTransform worldToScreenTransform;

    public RenderedPartialQueryResult(ArrayList<RenderedPartial> partials, ReferencedEnvelope worldBounds, Rectangle screenBounds,
                                      int partialNumberWidth, int partialNumberHeight, int partialsLoaded, PartialRenderingQueue renderingQueue) {

        this.partialsLoaded = partialsLoaded;
        this.partialNumberWidth = partialNumberWidth;
        this.partialNumberHeight = partialNumberHeight;
        this.partials = partials;
        this.renderingQueue = renderingQueue;

        worldToScreenTransform = RendererUtilities.worldToScreenTransform(worldBounds, screenBounds);
        try {
            screenToWorldTransform = worldToScreenTransform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }

    }

    public AffineTransform getScreenToWorldTransform() {
        return screenToWorldTransform;
    }

    public AffineTransform getWorldToScreenTransform() {
        return worldToScreenTransform;
    }


    public int getPartialNumberHeight() {
        return partialNumberHeight;
    }

    public int getPartialNumberWidth() {
        return partialNumberWidth;
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
}
