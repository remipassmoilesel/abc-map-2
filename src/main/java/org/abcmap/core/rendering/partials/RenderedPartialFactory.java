package org.abcmap.core.rendering.partials;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.rendering.CachedRenderingEngine;
import org.abcmap.core.rendering.RenderingException;
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

    private static final CustomLogger logger = LogManager.getLogger(RenderedPartialFactory.class);

    private static long loadedPartialsReused = 0;

    /**
     * Id of layer associated with partials
     */
    private final String layerId;

    /**
     * Debug flag used to print CRS of layers to render in console
     */
    private final boolean printCrsToRender;

    /**
     * Associated map content
     */
    private MapContent mapContent;

    /**
     * Where are stored partials
     */
    private final RenderedPartialStore store;

    /**
     * Default size in px of each partial
     */
    private double partialSizePx;

    /**
     * Size of map rendered on partials, in world unit
     * <p>
     * This size is valid only for Y axis, X axis value should be equals or greater but
     * Geotools StreamingRenderer should compensate this
     */
    private double partialSizeYwu;

    /**
     * If set to true, additional information will be displayed on partials
     */
    private boolean debugMode = false;

    /**
     * @param store
     * @param content
     * @param layerId
     */
    public RenderedPartialFactory(RenderedPartialStore store, MapContent content, String layerId) {
        this.store = store;
        this.mapContent = content;
        this.layerId = layerId;
        this.partialSizePx = CachedRenderingEngine.DEFAULT_PARTIAL_SIZE_PX;

        this.printCrsToRender = false;
    }

    /**
     * If set to true, supplementary information will be displayed on partials
     *
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Get partials from Upper Left Corner (world) position with specified dimension
     * <p>
     * Using this method involves using setPartialSideWu() to specify 'scale' of each partial
     *
     * @param ulc
     * @param pixelDimension
     * @return
     */
    public RenderedPartialQueryResult intersect(Point2D ulc, Dimension pixelDimension, double partialSizeYWu, CoordinateReferenceSystem crs,
                                                Runnable toRunWhenPartialsUpdated) throws RenderingException {

        // check rendering values
        if (mapContent == null) {
            throw new RenderingException("Noting to renderer, map content is null");
        }

        if (pixelDimension.width < 1 || pixelDimension.height < 1) {
            throw new RenderingException("Invalid dimensions to render: " + pixelDimension);
        }

        if (Double.isInfinite(partialSizeYWu) || Double.isNaN(partialSizeYWu)) {
            throw new RenderingException("Invalid partial side world unit value: " + partialSizeYWu);
        }

        // get width and height in decimal dg
        double wdg = partialSizeYWu * pixelDimension.width / partialSizePx;
        double hdg = partialSizeYWu * pixelDimension.height / partialSizePx;

        // create a new envelope
        double x1 = ulc.getX();
        double y1 = ulc.getY() - hdg; // to BLC
        double x2 = ulc.getX() + wdg;
        double y2 = ulc.getY();

        ReferencedEnvelope env = new ReferencedEnvelope(x1, x2, y1, y2, crs);

        // create a new envelope
        return intersect(env, partialSizeYWu, toRunWhenPartialsUpdated);

    }

    /**
     * Get partials around a world envelope
     * <p>
     * Using this method involves using setPartialSideWu() to specify 'scale' of each partial
     *
     * @param worldBounds
     * @return
     */
    public RenderedPartialQueryResult intersect(ReferencedEnvelope worldBounds, double partialSizeYwu,
                                                Runnable toRunWhenPartialsUpdated) throws RenderingException {

        // check rendering values
        if (mapContent == null) {
            throw new RenderingException("Nothing to renderer, map content is null");
        }

        if (worldBounds == null) {
            throw new RenderingException("World bounds are null");
        }

        if (Double.isInfinite(partialSizeYwu) || Double.isNaN(partialSizeYwu)) {
            throw new RenderingException("Invalid partial side world unit value: " + partialSizeYwu);
        }

        // keep partial side size in world unit
        // this size is valid only for Y axis, X axis value should be equals or greater
        // but Geotools StreamingRenderer should compensate this
        this.partialSizeYwu = partialSizeYwu;

        // list of partials returned as a part of result
        ArrayList<RenderedPartial> rsparts = new ArrayList<>();

        // first position to go from
        // position is rounded in order to have partials that can be reused in future display
        double x = getStartPointFrom(worldBounds.getMinX(), partialSizeYwu);
        double y = getStartPointFrom(worldBounds.getMinY(), partialSizeYwu);

        PartialRenderingQueue renderingQueue = new PartialRenderingQueue(mapContent, store, partialSizePx, partialSizePx, toRunWhenPartialsUpdated);
        renderingQueue.setDebugMode(debugMode);

        // count how many tiles are already loaded
        int loaded = 0;

        // be sure that we have enough partials around selection
        double maxX = worldBounds.getMaxX();
        double maxY = worldBounds.getMaxY();

        // this can be interesting to load partials before user need it
        // (but maybe we have to load extra partials *after*)
        //double maxX = worldBounds.getMaxX() + partialSideWu;
        //double maxY = worldBounds.getMaxY() + partialSideWu;
        //x -= partialSideWu;
        //y -= partialSideWu;

        // iterate area to renderer from bottom left corner to upper right corner
        while (y < maxY) {

            // Compute needed area for next partial
            // CRS must be null, to prevent problems with different systems
            ReferencedEnvelope area = new ReferencedEnvelope(x, x + partialSizeYwu, y, y + partialSizeYwu, worldBounds.getCoordinateReferenceSystem());

            // check if bounds of partials are on layer
            // verification is done on Generic2D system, to prevent weird results (eg: ED50 (small domain) / WGS84)
            // TODO: check if partial is on area to render, to prevent render of part of layers out of bounds
            // TODO: (like with transformed shape file of France to ED50 system)

            // check if partial already exist and is already loaded
            RenderedPartial part = store.searchInLoadedList(layerId, area);

            if (RenderedPartial.isLoaded(part) || PartialRenderingQueue.isRenderInProgress(part)) {
                rsparts.add(part);

                loaded++;
                loadedPartialsReused++;
            }

            // partial does not exist or image is not loaded, create it
            else {

                // create a new partial only if needed
                if (part == null) {
                    part = new RenderedPartial(null, area, (int) partialSizePx, (int) partialSizePx, layerId);
                    store.addInLoadedList(part);
                }

                // create a task to retrieve or renderer image from map
                renderingQueue.addTask(part);

                rsparts.add(part);

            }

            // go to next
            x += partialSizeYwu;

            // change line when finished,
            // + partialSidePx to be sure that we stop after end of surface to render
            if (x > maxX) {

                y += partialSizeYwu;

                // reset x except the last loop
                if (y < maxY) {
                    x = getStartPointFrom(worldBounds.getMinX(), partialSizeYwu);
                }
            }

        }


        // if not enough tiles, return null to avoid errors on transformations
        if (rsparts.size() < 1) {
            return null;
        }

        // launch tasks to retrieve or produce partial in a separated thread
        renderingQueue.start();

        double w = worldBounds.getWidth();
        double h = worldBounds.getHeight();

        // compute real screen bounds of asked world area
        // given that we used fixed size partials, area can be larger than asked one
        Rectangle screenBounds = new Rectangle(0, 0,
                (int) Math.round(w * partialSizePx / partialSizeYwu),
                (int) Math.round(h * partialSizePx / partialSizeYwu));

        return new RenderedPartialQueryResult(rsparts, worldBounds, screenBounds, loaded, renderingQueue);
    }

    /**
     * Get the closest start point of specified coordinate.
     * <p>
     * Coordinates are normalized in order to have reusable partials
     * <p>
     * When extreme values are used, starting values can be weird
     *
     * @param coord
     * @return
     */
    public double getStartPointFrom(double coord, double sizeWu) {

        double mod = coord % sizeWu;

        if (mod < 0) {
            mod += sizeWu;
        }

        return Math.round((coord - mod) * 10000.0) / 10000.0;
    }

    /**
     * Return the size of partial in world unit
     *
     * @return
     */
    public double getPartialSizeYwu() {
        return partialSizeYwu;
    }

    /**
     * Return the size of partial in pixel
     *
     * @return
     */
    public double getPartialSizePx() {
        return partialSizePx;
    }

    public static long getLoadedPartialsReused() {
        return loadedPartialsReused;
    }

    public RenderedPartialStore getStore() {
        return store;
    }

    public void setMapContent(MapContent mapContent) {
        this.mapContent = mapContent;
    }
}
