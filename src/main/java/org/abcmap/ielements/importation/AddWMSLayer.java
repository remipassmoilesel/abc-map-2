package org.abcmap.ielements.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.wms.PredefinedWmsServer;
import org.abcmap.gui.components.PredefinedWmsServerRenderer;
import org.abcmap.gui.dialogs.WMSSelectionDialog;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Allow user to add a new WMS layer, by enter URL or by selecting a predefined one.
 */
public class AddWMSLayer extends InteractionElement {

    private ArrayList<PredefinedWmsServer> predefinedServers;
    /**
     * Text field where user can enter a custom WMS url
     */
    private JTextField wmsTextField;

    /**
     * Combo box where user can select a predefined WMS server
     */
    private JComboBox<PredefinedWmsServer> selectWmsServer;
    private DefaultComboBoxModel<PredefinedWmsServer> wmsServerSelectModel;

    /**
     * Label which indicate to user that he have to select a server in combo list
     */
    private final PredefinedWmsServer defaultFakeWmsServer;

    public AddWMSLayer() {

        this.label = "Ajouter une couche distante";
        this.help = "Importez une couche de données distante (WMS: Web Map Service). Cette couche peut représenter le monde, un pays, etc ... " +
                "Plusieurs couches sont accessibles librement sur Internet.";

        this.displaySimplyInSearch = false;

        // create combo box model
        wmsServerSelectModel = new DefaultComboBoxModel<>();
        predefinedServers = new ArrayList<>();

        // create fake element showing to user "Please select ..."
        defaultFakeWmsServer = new PredefinedWmsServer("Sélectionnez un serveur", "http://no-where.com");

        // listen changes on list of WMS servers
        notifm.setDefaultListener((ev) -> {
            SwingUtilities.invokeLater(() -> {
                updateListOfWmsServers();
            });
        });
        mapm().getNotificationManager().addObserver(this);

        // first update
        updateListOfWmsServers();
    }

    /**
     * Update combo box model and WMS servers list
     */
    private void updateListOfWmsServers() {

        GuiUtils.throwIfNotOnEDT();

        ArrayList<PredefinedWmsServer> newList = mapm().getListOfPredefinedWmsServers();

        // clear previous combo model and add fake element
        wmsServerSelectModel.removeAllElements();
        wmsServerSelectModel.addElement(defaultFakeWmsServer);

        // load predefined servers
        predefinedServers.clear();

        predefinedServers.addAll(newList);
        for (PredefinedWmsServer server : predefinedServers) {
            wmsServerSelectModel.addElement(server);
        }

    }

    @Override
    protected Component createPrimaryGUI() {

        JPanel panel = new JPanel(new MigLayout("insets 0, gap 5px"));

        // predefined WMS selection
        selectWmsServer = new JComboBox<>();
        selectWmsServer.setRenderer(new PredefinedWmsServerRenderer());
        selectWmsServer.setModel(wmsServerSelectModel);
        selectWmsServer.setSelectedItem(defaultFakeWmsServer);

        // when a server is selected, display URL in text field
        selectWmsServer.addActionListener((ev) -> {
            PredefinedWmsServer server = (PredefinedWmsServer) selectWmsServer.getSelectedItem();
            if (server != null) {
                if (defaultFakeWmsServer.equals(server)) {
                    GuiUtils.changeTextWithoutFire(wmsTextField, "");
                } else {
                    GuiUtils.changeTextWithoutFire(wmsTextField, server.getUrl());
                }
            }
        });

        GuiUtils.addLabel("Serveur prédéfini: ", panel, "wrap");
        panel.add(selectWmsServer, "width 95%, " + wrap15());

        // manual url input
        wmsTextField = new JTextField();
        wmsTextField.addCaretListener((ev) -> {

            if (mapm().getPredefinedWMSServer(null, wmsTextField.getText()) == null) {
                // user use text field, reset combo box
                GuiUtils.changeWithoutFire(selectWmsServer, defaultFakeWmsServer);
            }

        });
        GuiUtils.addLabel("Adresse du serveur: ", panel, "wrap");
        panel.add(wmsTextField, "width 95%, wrap");

        // valid button
        JButton validBtn = new JButton("Ajouter la couche");
        panel.add(validBtn, "wrap");
        validBtn.addActionListener((event) -> {

            // get combo value
            PredefinedWmsServer server = (PredefinedWmsServer) selectWmsServer.getSelectedItem();

            // if the default value is selected, use text field
            if (defaultFakeWmsServer.equals(server)) {
                server = new PredefinedWmsServer("Custom server", wmsTextField.getText());
            }

            openLayer(server.getUrl(), null);
        });

        // search servers online button
        JButton searchBtn = new JButton("Chercher d'autres couches");
        panel.add(searchBtn, "wrap");
        searchBtn.addActionListener((event) -> {
            ThreadManager.runLater(() -> {
                try {
                    int updates = mapm().updateListOfPredefinedWmsServers();
                    if (updates > 0) {
                        dialm().showMessageInBox("Mise à jour effectuée");
                    } else {
                        dialm().showMessageInBox("Rien à mettre à jour");
                    }
                } catch (IOException e) {
                    logger.error(e);
                    dialm().showMessageInBox("Impossible d'accéder au site de mise à jour");
                }
            });
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
            SwingUtilities.invokeLater(() -> {

                if (wmsTextField != null) {
                    GuiUtils.changeTextWithoutFire(wmsTextField, "");
                }

            });

            dialm().showMessageInBox("La couche a été ajoutée au projet: " + url);
        });

    }
}
