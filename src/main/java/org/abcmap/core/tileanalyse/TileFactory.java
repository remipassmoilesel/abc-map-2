package org.abcmap.core.tileanalyse;

import org.abcmap.core.clipboard.ClipboardListener;
import org.abcmap.core.events.ClipboardEvent;
import org.abcmap.core.events.TileFactoryEvent;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.utils.Utils;
import org.abcmap.core.utils.listeners.HasListenerHandler;
import org.abcmap.core.utils.listeners.ListenerHandler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Create tiles from images or files and add it to the active layer.
 * <p>
 * Thread are processed in a separated thread.
 * <p>
 * Images are stored in temorary files before process.
 */
public class TileFactory extends ManagerTreeAccessUtil implements HasListenerHandler<TileFactoryListener>, ClipboardListener {

    private static final CustomLogger logger = LogManager.getLogger(TileFactory.class);

    /**
     * Waiting list of files to proceed
     */
    protected ArrayList<Path> waitingList;

    /**
     * If set to true, crop will be enabled
     */
    protected Boolean activateCropping;

    /**
     * Rectangle that should be applied on image as a crop
     */
    protected Rectangle cropRectangle;

    /**
     * Analyse waiting list
     */
    private TileFactoryAnalyser waitingListAnalyser;

    /**
     * Count instances of factory to make different names for temp files
     */
    private static int instances = 0;

    /**
     * Id of factory used to make different names for temp files
     */
    private int factoryId;

    /**
     * Prefix used on temporary file names
     */
    private String tempFilePrefix;

    /**
     * Transmit events when tiles are ready
     */
    private ListenerHandler<TileFactoryListener> listenerHandler;

    // stats
    protected int totalToImport;
    protected int ioErrors;
    protected int imported;
    protected int refused;

    public TileFactory() {

        instances++;
        this.factoryId = instances;
        this.tempFilePrefix = "waitingForTileMaker_n" + factoryId + "_";

        listenerHandler = new ListenerHandler<>();

        waitingList = new ArrayList<Path>();
        waitingListAnalyser = new TileFactoryAnalyser(this);
        activateCropping = false;
        cropRectangle = null;

        resetStats();
    }

    /**
     * Reset statistics
     */
    protected void resetStats() {
        totalToImport = 0;
        ioErrors = 0;
        imported = 0;
        refused = 0;
    }

    /**
     * Return true if this factory is currently working
     *
     * @return
     */
    public boolean isWorking() {
        return waitingList.size() >= 0 || waitingListAnalyser.isWorking();
    }

    /**
     * Fire an event from this object
     *
     * @param name
     */
    protected void fireEvent(String name) {

        TileFactoryEvent ev = new TileFactoryEvent(name, null);
        ev.setImported(imported);
        ev.setTotalToImport(totalToImport);
        ev.setRefused(refused);
        ev.setIoErrors(ioErrors);

        listenerHandler.fireEvent(ev);
    }

    /**
     * Add an image to the waiting list
     *
     * @param img
     */
    public void add(BufferedImage img) {

        // add image and check if it is well saved
        if (addAndSaveBufferedImage(img) == false) {
            ioErrors++;
            return;
        }

        // stats
        totalToImport++;

        // start analyse only if needed
        startAnalyseIfNeeded();

        // fire an event to notify observers
        fireEvent(TileFactoryEvent.WAITING_LIST_CHANGED);

    }

    /**
     * Save an image in temp files and add it to waiting list
     *
     * @param img
     * @return
     */
    private boolean addAndSaveBufferedImage(BufferedImage img) {

        Path tempPath = null;
        try {
            tempPath = tempm().createTemporaryFile(tempFilePrefix, ".png");
            Utils.writeImage(img, tempPath);

            waitingList.add(tempPath);
            return true;
        } catch (IOException e) {
            logger.error(e);
        }

        return false;

    }

    /**
     * Start analyse but only it was not started before
     */
    protected synchronized void startAnalyseIfNeeded() {
        if (waitingListAnalyser.isWorking() == false) {
            waitingListAnalyser.start();
        }
    }

    /**
     * Add a list of images
     *
     * @param images
     */
    public void add(ArrayList<BufferedImage> images) {

        // iterate and save images
        for (BufferedImage img : images) {
            if (addAndSaveBufferedImage(img) == true) {
                totalToImport++;
            } else {
                ioErrors++;
            }
        }

        // start analyse
        startAnalyseIfNeeded();

        // notify observers
        fireEvent(TileFactoryEvent.WAITING_LIST_CHANGED);

    }

    /**
     * Add specified files to the waiting list
     *
     * @param files
     */
    public void addFiles(ArrayList<Path> files) {

        // iterate files and add it to waiting list
        for (Path toCopy : files) {

            try {

                // copy files
                Path target = tempm().createTemporaryFile(tempFilePrefix, ".jpg");
                Files.copy(toCopy, target);

                // add to waiting list
                waitingList.add(target);

                totalToImport++;
            }

            // error while writing files
            catch (IOException e) {
                logger.error(e);
                ioErrors++;
            }
        }

        // start analyse if needed
        startAnalyseIfNeeded();

        // notify observers
        fireEvent(TileFactoryEvent.WAITING_LIST_CHANGED);

    }

    /**
     * Return current waiting list
     * <p>
     * THis method MUST return a live list
     *
     * @return
     */
    protected ArrayList<Path> getWaitingList() {
        return waitingList;
    }

    /**
     * Set to true to enable cropping on imported tiles
     *
     * @param val
     */
    public void enableCropping(Boolean val) {
        this.activateCropping = val;
    }

    /**
     * Stop analyse as soon as possible, if it was started
     */
    public void stopImportLater() {
        if (waitingListAnalyser != null && waitingListAnalyser.isWorking() == false) {
            waitingListAnalyser.stop();
        }
    }

    /**
     * Set the rectangle will be used for crop images
     *
     * @param cropRectangle
     */
    public void setCropRectangle(Rectangle cropRectangle) {
        this.cropRectangle = new Rectangle(cropRectangle);
    }

    @Override
    public ListenerHandler<TileFactoryListener> getListenerHandler() {
        return listenerHandler;
    }

    /**
     * Delete all temporary files associated with this factory
     */
    protected void cleanTempFiles() {

        ArrayList<Path> tempDir = tempm().getTemporaryFilesFromCurrentProject();
        for (Path file : tempDir) {
            if (file.toString().contains(tempFilePrefix)) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    logger.error(e);
                }
            }
        }

    }

    @Override
    public void clipboardChanged(ClipboardEvent event) {
        if (ClipboardEvent.NEW_IMAGE.equals(event.getName())) {
            add((BufferedImage) event.getValue());
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        cleanTempFiles();
    }

}
