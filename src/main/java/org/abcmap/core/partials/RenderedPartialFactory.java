package org.abcmap.core.partials;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Store and create partials
 * <p>
 * Partials are fixed size squares. Same degree value is used for partial height and width (e.g: 1 dg lat/lon) and this should cause issues but Geotools
 * renderer is supposed to compensate that (no deformation should appear)
 * <p>
 * Need more tests at several position
 * <p>
 */
public class RenderedPartialFactory {

    /**
     * Minimal size in world unit of rendered map on partial
     * <p>
     * This value should prevent partial side to be negative
     */
    public static final double MIN_PARTIAL_SIDE_WU = 0.1d;

    /**
     * Default size in pixel of each partial
     */
    public static final int DEFAULT_PARTIAL_SIDE_PX = 500;

    private static long loadedPartialsReused = 0;

    /**
     * Id of layer associated with partials
     */
    private final String layerId;

    /**
     * Associated map content
     */
    private MapContent mapContent;

    /**
     * Where are stored partials
     */
    private final RenderedPartialStore store;

    /**
     * Zoom level of current rendering
     */
    private double partialSideWu = 2d;

    /**
     * Default size in px of each partial
     */
    private int partialSidePx = DEFAULT_PARTIAL_SIDE_PX;

    private boolean debugMode = false;

    public RenderedPartialFactory(RenderedPartialStore store, MapContent content, String layerId) {
        this.store = store;
        this.mapContent = content;
        this.layerId = layerId;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Get partials from Upper Left Corner (world) position with specified dimension
     *
     * @param ulc
     * @param pixelDimension
     * @return
     */
    public RenderedPartialQueryResult intersect(Point2D ulc, Dimension pixelDimension, CoordinateReferenceSystem crs, Runnable toNotifyWhenPartialsCome) {

        // get width and height in decimal dg
        double wdg = partialSideWu * pixelDimension.width / partialSidePx;
        double hdg = partialSideWu * pixelDimension.height / partialSidePx;

        // create a new envelope
        double x1 = ulc.getX();
        double y1 = ulc.getY() - hdg; // to BLC
        double x2 = ulc.getX() + wdg;
        double y2 = ulc.getY();

        ReferencedEnvelope env = new ReferencedEnvelope(x1, x2, y1, y2, crs);

        // create a new envelope
        return intersect(env, toNotifyWhenPartialsCome);

    }

    /**
     * Get partials around a world envelope
     *
     * @param worldBounds
     * @return
     */
    public RenderedPartialQueryResult intersect(ReferencedEnvelope worldBounds, Runnable toNotifyWhenPartialsCome) {

        if (mapContent == null) {
            throw new NullPointerException("Noting to renderer, map content is null");
        }

        // keep the same value until end of rendering process, even if value is changed by setter
        double partialSideWu = normalizeWorldUnitSideValue(this.partialSideWu);

        ArrayList<RenderedPartial> rsparts = new ArrayList<>();

        // count partials
        int tileNumberW = 0;
        int tileNumberH = 0;

        // first position to go from
        // position is rounded in order to have partials that can be reused in future display
        double x = getStartPointFrom(worldBounds.getMinX());
        double y = getStartPointFrom(worldBounds.getMinY());

        PartialRenderingQueue renderingQueue = new PartialRenderingQueue(mapContent, store, partialSidePx, partialSidePx, toNotifyWhenPartialsCome);
        renderingQueue.setDebugMode(debugMode);

        // iterate area to renderer from bottom left corner to upper right corner
        while (y < worldBounds.getMaxY()) {

            // count horizontal partials only on the first line
            if (tileNumberH == 0) {
                tileNumberW++;
            }

            // compute needed area for next partial
            ReferencedEnvelope area = new ReferencedEnvelope(x, x + partialSideWu, y, y + partialSideWu, mapContent.getCoordinateReferenceSystem());

            // check if partial already exist and is already loaded
            RenderedPartial part = store.searchInLoadedList(layerId, area);

            if (RenderedPartial.isLoaded(part) || PartialRenderingQueue.isRenderInProgress(part)) {
                rsparts.add(part);
                loadedPartialsReused++;
            }

            // partial does not exist or image is not loaded, create it
            else {

                // create a new partial if needed
                if (part == null) {
                    part = new RenderedPartial(RenderedPartial.getWaitingImage(), area, partialSidePx, partialSidePx, layerId);
                    store.addInLoadedList(part);
                    rsparts.add(part);
                }

                // create a task to retrieve or renderer image from map
                renderingQueue.addTask(part);

            }

            // go to next
            x += partialSideWu;

            // change line when finished
            if (x > worldBounds.getMaxX()) {
                y += partialSideWu;
                tileNumberH++;

                // reset x except the last loop
                if (y < worldBounds.getMaxY()) {
                    x = getStartPointFrom(worldBounds.getMinX());
                }
            }

        }

        // launch tasks to retrieve or produce partial in a separated thread, if needed
        if (renderingQueue.size() > 0) {
            renderingQueue.start();
        }

        // if not enough tiles, return null to avoid errors on transformations
        if (rsparts.size() < 1) {
            return null;
        }

        double w = worldBounds.getWidth();
        double h = worldBounds.getHeight();

        // compute real screen bounds of asked world area
        // given that we used fixed size partials, area can be larger than asked one
        Rectangle screenBounds = new Rectangle(0, 0,
                (int) Math.round(w * partialSidePx / partialSideWu),
                (int) Math.round(h * partialSidePx / partialSideWu));

        return new RenderedPartialQueryResult(rsparts, worldBounds, screenBounds, tileNumberW, tileNumberH);
    }

    /**
     * Get the closest start point of specified coordinate.
     * <p>
     * Coordinates are normalized in order to have reusable partials
     *
     * @param coord
     * @return
     */
    public double getStartPointFrom(double coord) {

        double mod = coord % partialSideWu;
        if (mod < 0) {
            mod += partialSideWu;
        }

        double rslt = coord - mod;

        return Math.round(rslt * 10000.0) / 10000.0;
    }

    /**
     * Set rendered partial size in world unit
     * <p>
     * Partial size can be used as a "zoom" value
     *
     * @param
     */
    public void setPartialSideWu(double sideDg) {
        this.partialSideWu = normalizeWorldUnitSideValue(sideDg);
    }

    public int getPartialSidePx() {
        return partialSidePx;
    }

    /**
     * Get rendered partial size in world unit
     * <p>
     * Partial size can be used as a "zoom" value
     *
     * @param
     */
    public double getPartialSideWu() {
        return partialSideWu;
    }

    public static long getLoadedPartialsReused() {
        return loadedPartialsReused;
    }

    public RenderedPartialStore getStore() {
        return store;
    }

    /**
     * Check if a side value for partial (in world unit) is greater than the minimum value
     *
     * @param value
     * @return
     */
    public static double normalizeWorldUnitSideValue(double value) {

        if (value < MIN_PARTIAL_SIDE_WU) {
            value = MIN_PARTIAL_SIDE_WU;
        }

        return value;
    }

    public void setMapContent(MapContent mapContent) {
        this.mapContent = mapContent;
    }
}
