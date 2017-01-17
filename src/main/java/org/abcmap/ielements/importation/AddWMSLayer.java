package org.abcmap.ielements.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.dialogs.WMSSelectionDialog;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class AddWMSLayer extends InteractionElement {

    private JTextField wmsTextField;

    public AddWMSLayer() {

        this.label = "Ajouter une couche distante";
        this.help = "Importez une couche de données distante (WMS: Web Map Service). Cette couche peut représenter le monde, un pays, etc ... " +
                "Plusieurs couches sont accessibles librement sur Internet.";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        wmsTextField = new JTextField();

        JPanel panel = new JPanel(new MigLayout("insets 0, gap 5px"));
        GuiUtils.addLabel("Adresse du serveur: ", panel, "wrap");
        panel.add(wmsTextField, "width 95%, wrap");

        JButton buttonValid = new JButton("Ajouter");
        panel.add(buttonValid, "wrap");

        buttonValid.addActionListener((event) -> {
            openLayer(wmsTextField.getText(), null);
        });

        return panel;

    }

    /**
     * Open a new WMS layer with specified arguments, in another thread
     *
     * @param url
     * @param layerName
     */
    public void openLayer(String url, String layerName) {

        ThreadManager.runLater(() -> {

            // try to open wms layer
            AbmWMSLayer layer;
            try {
                layer = projectm().getProject().addNewWMSLayer(url, layerName);
            } catch (IOException e) {
                logger.error(e);
                dialm().showErrorInBox("Impossible d'ouvrir cette ressource");
                return;
            }

            // let user select layer name
            final String[] choosenLayerName = new String[]{null};
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        WMSSelectionDialog dialog = new WMSSelectionDialog(layer.getAvailableWMSNames());
                        dialog.setSelectedValue(layerName);
                        dialog.setVisible(true);
                        choosenLayerName[0] = dialog.getSelectedValue();
                    }
                });
            } catch (InterruptedException | InvocationTargetException e) {
                logger.error(e);
            }

            // change readable name if needed
            if (layer.getName() == null || layer.getName().isEmpty()) {
                layer.setName(layerName);
            }

            // change internal layer
            layer.changeWmsName(choosenLayerName[0]);

            // delete cache and show changes
            mapm().mainmap.deleteCache(layer.getId(), null);
            mapm().mainmap.refresh();

            projectm().fireLayerListChanged();

            // empty text field
            SwingUtilities.invokeLater(()->{

                if (wmsTextField != null) {
                    GuiUtils.changeTextWithoutFire(wmsTextField, "");
                }

            });

            dialm().showMessageInBox("La couche a été ajoutée au projet: " + url);
        });

    }
}