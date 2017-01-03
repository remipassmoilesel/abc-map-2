package org.abcmap.core.rendering;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.project.layers.AbmTileLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Renderer designed for Abc-Map projects
 * <p>
 * Nothing is cached, performances can be low
 */
public class PlainRenderingEngine {

    private static final CustomLogger logger = LogManager.getLogger(PlainRenderingEngine.class);

    /**
     * If set to true, outlines will be rendered on tiles layers
     */
    private boolean includeTileOutlines;
    /**
     * Final dimensions of rendered image
     */
    private Dimension renderedDimensions;
    /**
     * Map location to renderer
     */
    private ReferencedEnvelope mapBoundsToRender;
    /**
     * If set to true, time needed to renderer each layer will be logged
     */
    private boolean logStats = true;

    /**
     * Geotools streaming renderer, sed to renderer layers
     */
    private StreamingRenderer renderer;

    /**
     * Layers are rendered in pictures that are stored in this list
     */
    private ArrayList<BufferedImage> renderedImages;

    private ProjectManager pman;
    private Project project;

    public PlainRenderingEngine() {
        this.pman = Main.getProjectManager();
        this.project = pman.getProject();
        this.renderer = GeoUtils.buildRenderer();

        renderedImages = new ArrayList<>();

        renderedDimensions = null;
        mapBoundsToRender = null;
        includeTileOutlines = false;
    }

    /**
     * Render only one layer
     *
     * @param id index of layer in AbcMap project
     */
    public void renderLayer(int id) {

        /**
         * Here we have to build a map content at each time, to avoid synchronization problems on layer visibility
         */
        MapContent mapContent = project.buildGlobalMapContent(includeTileOutlines);
        List<Layer> geotoolsLayers = mapContent.layers();

        // monitor time of rendering
        long startRender = System.currentTimeMillis();

        // Hide other layers to optimize renderer time

        // Two kin of index are used because some AbcMap layers can contains many geotools layers,
        // for example TileLayer that have one raster layer and one shape layer
        int abcmapIndex = 0;
        int geotoolsIndex = 0;
        for (AbmAbstractLayer lay : project.getLayersList()) {

            Layer glay = geotoolsLayers.get(geotoolsIndex);

            if (abcmapIndex == id) {
                glay.setVisible(true);
            } else {
                glay.setVisible(false);
            }

            if (lay instanceof AbmTileLayer) {
                geotoolsIndex++;
                Layer outlines = geotoolsLayers.get(geotoolsIndex);
                glay.setVisible(abcmapIndex == id);
            }

            abcmapIndex++;
            geotoolsIndex++;

        }

        // create image and paint map content
        BufferedImage renderedImage = new BufferedImage(renderedDimensions.width, renderedDimensions.height,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = renderedImage.createGraphics();
        renderer.paint(g2d, new Rectangle(renderedDimensions), mapBoundsToRender);

        renderedImages.set(id, renderedImage);

        // display time of rendering
        long renderTime = System.currentTimeMillis() - startRender;

        if (logStats) {
            logger.debug("layer " + id + ": Render finished, " + renderTime + " ms");
        }

    }

    /**
     * Render a layer in a different thread
     *
     * @param id
     * @param whenFinished
     */
    public void renderLayerLater(int id, Runnable whenFinished) {

        ThreadManager.runLater(() -> {

            renderLayer(id);

            if (whenFinished != null) {
                whenFinished.run();
            }

        });
    }

    /**
     * Render all layers of project in a different thread
     *
     * @param whenFinished
     */
    public void renderAllLayersLater(Runnable whenFinished) {
        ThreadManager.runLater(() -> {
            for (int i = 0; i < project.getLayersList().size(); i++) {
                renderLayer(i);
            }

            if (whenFinished != null) {
                whenFinished.run();
            }
        });

    }

    /**
     * True if outlines of tile layers are rendered
     *
     * @return
     */
    public boolean isIncludeTileOutlines() {
        return includeTileOutlines;
    }

    /**
     * Return final dimensions of rendered image
     *
     * @return
     */
    public Dimension getRenderedDimensions() {
        return renderedDimensions;
    }

    /**
     * Return rendered map bounds
     *
     * @return
     */
    public ReferencedEnvelope getMapBoundsToRender() {
        return mapBoundsToRender;
    }

    /**
     * Set to true to include outlines of tile layers in renderer
     *
     * @param includeTileOutlines
     */
    public void setIncludeTileOutlines(boolean includeTileOutlines) {
        this.includeTileOutlines = includeTileOutlines;
    }

    /**
     * Set the map bounds to renderer
     *
     * @param mapBoundsToRender
     */
    public void setMapBoundsToRender(ReferencedEnvelope mapBoundsToRender) {
        this.mapBoundsToRender = mapBoundsToRender;
    }

    /**
     * All rendered image are stored here
     *
     * @return
     */
    public ArrayList<BufferedImage> getRenderedImages() {
        return renderedImages;
    }

}
