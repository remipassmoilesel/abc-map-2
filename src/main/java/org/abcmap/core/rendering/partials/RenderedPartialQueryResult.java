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

    private int tileNumberWidth;
    private int tileNumberHeight;
    private ArrayList<RenderedPartial> partials;

    private AffineTransform screenToWorldTransform;
    private AffineTransform worldToScreenTransform;

    public RenderedPartialQueryResult(ArrayList<RenderedPartial> partials, ReferencedEnvelope worldBounds, Rectangle screenBounds,
                                      int tileNumberWidth, int tileNumberHeight) {

        this.tileNumberWidth = tileNumberWidth;
        this.tileNumberHeight = tileNumberHeight;
        this.partials = partials;

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

    public int getTileNumberWidth() {
        return tileNumberWidth;
    }

    public int getTileNumberHeight() {
        return tileNumberHeight;
    }

    public ArrayList<RenderedPartial> getPartials() {
        return partials;
    }
}
