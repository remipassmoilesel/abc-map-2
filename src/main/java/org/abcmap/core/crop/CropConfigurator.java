package org.abcmap.core.crop;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.*;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.utils.GuiUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class CropConfigurator {

    private static final CustomLogger logger = LogManager.getLogger(CropConfigurator.class);

    private final ReentrantLock startLock;
    private final ReentrantLock stopLock;
    private ArrayList<Component> visibleFrames;
    private String mode;

    private ImportManager importm;
    private ConfigurationManager configm;

    private GuiManager guim;

    public static final String FOR_SCREEN_IMPORT = "FOR_SCREEN_IMPORT";

    public static final String FOR_DIRECTOY_IMPORT = "FOR_DIRECTOY_IMPORT";

    public CropConfigurator(String mode) {

        this.importm = MainManager.getImportManager();
        this.configm = MainManager.getConfigurationManager();
        this.guim = MainManager.getGuiManager();

        this.mode = mode;

        this.startLock = new ReentrantLock();
        this.stopLock = new ReentrantLock();
    }

    public void start() throws IOException {

        GuiUtils.throwIfOnEDT();

        if (startLock.tryLock() == false) {
            return;
        }

        try {

            BufferedImage bg = null;

            visibleFrames = MainManager.getGuiManager().getVisibleWindows();

            // configuration from directory
            if (CropConfigurator.FOR_DIRECTOY_IMPORT.equals(mode)) {

                File dir = new File(configm.getDirectoryImportPath());
                if (dir.isDirectory() == false) {
                    throw new IOException("Invalid directory: " + dir.getAbsolutePath());
                }

                // check extension
                File img = null;
                for (File f : dir.listFiles()) {
                    String ext = Utils.getExtension(f.getAbsolutePath());
                    if (importm.isValidExtensionsForTile(ext)) {
                        img = f;
                        break;
                    }
                }

                // read image
                try {
                    bg = ImageIO.read(img);
                } catch (Exception e) {
                    throw new IOException(e);
                }

                // hide windows
                for (Component c : visibleFrames) {
                    c.setVisible(false);
                }

                // wait a little windows are hiding
                try {
                    Thread.sleep(configm.getWindowHidingDelay());
                } catch (InterruptedException e) {
                    logger.error(e);
                }

            }

            // configuration on screen
            else if (CropConfigurator.FOR_SCREEN_IMPORT.equals(mode)) {
                bg = importm.catchScreen(visibleFrames, false);
            }

            // unknown mode
            else {
                throw new IllegalArgumentException("Invalid crop configuration mode: " + mode);
            }

            // enregistrer une taille image plus petite si necesaire

            // ouverture de la fenetre de selection de zone
            final BufferedImage finalBg = bg;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    guim.showCropWindow(finalBg);
                }
            });

        } finally {
            startLock.unlock();
        }

    }

    public void stop() {

        if (stopLock.tryLock() == false) {
            return;
        }

        try {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {

                    guim.getCropWindow().setVisible(false);

                    // wait a little
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        logger.error(e);
                    }

                    // restore windows
                    for (Component c : visibleFrames) {
                        c.setVisible(true);
                    }

                }
            });

        } finally {
            stopLock.unlock();
        }


    }

}