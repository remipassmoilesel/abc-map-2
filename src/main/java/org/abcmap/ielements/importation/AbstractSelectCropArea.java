package org.abcmap.ielements.importation;

import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.components.importation.CropDimensionsPanel;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.gui.utils.KeyAdapter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

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

        cropPanel = new CropDimensionsPanel(CropDimensionsPanel.Mode.WITH_VISUAL_CONFIG_BUTTON);
        cropPanel.addListener(new TextfieldListener());

        cropPanel.getBtnVisualConfig().addActionListener(
                new VisualCropConfigLauncher());

        cropPanel.activateCroppingListener(true);

        textfieldsUpdater = new TextFieldsUpdater();

        notifm.addEventListener(textfieldsUpdater);
        configm().getNotificationManager().addObserver(this);
        projectm().getNotificationManager().addObserver(this);

        textfieldsUpdater.run();

        return cropPanel;


    }

    private class TextFieldsUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();
            /*

            // recuperer les nouvelles données
            Rectangle newRect = configm.getCropRectangle();

            // recuperer les données du formulaire
            Rectangle rect = new Rectangle();
            try {
                rect = cropPanel.getRectangle();
            } catch (InvalidInputException e) {
                // Log.debug(e);
            }

            // metter à jour le formulaire si les donnees sont differentes
            if (Utils.safeEquals(rect, newRect) == false) {
                cropPanel.updateValuesWithoutFire(newRect);
            }
            */

        }

    }


    private class TextfieldListener extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {

            /*
            // recuperer les valeurs saisies
            Rectangle rect = null;
            try {
                rect = cropPanel.getRectangle();
            } catch (InvalidInputException e1) {
                return;
            }

            // changer si valeurs différentes
            if (rect.equals(configm.getCropRectangle()) == false) {
                configm.setCropRectangle(rect);
            }
            */

        }

    }


    private class VisualCropConfigLauncher implements ActionListener, Runnable {

        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadManager.runLater(this);
        }

        @Override
        public void run() {

            /*
            try {
                importm.startCropAreaConfiguration(mode);
            } catch (IOException | MapImportException e1) {

                // arreter la configuration
                importm.stopCropConfiguration();

                // message d'erreur
                guim.showErrorInDialog(
                        guim.getMainWindow(),
                        "Impossible de lancer la configuration visuelle."
                                + "<br>Si vous souhaitez importer à partir d'un dossier, vérifiez que le dossier "
                                + "existe et vérifiez qu'il contient bien des images.",
                        false);
            }
            */
        }

    }

}
