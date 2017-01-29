package org.abcmap.ielements.importation;

import org.abcmap.core.resources.MapImportException;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.InvalidInputException;
import org.abcmap.gui.components.importation.CropDimensionsPanel;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.utils.KeyAdapter;
import org.abcmap.ielements.InteractionElement;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

public abstract class AbstractSelectCropArea extends InteractionElement {

    private CropDimensionsPanel cropPanel;
    private TextFieldsUpdater textfieldsUpdater;
    private String mode;

    public AbstractSelectCropArea(String mode) {

        this.mode = mode;

        this.label = "Recadrage des images";
        this.help = "Sélectionnez ci-dessous l'aire qui sera recadrée lors des imports.";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        // set up a panel with crop controls
        cropPanel = new CropDimensionsPanel(CropDimensionsPanel.Mode.WITH_VISUAL_CONFIG_BUTTON);
        cropPanel.addListener(new TextfieldListener());

        // listen clicks on button
        cropPanel.getBtnVisualConfig().addActionListener(new VisualCropConfigLauncher());

        // listen clicks on checkbox
        cropPanel.enableCropActivationListener(true);

        // update text fields from configuration
        textfieldsUpdater = new TextFieldsUpdater();

        notifm.addEventListener(textfieldsUpdater);
        configm().getNotificationManager().addObserver(this);
        projectm().getNotificationManager().addObserver(this);

        // first update
        textfieldsUpdater.run();

        return cropPanel;

    }

    /**
     * Listen configuration and update text fields
     */
    private class TextFieldsUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();

            // get current rectangle
            Rectangle newRect = configm().getCropRectangle();

            // get values from form
            Rectangle rect = new Rectangle();
            try {
                rect = cropPanel.getRectangle();
            } catch (InvalidInputException e) {
                logger.debug(e);
            }

            // updates values if they are differents
            if (Utils.safeEquals(rect, newRect) == false) {
                cropPanel.updateValuesWithoutFire(newRect);
            }

        }

    }

    /**
     * Listen user input and update configuration
     */
    private class TextfieldListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent ev) {

            // get input from user
            Rectangle rect = null;
            try {
                rect = cropPanel.getRectangle();
            } catch (InvalidInputException e) {
                logger.debug(e);
                return;
            }

            // update values only if they are different
            if (rect.equals(configm().getCropRectangle()) == false) {
                configm().setCropRectangle(rect);
                configm().fireConfigurationUpdated();
            }

        }

    }

    /**
     * Launch visual crop configuration
     */
    private class VisualCropConfigLauncher implements ActionListener, Runnable {

        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadManager.runLater(this);
        }

        @Override
        public void run() {

            GuiUtils.throwIfOnEDT();

            try {
                try {
                    importm().startCropAreaConfiguration(mode);
                } catch (MapImportException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {

                importm().stopCropConfiguration();

                // message d'erreur
                dialm().showErrorInDialog("Impossible de lancer la configuration visuelle."
                                + "<br>Si vous souhaitez importer à partir d'un dossier, vérifiez que le dossier "
                                + "existe et vérifiez qu'il contient bien des images.",
                        false);
            }

        }

    }

}
