package org.abcmap.core.render;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layer.AbstractLayer;
import org.abcmap.core.project.layer.TileLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Renderer specially designed for projects
 */
public class ProjectRenderer {

    private static final CustomLogger logger = LogManager.getLogger(ProjectRenderer.class);

    /**
     * If set to true, outlines will be rendered on tiles layers
     */
    private boolean includeTileOutlines;
    /**
     * Final dimensions of rendered image
     */
    private Dimension renderedDimensions;
    /**
     * Map location to render
     */
    private ReferencedEnvelope mapBoundsToRender;
    /**
     * If set to true, time needed to render each layer will be logged
     */
    private boolean logStats = true;
    /**
     * Lock used to avoid uneeded call of renderer
     */
    private ReentrantLock renderLock;

    /**
     * Geotools streaming renderer, sed to render layers
     */
    private StreamingRenderer renderer;

    /**
     * Layers are rendered in pictures that are stored in this list
     */
    private ArrayList<BufferedImage> renderedImages;

    private ProjectManager pman;
    private Project project;

    public ProjectRenderer() {
        this.pman = MainManager.getProjectManager();
        this.project = pman.getProject();
        this.renderLock = new ReentrantLock();
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
        MapContent mapContent = project.buildMapContent(includeTileOutlines);
        List<Layer> geotoolsLayers = mapContent.layers();

        // monitor time of rendering
        long startRender = System.currentTimeMillis();

        // Hide other layers to optimize render time

        // Two kin of index are used because some AbcMap layers can contains many geotools layers,
        // for example TileLayer that have one raster layer and one shape layer
        int abcmapIndex = 0;
        int geotoolsIndex = 0;
        for (AbstractLayer lay : project.getLayers()) {

            Layer glay = geotoolsLayers.get(geotoolsIndex);

            if (abcmapIndex == id) {
                glay.setVisible(true);
            } else {
                glay.setVisible(false);
            }

            if (lay instanceof TileLayer) {
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
            for (int i = 0; i < project.getLayers().size(); i++) {
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
     * Set to true to include outlines of tile layers in render
     *
     * @param includeTileOutlines
     */
    public void setIncludeTileOutlines(boolean includeTileOutlines) {
        this.includeTileOutlines = includeTileOutlines;
    }

    /**
     * Set the map bounds to render
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
