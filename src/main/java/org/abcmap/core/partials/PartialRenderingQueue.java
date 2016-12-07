package org.abcmap.core.partials;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;
import org.geotools.renderer.lite.StreamingRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Represent a succession of partial rendering operations
 * <p>
 * Each time a map is render, all partial rendering operations are stored in this object to be executed in a separated thread.
 * <p>
 * Each queue have his own StreamingRenderer to avoid multi-threading issues
 */
class PartialRenderingQueue {

    /**
     * List of partials which processing is already scheduled
     */
    private static final ArrayList<RenderedPartial> partialsInProgress = new ArrayList<>(10);

    private static long loadedFromDatabase = 0;
    private static long renderedPartials = 0;

    /**
     * Return true if specified partial should be processed soon
     *
     * @param area
     * @return
     */
    public static boolean isRenderInProgress(ReferencedEnvelope area) {

        for (RenderedPartial part : partialsInProgress) {
            if (part.getEnvelope().equals(area)) {
                return true;
            }
        }

        return false;
    }

    public static int getWaitingPartialsNumber(){
        return partialsInProgress.size();
    }

    private final StreamingRenderer renderer;
    private final RenderedPartialStore store;
    private final Runnable toNotifyWhenPartialsCome;
    private final int renderedWidthPx;
    private final int renderedHeightPx;

    private ArrayList<Runnable> tasks;

    PartialRenderingQueue(MapContent content, RenderedPartialStore store, int renderedWidthPx, int renderedHeightPx, Runnable toNotifyWhenPartialsCome) {
        this.tasks = new ArrayList<>();
        this.store = store;
        this.renderedWidthPx = renderedWidthPx;
        this.renderedHeightPx = renderedHeightPx;
        this.toNotifyWhenPartialsCome = toNotifyWhenPartialsCome;

        this.renderer = GeoUtils.buildRenderer();
        renderer.setMapContent(content);
    }

    /**
     * Add a partial to this queue. An image will be added to this partial, extracted from database or a new rendered one if nothing is found.
     *
     * @param part
     */
    public void addTask(RenderedPartial part) {

        partialsInProgress.add(part);

        this.tasks.add(() -> {

            // try to find existing partial in database
            boolean exist = false;
            try {
                exist = store.updatePartialFromDatabase(part);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if(exist == true){
                loadedFromDatabase++;
            }

            // or create a new one
            else {

                renderedPartials++;

                ReferencedEnvelope bounds = part.getEnvelope();

                // create an image, and render map
                BufferedImage img = new BufferedImage(renderedWidthPx, renderedHeightPx, BufferedImage.TYPE_INT_ARGB);

                renderer.paint((Graphics2D) img.getGraphics(), new Rectangle(renderedWidthPx, renderedHeightPx), bounds);

                // keep image
                part.setImage(img, renderedWidthPx, renderedHeightPx);

                try {
                    store.addPartial(part);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            partialsInProgress.remove(part);

            // notify of new tile arrival
            if (toNotifyWhenPartialsCome != null) {
                toNotifyWhenPartialsCome.run();
            }

        });
    }

    /**
     * Start processing queue in a separated thread
     */
    public void start() {
        ThreadManager.runLater(() -> {
            for (Runnable task : tasks) {
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static long getLoadedFromDatabase() {
        return loadedFromDatabase;
    }

    public static long getRenderedPartials() {
        return renderedPartials;
    }
}
