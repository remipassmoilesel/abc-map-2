package org.abcmap.ielements.importation.robot;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.importation.CaptureAreaSelectionPanel;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.gui.utils.FormUpdater;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelectCaptureArea extends InteractionElement {

    private CaptureAreaSelectionPanel capturePanel;
    private Updater formUpdater;
    private MemoryPanelUpdater memoryIndicatorUpdater;

    public SelectCaptureArea() {
        label = "Zone de capture";
        help = "Sélectionnez ci-dessous en hauteur et largeur la zone de capture du logiciel. "
                + "Pour une zone de 5 par 5, le logiciel déplacera l'écran 5 fois à droite par ligne, "
                + "pour 5 lignes.";
    }

    @Override
    protected Component createPrimaryGUI() {

        formUpdater = new Updater();
        configm().getNotificationManager().addObserver(this);
        notifm.setDefaultListener(formUpdater);

        JPanel panel = new JPanel(new MigLayout("insets 0"));

        capturePanel = new CaptureAreaSelectionPanel();
        panel.add(capturePanel, wrap15());

        memoryIndicatorUpdater = new MemoryPanelUpdater();
        capturePanel.getListenerHandler().add(new ConfigurationUpdater());
        capturePanel.getListenerHandler().add(memoryIndicatorUpdater);

        formUpdater.run();

        return panel;

    }

    private class Updater extends FormUpdater {
        @Override
        protected void updateFields() {
            super.updateFields();

            /*
            // récuperer la configuration courante
            Dimension dim = configm.getRobotImportCaptureArea();

            // récupérer les dimensions courantes de l'objet
            Dimension currentDim = capturePanel.getValues();

            // changer les valeurs si nécéssaire
            if (Utils.safeEquals(dim, currentDim) == false) {
                capturePanel.updateValuesWithourFire(dim.width, dim.height);
            }


            memoryIndicatorUpdater.updateMemoryIndicator();
            */
        }
    }

    private class ConfigurationUpdater implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            /*
            // récuperer le parametre en cours
            Dimension currentDim = configm.getRobotImportCaptureArea();

            // parametre selectionne
            Dimension selectedDim = capturePanel.getValues();

            if (Utils.safeEquals(currentDim, selectedDim) == false) {
                configm.setRobotImportCaptureArea(selectedDim);
            }
            */

        }

    }

    private class MemoryPanelUpdater implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateMemoryIndicator();
        }

        public void updateMemoryIndicator() {

            /*
            // récupérer la taille de l'aire de capture
            Dimension areaDimension = capturePanel.getValues();

            // erreur de saisie, retour
            if (areaDimension == null) {
                return;
            }

            // calculer la taille d'une capture
            // si le recadrage est activé, une capture = un recadrage
            Dimension captureDimPx = null;
            if (configm.isCroppingEnabled()) {
                captureDimPx = configm.getCropRectangle().getSize();
            }

            // sinon une capture équivaut à la taille de l'écran
            else {
                captureDimPx = Toolkit.getDefaultToolkit().getScreenSize();
            }

            // enlever la valeur de recouvrement
            Float covering = configm.getRobotImportCovering();
            captureDimPx.width = captureDimPx.width - Math.round(captureDimPx.width * covering * 2);
            captureDimPx.height = captureDimPx.height - Math.round(captureDimPx.height * covering);

            // multiplier par le nombre de capture
            int nbrCapture = areaDimension.width * areaDimension.height;
            float valueMP = (captureDimPx.width * captureDimPx.height * nbrCapture) / 1000000f;

            // mettre a jour le panneau
            memoryIndicator.setIndicationFor(valueMP);
            memoryIndicator.reconstruct();
            */
        }

    }

}
