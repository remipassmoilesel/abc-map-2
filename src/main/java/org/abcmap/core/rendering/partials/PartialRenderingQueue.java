package org.abcmap.core.rendering.partials;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.listeners.ListenerHandler;
import org.abcmap.gui.utils.GuiUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.renderer.RenderListener;
import org.geotools.renderer.lite.StreamingRenderer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represent a succession of partial rendering operations
 * <p>
 * Each time a map is renderer, all partial rendering operations are stored in this object to be executed in separated threads.
 */
class PartialRenderingQueue {

    private static final CustomLogger logger = LogManager.getLogger(ListenerHandler.class);

    /**
     * List of partials which processing is already scheduled
     * <p>
     * This list is shared between all rendering queues of software
     */
    private static final ArrayList<RenderedPartial> partialsInProgress = new ArrayList<>(10);

    /**
     * If true, Geotools renderer is active
     */
    private boolean renderingActive;

    /**
     * If set to true, rendering will be stopped as soon as possible
     */
    private boolean stopRendering;

    /**
     * Map content associated with rendering queue
     */
    private final MapContent mapContent;

    /**
     * Debug id
     */
    private final long queueId;

    /**
     * Interval in ms between two execution of optional update callback
     */
    private final long updateIntervalMs;

    /**
     * Indicator of last time of execution of update callback
     */
    private long lastUpdateRun;

    /**
     * Geotools renderer associated with Partial rendering queue
     */
    private final StreamingRenderer renderer;

    /**
     * Store where rendered partial are keep.
     */
    private final RenderedPartialStore store;

    /**
     * Optional callback called every time an update occur to partial
     */
    private final Runnable toRunWhenPartialsUpdated;

    /**
     * Rendered dimension
     */
    private final double renderedWidthPx;

    /**
     * Rendered dimension
     */
    private final double renderedHeightPx;

    /**
     * List of tasks to render
     */
    private ArrayList<PartialRenderingTask> tasks;

    // debug information
    private boolean debugMode = true;
    private static int debugFontSize = 12;
    private static int debugIncr = debugFontSize + 5;
    private static Font debugFont = new Font("Dialog", Font.BOLD, debugFontSize);

    private static long queueNumber = 0;
    private static long partialsLoadedFromDatabase = 0;
    private static long partialsRendered = 0;
    private static long taskNumber = 0;

