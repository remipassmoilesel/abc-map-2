package org.abcmap.core.robot;

import org.abcmap.core.configuration.CFNames;
import org.abcmap.core.configuration.ConfigurationContainer;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.Main;
import org.abcmap.core.resources.MapImportException;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Utility used to catch screen
 */
public class ScreenCatcher implements Runnable {

    private static final CustomLogger logger = LogManager.getLogger(ScreenCatcher.class);

    /**
     * Robot object used to catch screen
     */
    private Robot robot;

    /**
     * Components we have to hide before catch screen
     */
    private ArrayList<Component> componentsToHide;

    /**
     * Components we have to show after catch screen
     * <p>
     * These components are stored in a separated list because some components we have to hide can be invisible, so they don't have to be shown again
     */
    private ArrayList<Component> componentsToShow;

    /**
     * Screen capture
     */
    private BufferedImage screenCapture;

    /**
     * If set to true, all components which have been hidden will be shown again
     */
    private boolean displayAgainAfterCatch = true;

    public ScreenCatcher(ArrayList<Component> comps) throws MapImportException {

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            logger.error(e);
            throw new MapImportException("Cannot instantiate capture robot", e);
        }

        this.componentsToHide = comps;
    }

    @Override
    public void run() {

        GuiUtils.throwIfOnEDT();

        // get configuration
        ConfigurationContainer config = Main.getConfigurationManager().getConfiguration();

        // hide components
        if (componentsToHide != null) {

            // list visible components to show them after capture
            componentsToShow = new ArrayList<>(componentsToHide.size());
            for (final Component c : componentsToHide) {

                if (c == null) {
                    continue;
                }

                if (c.isVisible() == true) {
                    componentsToShow.add(c);
                    try {
                        SwingUtilities.invokeAndWait(() -> {
                            c.setVisible(false);
                        });
                    } catch (InvocationTargetException | InterruptedException e) {
                        logger.error(e);
                    }
                }
            }

            // wait a little that components are hidden
            if (componentsToShow.size() > 0) {
                try {
                    Thread.sleep(config.getInt(CFNames.WINDOW_HIDDING_DELAY_MS));
                } catch (InterruptedException e) {
                    logger.error(e);
                }
            }

        }

        // catch screen
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        screenCapture = robot.createScreenCapture(new Rectangle(0, 0, screen.width, screen.height));

        // show hiddent components after
        if (componentsToShow != null && displayAgainAfterCatch) {
            SwingUtilities.invokeLater(() -> {
                for (Component c : componentsToShow) {
                    c.setVisible(true);
                }
            });
        }
    }

    /**
     * Return the screen capture which have been taken
     *
     * @return
     */
    public BufferedImage getScreenCapture() {
        return screenCapture;
    }

    public void setDisplayAgainAfterCatch(boolean displayAgainAfterCatch) {
        this.displayAgainAfterCatch = displayAgainAfterCatch;
    }
}