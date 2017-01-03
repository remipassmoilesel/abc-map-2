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
 * Each time a map is renderer, all partial rendering operations are stored in this object to be executed in a separated thread.
 * <p>
 * Each queue have his own StreamingRenderer to avoid multi-threading issues
 */
class PartialRenderingQueue {

    /**
     * List of partials which processing is already scheduled
     * <p>
     * This list is shared between all rendering queues of software
     */
    private static final ArrayList<RenderedPartial> partialsInProgress = new ArrayList<>(10);

    private static final CustomLogger logger = LogManager.getLogger(ListenerHandler.class);

    private final MapContent mapContent;
    private final long queueId;

    private final long updateIntervalMs;
    private long lastUpdateRun;

    // debug information
    private boolean debugMode = true;
    private static int debugFontSize = 12;
    private static int debugIncr = debugFontSize + 5;
    private static Font debugFont = new Font("Dialog", Font.BOLD, debugFontSize);

    private static long queueNumber = 0;
    private static long loadedFromDatabase = 0;
    private static long renderedPartials = 0;
    private static long taskNumber = 0;


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

    private final StreamingRenderer renderer;
    private final RenderedPartialStore store;
    private final Runnable toRunWhenPartialsUpdated;
    private final double renderedWidthPx;
    private final double renderedHeightPx;

    private ArrayList<PartialRenderingTask> tasks;

    PartialRenderingQueue(MapContent content, RenderedPartialStore store, double renderedWidthPx, double renderedHeightPx, Runnable toRunWhenPartialsUpdated) {
        this.tasks = new ArrayList<>();
        this.store = store;
        this.renderedWidthPx = renderedWidthPx;
        this.renderedHeightPx = renderedHeightPx;
        this.toRunWhenPartialsUpdated = toRunWhenPartialsUpdated;
        this.mapContent = content;

        this.updateIntervalMs = 50;
        this.lastUpdateRun = -1;
        this.renderer = GeoUtils.buildRenderer(new RenderListener() {
            @Override
            public void featureRenderer(SimpleFeature feature) {

                // notify each time features are rendered, but not too much time
                if (System.currentTimeMillis() - lastUpdateRun > updateIntervalMs) {
                    try {
                        toRunWhenPartialsUpdated.run();
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

        queueNumber++;
        queueId = queueNumber;

    }

    /**
     * Add a partial to this queue. An image will be added to this partial, extracted from database or a new rendered one if nothing is found.
     *
     * @param part
     */
    public void addTask(RenderedPartial part) {

        partialsInProgress.add(part);

        this.tasks.add(new PartialRenderingTask() {

            @Override
            public void run() {

                GuiUtils.throwIfOnEDT();

                taskNumber++;

                if (debugMode) {
                    //logger.warning("Launching rendering task. Queue:  " + queueId + " Task: " + taskNumber);
                }

                try {
                    // try to find existing partial in database
                    boolean loadedFromDatabase = false;
                    long renderTime = -1;
                    try {
                        loadedFromDatabase = store.updatePartialFromDatabase(part);
                    } catch (SQLException e) {
                        logger.error(e);
                    }

                    if (loadedFromDatabase == true) {
                        PartialRenderingQueue.loadedFromDatabase++;
                    }

                    // or create a new one
                    else {

                        renderedPartials++;

                        ReferencedEnvelope partialWorldBounds = part.getEnvelope();

                        // create an image, and renderer map
                        int imgWidth = (int) renderedWidthPx;
                        BufferedImage img = new BufferedImage(imgWidth, imgWidth, BufferedImage.TYPE_INT_ARGB);

                        // set image now, to draw it on time
                        part.setImage(img, imgWidth, imgWidth);

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
                        long before = System.currentTimeMillis();
                        renderer.paint(g2d, new Rectangle(imgWidth, imgWidth), partialWorldBounds);
                        renderTime = System.currentTimeMillis() - before;

                        try {
                            store.addPartial(part);
                        } catch (SQLException e) {
                            logger.error(e);
                        }

                        //GuiUtils.showImage(img);

                    }

                    // mark as up to date
                    part.setOutdated(false);

                    // display debug information on partial if needed
                    if (debugMode) {

                        BufferedImage img = part.getImage();
                        ReferencedEnvelope bounds = part.getEnvelope();
                        Graphics2D g2d = (Graphics2D) img.getGraphics();

                        GuiUtils.applyQualityRenderingHints(g2d);

                        String[] lines = new String[]{
                                "Partial id: " + part.getDebugId(),
                                "DB id: " + part.getDatabaseId(),
                                "Image id:" + System.identityHashCode(part.getImage()),
                                "MinX: " + bounds.getMinX(),
                                "MinY: " + bounds.getMinY(),
                                "MaxX: " + bounds.getMaxX(),
                                "MaxY: " + bounds.getMaxY(),
                                "Width: " + bounds.getWidth(),
                                "Loaded from DB: " + loadedFromDatabase,
                                "RenderTime: " + renderTime + " ms",
                                "Layer id: " + part.getLayerId(),
                        };

                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(0, 0, (int) (renderedWidthPx - 20), debugIncr * (lines.length + 1));

                        g2d.setColor(Color.black);
                        g2d.setFont(debugFont);

                        int i = debugIncr;
                        for (String l : lines) {
                            g2d.drawString(l, 20, i);
                            i += debugIncr;
                        }

                        g2d.setColor(Color.GRAY);
                        g2d.drawRect(0, 0, (int) (renderedWidthPx - 20), debugIncr * (lines.length + 1));

                    }

                } finally {
                    partialsInProgress.remove(part);

                    // notify of new tile arrival
                    if (toRunWhenPartialsUpdated != null) {
                        toRunWhenPartialsUpdated.run();
                    }
                }
            }
        });

    }

    /**
     * Start processing queue in a separated thread
     */
    public void start() {
        ThreadManager.runLater(() -> {
            for (PartialRenderingTask task : tasks) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                task.markAsFinished();
            }
        });
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

    public static long getLoadedFromDatabase() {
        return loadedFromDatabase;
    }

    public static long getRenderedPartials() {
        return renderedPartials;
    }

    public int size() {
        return tasks.size();
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }


}