    PartialRenderingQueue(MapContent content, RenderedPartialStore store, double renderedWidthPx,
                          double renderedHeightPx, Runnable toRunWhenPartialsUpdated) {

        queueNumber++;
        this.queueId = queueNumber;

        this.stopRendering = false;

        this.tasks = new ArrayList<>();
        this.store = store;
        this.renderedWidthPx = renderedWidthPx;
        this.renderedHeightPx = renderedHeightPx;
        this.toRunWhenPartialsUpdated = toRunWhenPartialsUpdated;
        this.mapContent = content;

        this.updateIntervalMs = 50;
        this.lastUpdateRun = -1;

        this.renderingActive = false;

        this.renderer = GeoUtils.buildRenderer(new RenderListener() {
            @Override
            public void featureRenderer(SimpleFeature feature) {

                // notify each time features are rendered, but not too much time
                if (System.currentTimeMillis() - lastUpdateRun > updateIntervalMs) {
                    try {
                        if (toRunWhenPartialsUpdated != null) {
                            toRunWhenPartialsUpdated.run();
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    lastUpdateRun = System.currentTimeMillis();
                }
            }

            @Override
            public void errorOccurred(Exception e) {
                logger.error(e);
            }
        });
        renderer.setMapContent(mapContent);


    }

    /**
     * Add a partial to this queue. An image will be added to this partial,
     * <p>
     * extracted from database or a new rendered one if nothing is found.
     *
     * @param part
     */
    public void addTask(RenderedPartial part) {

        partialsInProgress.add(part);

        // set partial outdated on start, in case rendering is stopped
        part.setOutdated(true);

        this.tasks.add(new PartialRenderingTask(part));

    }

    /**
     * Start processing queue in a separated thread
     */
    public void start() {
        ThreadManager.runLater(() -> {

            // iterate tasks to render
            for (PartialRenderingTask task : tasks) {

                // run task
                try {
                    task.run();
                } catch (Exception e) {
                    logger.error(e);
                }

                task.markAsFinished();

                // stop all if requested, and mark partial as outdated
                if (stopRendering == true) {
                    task.getPartial().setOutdated(true);
                    break;
                }

                // else mark partial as up to date
                else {
                    task.getPartial().setOutdated(false);
                }
            }

        });
    }


    /**
     * Stop rendering, as soon as possible
     */
    public void stopRendering() {

        // activate flag
        stopRendering = true;

        if (debugMode) {
            logger.warning("Stop rendering. Queue:  " + queueId + " Task: " + taskNumber);
        }

        // remove all current partials from list
        for (PartialRenderingTask task : new ArrayList<>(tasks)) {
            partialsInProgress.remove(task.getPartial());
        }

        // stop eventual current rendering
        if (renderingActive) {
            try {
                renderer.stopRendering();
            } catch (Exception e) {
                // sometimes exceptions are raised when stop
                logger.debug(e);
            }

            renderingActive = false;
        }

    }

    /**
     * Return true if all rendering tasks are finished
     *
     * @return
     */
    public boolean isFinished() {
        for (PartialRenderingTask task : tasks) {
            if (task.isFinished() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return number of task that queue have to process
     *
     * @return
     */
    public int size() {
        return tasks.size();
    }

    /**
     * If set to true, more information will be displayed
     *
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * Return number of partials loaded from database
     *
     * @return
     */
    public static long getPartialsLoadedFromDatabase() {
        return partialsLoadedFromDatabase;
    }

    /**
     * Return number of partials rendered
     *
     * @return
     */
    public static long getPartialsRendered() {
        return partialsRendered;
    }

    /**
     * Return true if specified partial should be processed soon
     *
     * @param toCheck
     * @return
     */
    public static boolean isRenderInProgress(RenderedPartial toCheck) {

        if (toCheck == null) {
            return false;
        }

        for (RenderedPartial part : new ArrayList<>(partialsInProgress)) {
            if (part.equals(toCheck)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Return number of partials waiting for rendering
     *
     * @return
     */
    public static int getWaitingPartialsNumber() {
        return partialsInProgress.size();
    }

    /**
     * Special runnable used to render partials
     */
    private class PartialRenderingTask implements Runnable {

        private static final byte INITIALIZED = 0;
        private static final byte FINISHED = 1;

        /**
         * Current status of partial
         */
        protected byte status;

        /**
         * Partial we have to render
         */
        private final RenderedPartial partial;

        PartialRenderingTask(RenderedPartial partial) {
            this.status = INITIALIZED;
            this.partial = partial;
        }

        /**
         * Rendering task
         */
        @Override
        public void run() {

            //
            // Never return before removing current tile from inProgressList()
            // >> Use finally() clause below
            //
            try {

                GuiUtils.throwIfOnEDT();

                taskNumber++;

                if (debugMode) {
                    logger.warning("Launching rendering task. Queue:  " + queueId + " Task: " + taskNumber);
                }

                // stop rendering if needed
                if (stopRendering == true) {
                    return;
                }

                // try to find existing partial in database
                boolean loadedFromDatabase = false;
                long renderTime = -1;
                try {
                    loadedFromDatabase = store.updatePartialFromDatabase(partial);
                } catch (SQLException e) {
                    logger.error(e);
                }

                // partial have been successful loaded
                if (loadedFromDatabase == true) {
                    partialsLoadedFromDatabase++;
                }

                // partial cannot be loaded, create a new one
                else {

                    // stop rendering if needed
                    if (stopRendering == true) {
                        return;
                    }

                    ReferencedEnvelope partialWorldBounds = partial.getEnvelope();

                    // select image where render map
                    int imgWidth = (int) renderedWidthPx;
                    BufferedImage img = null;

                    // previous image if required
                    if(partial.isToRedraw() == true){
                        img = partial.getImage();
                    }

                    // or create a new one
                    else {
                        img = new BufferedImage(imgWidth, imgWidth, BufferedImage.TYPE_INT_ARGB);

                        // set image now, to draw it on map while rendering
                        partial.setImage(img, imgWidth, imgWidth);
                    }

                    partial.setToRedraw(false);

                    // get layer and CRS
                    Layer layer = mapContent.layers().get(0);
                    CoordinateReferenceSystem layerCrs = layer.getFeatureSource().getSchema().getCoordinateReferenceSystem();

                    // check crs, if different transform envelope to destination CRS
                    if (partialWorldBounds.getCoordinateReferenceSystem().equals(layerCrs) == false) {

                        try {
                            partialWorldBounds = partialWorldBounds.transform(layerCrs, true);
                        } catch (TransformException | FactoryException e) {
                            logger.error(e);
                        }

                    }

                    // get graphics from image and improve drawing quality
                    Graphics2D g2d = (Graphics2D) img.getGraphics();
                    GuiUtils.applyQualityRenderingHints(g2d);

                    // draw image
                    renderingActive = true;
                    long before = System.currentTimeMillis();
                    try {
                        renderer.paint(g2d, new Rectangle(imgWidth, imgWidth), partialWorldBounds);
                    } finally {
                        renderTime = System.currentTimeMillis() - before;
                        renderingActive = false;
                    }

                    // stop if requested, before store partial
                    if (stopRendering == true) {
                        return;
                    }

                    // or store rendered partial in database
                    else {

                        partialsRendered++;

                        // set partial up to date here, just after rendering, to prevent multiple insertions in database
                        // this property should be normally set after, in task management, but it is too late and it can disturb
                        // multithreading process
                        partial.setOutdated(false);

                        try {
                            store.addPartial(partial);
                        } catch (SQLException e) {
                            logger.error(e);
                        }

                    }

                    //GuiUtils.showImage(img);

                }

                // display debug information on partial if needed
                if (debugMode) {

                    // get graphics from partial
                    BufferedImage img = partial.getImage();
                    ReferencedEnvelope bounds = partial.getEnvelope();
                    Graphics2D g2d = (Graphics2D) img.getGraphics();

                    // improve quality of painting
                    GuiUtils.applyQualityRenderingHints(g2d);

                    // informations to display
                    String[] lines = new String[]{
                            "Partial id: " + partial.getDebugId(),
                            "DB id: " + partial.getDatabaseId(),
                            "Image id:" + System.identityHashCode(partial.getImage()),
                            "MinX: " + bounds.getMinX(),
                            "MinY: " + bounds.getMinY(),
                            "MaxX: " + bounds.getMaxX(),
                            "MaxY: " + bounds.getMaxY(),
                            "Width: " + bounds.getWidth(),
                            "Loaded from DB: " + loadedFromDatabase,
                            "RenderTime: " + renderTime + " ms",
                            "Layer id: " + partial.getLayerId(),
                    };

                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, (int) (renderedWidthPx - 20), debugIncr * (lines.length + 1));

                    g2d.setColor(Color.black);
                    g2d.setFont(debugFont);

                    // draw lines below each others
                    int i = debugIncr;
                    for (String l : lines) {
                        g2d.drawString(l, 20, i);
                        i += debugIncr;
                    }

                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(0, 0, (int) (renderedWidthPx - 20), debugIncr * (lines.length + 1));

                }

            } finally {

                partialsInProgress.remove(partial);

                // notify changes
                if (toRunWhenPartialsUpdated != null) {
                    toRunWhenPartialsUpdated.run();
                }
            }
        }

        /**
         * Return true if task is finished
         *
         * @return
         */
        public boolean isFinished() {
            return status == FINISHED;
        }

        /**
         * Set task as finished
         */
        public void markAsFinished() {
            status = FINISHED;
        }

        /**
         * Get partial to render
         *
         * @return
         */
        public RenderedPartial getPartial() {
            return partial;
        }
    }
}
