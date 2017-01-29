package org.abcmap.core.tileanalyse;

import org.abcmap.core.events.TileFactoryEvent;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.threads.ThreadManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyse a set of waiting files, make tiles and add it to the current active layer
 */
class TileFactoryAnalyser extends ManagerTreeAccessUtil implements Runnable {

    private static final CustomLogger logger = LogManager.getLogger(TileFactoryAnalyser.class);

    /**
     * Lock used to prevent uneeded call
     */
    private final ReentrantLock analyseLock;

    /**
     * Object used to find tile positions relative to each others
     */
    private final TileComposer tileComposer;

    /**
     * If set to true, import should be stopped as soon as possible
     */
    private boolean stopImport = false;

    /**
     * Tile factory where get waiting list
     */
    private TileFactory parent;

    public TileFactoryAnalyser(TileFactory maker) {
        this.parent = maker;
        this.tileComposer = null;//new TileComposer();
        this.analyseLock = new ReentrantLock();
    }

    /**
     * Stop analyse as soon as possible
     */
    public void stop() {
        stopImport = true;
    }

    /**
     * Start analyse if needed on a separated thread
     */
    public void start() {
        if (isWorking() == false) {
            stopImport = false;
            ThreadManager.runLater(this);
        }
    }

    /**
     * Return true if analyse is in progress
     *
     * @return
     */
    public boolean isWorking() {
        return analyseLock.isLocked();
    }

    @Override
    public void run() {

        if (analyseLock.tryLock() == false) {
            return;
        }
        try {

            // notify observers
            parent.fireEvent(TileFactoryEvent.IMPORT_STARTED);

            // reset flags
            stopImport = false;

            // grab waiting list (live reference)
            ArrayList<Path> waitingList = parent.getWaitingList();

            // get active layer where add tiles
            Project project = projectm().getProject();
            AbmAbstractLayer layer = project.getActiveLayer();

            // iterate images
            importing:
            while (waitingList.size() > 0 && stopImport == false) {

                // check if project is still initialized
                // If not, stop import
                if (projectm().isInitialized() == false ||
                        project.isLayerBelongToProject(layer) == false) {

                    parent.fireEvent(TileFactoryEvent.FATAL_EXCEPTION_HAPPEND);
                    stopImport = true;
                    break importing;
                }

                // remove image from list
                BufferedImage img;
                Path tempFile = waitingList.remove(0);
                try {
                    img = ImageIO.read(tempFile.toFile());
                } catch (IOException e1) {
                    logger.error(e1);
                    parent.ioErrors++;
                    parent.fireEvent(TileFactoryEvent.EXCEPTION_HAPPENED);
                    continue;
                }

                /*
                // construire une tuile
                Tile t = new Tile();
                try {
                    t.loadAndSaveImage(img,
                            parent.activateCropping ? parent.cropRectangle : null);
                }

                // erreur lors de la création
                catch (Exception e) {
                    Log.error(e);
                    parent.ioErrors++;
                    parent.fireEvent(ImportEvent.EXCEPTION_HAPPENED);
                    continue;
                }

                try {
                    // determiner la position de la tuile et changer sa position
                    tileAnalyser.analyseTileAndSetPosition(t);
                    t.refreshShape();

                    // ajout au calque actif
                    layer.addElement(t);

                    // enregistrement pour annulation
                    cancelm.addDrawOperation(layer, t).elementsHaveBeenAdded(true);

                    // statistiques
                    parent.imported++;

                    // notifications
                    parent.fireEvent(ImportEvent.WAITING_LIST_CHANGED);
                }

                // impossible positionner la tuile
                catch (TileAnalyseException e) {
                    Log.error(e);

                    // statistiques
                    parent.refused++;

                    // notifications
                    parent.fireEvent(ImportEvent.TILE_REFUSED);
                }
                */
            }

            // import was canceled
            if (stopImport) {
                parent.fireEvent(TileFactoryEvent.IMPORT_ABORTED);
            }

            // import has finished normally
            else {
                parent.fireEvent(TileFactoryEvent.IMPORT_FINISHED);
            }

            // clean temporary files at the end
            parent.cleanTempFiles();

            // reset stats
            parent.resetStats();

        } finally {
            analyseLock.unlock();
        }

    }


}
