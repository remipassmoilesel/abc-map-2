package org.abcmap.gui.windows.crop;

import org.abcmap.core.events.ConfigurationEvent;
import org.abcmap.core.managers.ConfigurationManager;
import org.abcmap.core.managers.GuiManager;
import org.abcmap.core.managers.ImportManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.notifications.HasNotificationManager;
import org.abcmap.core.notifications.Notification;
import org.abcmap.core.notifications.NotificationManager;
import org.abcmap.core.notifications.UpdatableByNotificationManager;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.windows.FullScreenPictureWindow;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Area;

/**
 * Window used to configure automatic crop for screen capture
 */
public class CropConfigurationWindow extends FullScreenPictureWindow implements HasNotificationManager {

    private ImportManager importm;
    private NotificationManager observer;

    private WindowListener closeWL;
    private CropDimensionsDialog dialog;
    private ConfigurationManager configm;
    private CropSelectionRectangle selection;
    private CropSelectionTool selectionTool;
    private GuiManager guim;
    private float transparencyCoeff;
    private Area veilAroundSelection;

    public CropConfigurationWindow() {

        this.importm = MainManager.getImportManager();
        this.configm = MainManager.getConfigurationManager();
        this.guim = MainManager.getGuiManager();

        this.selection = new CropSelectionRectangle();

        // transparency around selection
        transparencyCoeff = 0.6f;

        // tracing tool
        this.selectionTool = new CropSelectionTool(this);
        imagePane.addMouseListener(selectionTool);
        imagePane.addMouseMotionListener(selectionTool);

        // listen closing to stop configuration
        this.closeWL = new WindowListener();
        this.addWindowListener(closeWL);

        // dialog with numerical values of selection
        this.dialog = new CropDimensionsDialog(this);
        dialog.addWindowListener(closeWL);

        // listen configuration changes
        this.observer = new NotificationManager(this);
        observer.setDefaultUpdatableObject(new CropSelectionUpdater());

    }

    /**
     * Update GUI if configuration change
     */
    private class CropSelectionUpdater implements UpdatableByNotificationManager, Runnable {

        @Override
        public void notificationReceived(Notification arg) {
            if (arg instanceof ConfigurationEvent) {
                SwingUtilities.invokeLater(this);
            }
        }

        @Override
        public void run() {

            // get dimension and refresh GUI
            Rectangle r = configm.getCropRectangle();
            dialog.refresh();
            updateVisualSelection(r);
            refreshImagePane();
        }

    }

    /**
     * Show/hide all crop configuration windows and start/stop crop configuration
     *
     * @param value
     */
    @Override
    public void setVisible(boolean value) {

        GuiUtils.throwIfNotOnEDT();

        super.setVisible(value);

        // show all
        if (value) {

            configm.getNotificationManager().addObserver(this);

            updateVisualSelection(configm.getCropRectangle());

            this.toFront();

            dialog.moveToDefaultPosition();

            dialog.setVisible(true);

            this.repaint();
        }

        // hide all
        else {

            dialog.setVisible(false);

            configm.getNotificationManager().removeObserver(CropConfigurationWindow.this);

            ThreadManager.runLater(new Runnable() {
                @Override
                public void run() {
                    importm.stopCropConfiguration();
                }
            });
        }

    }

    /**
     * Draw a "veil" over image to represent user selection
     *
     * @param g2d
     */
    @Override
    protected void paintImagePane(Graphics2D g2d) {
        super.paintImagePane(g2d);

        // draw transparent veil
        Graphics2D g2dT = (Graphics2D) g2d.create();
        GuiUtils.applyQualityRenderingHints(g2dT);
        g2dT.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparencyCoeff));
        g2dT.setColor(Color.white);
        g2dT.fill(veilAroundSelection);

        // draw rectangle
        selection.draw(g2d, null);
    }

    /**
     * Compute size of veil to show user crop selection
     */
    public void computeVeilShape() {

        Dimension d = imagePane.getSize();

        veilAroundSelection = new Area(new Rectangle(0, 0, d.width, d.height));

        veilAroundSelection.subtract(new Area(selection.getBounds()));
    }

    /**
     * Update GUI from specified selection rectangle
     *
     * @param r
     */
    public void updateVisualSelection(Rectangle r) {

        // do not update when action in progress
        if (selectionTool.isDrawing() == false && selectionTool.isResizing() == false) {

            Rectangle transR = transformToScreenSpace(r);

            selection.setPosition(transR.getLocation());
            selection.setDimensions(transR.getSize());

        }

        selection.refreshShape();
        computeVeilShape();

    }

    /**
     * Valid selection and save it
     */
    public void validVisualSelection() {

        Rectangle selectionRect = selection.getBounds();

        selectionRect = transformToImageSpace(selectionRect);

        configm.setCropRectangle(selectionRect);

        computeVeilShape();

    }

    /**
     * Hide veil and selection by move them out of screen
     */
    public void hideSelection() {
        selection.setPosition(new Point(-50, -50));
        selection.setDimensions(new Dimension(10, 10));
        selection.refreshShape();
    }

    private class WindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent arg0) {
            setVisible(false);
        }
    }

    public CropSelectionRectangle getSelection() {
        return selection;
    }

    @Override
    public NotificationManager getNotificationManager() {
        return observer;
    }

}
