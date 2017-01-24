package org.abcmap.ielements.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.resources.DistantResource;
import org.abcmap.core.resources.DistantResourceProgressEvent;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class AddDataFromCatalog extends InteractionElement {

    private JPanel listPane;
    private ArrayList<DistantResource> index;
    private JTextField searchTextField;
    private JLabel updateLabel;

    public AddDataFromCatalog() {

        this.label = "Ajouter des données à partir d'un catalogue";
        this.help = "...";

        this.displaySimplyInSearch = false;
    }


    @Override
    protected Component createPrimaryGUI() {

        JPanel contentPane = new JPanel(new MigLayout("insets 0, gap 0"));
        GuiUtils.addLabel("Ressources: ", contentPane, "wrap");

        // add search text field
        // TODO: add text field as filter
        //searchTextField = new JTextField();
        //contentPane.add(searchTextField, "width 98%!, wrap");
        //searchTextField.addCaretListener((event) -> {
        //    filterResourceList(searchTextField.getText());
        //});

        listPane = new JPanel(new MigLayout("insets 0"));
        JScrollPane scroll = new JScrollPane(listPane);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scroll, "height 200px!, width 98%!, wrap");

        // button refresh list
        JButton buttonRefresh = new JButton("Rafraichir");
        contentPane.add(buttonRefresh, "wrap");
        buttonRefresh.addActionListener(new ButtonActionListener(ButtonActionListener.REFRESH));

        // button import resources
        JButton buttonValid = new JButton("Importer");
        contentPane.add(buttonValid, "wrap 10px");
        buttonValid.addActionListener(new ButtonActionListener(ButtonActionListener.IMPORT));

        // update label information
        this.updateLabel = new JLabel();
        contentPane.add(updateLabel, "width 98%");

        // first refresh
        ThreadManager.runLater(() -> {
            refreshResourceListLater();
        });

        return contentPane;

    }

    private class ButtonActionListener implements ActionListener, Runnable {

        public static final String IMPORT = "IMPORT";
        public static final String REFRESH = "REFRESH";
        private final String mode;

        ButtonActionListener(String mode) {
            this.mode = mode;
        }

        @Override
        public void run() {
            if (getOperationLock() == false) {
                return;
            }

            try {

                // refresh list
                if (REFRESH.equals(mode)) {

                    setUpdateLabelText("Mise à jour en cours...");
                    refreshResourceListLater();
                }

                // import resources
                else if (IMPORT.equals(mode)) {

                    //TODO
                    //TODO Display information to user when total resources size is over 100 mo
                    //TODO

                    ArrayList<DistantResource> resources = getSelectedResources();

                    if (resources.size() < 1) {
                        dialm().showErrorInBox("Vous devez sélectionner une ressource");
                        return;
                    }

                    setAllElementsSelected(false);

                    mapm().importResources(resources, (event) -> {

                        int resId = resources.indexOf(event.getResource()) + 1;

                        // resource is preparing
                        if(DistantResourceProgressEvent.PREPARING.equals(event.getStatus())){
                            setUpdateLabelText("Res. " + resId + " en préparation...");
                            return;
                        }

                        // resource is being uncompressed
                        if(DistantResourceProgressEvent.UNCOMPRESSING.equals(event.getStatus())){
                            setUpdateLabelText("Res. " + resId + " en décompression...");
                            return;
                        }

                        // resource is downloading
                        else if(DistantResourceProgressEvent.DOWNLOADING.equals(event.getStatus())) {

                            double percent = -1;
                            try {
                                percent = Math.round(event.getDownloadedSize() / event.getFinalSize() * 100);
                            } catch (Exception e) {
                                logger.error(e);
                            }

                            if(percent < 0){
                                percent = 0;
                            }
                            else if(percent > 99){
                                percent = 99;
                            }

                            setUpdateLabelText("Res. " + resId + " en téléchargement: " + percent + " %");
                        }
                    });

                    // end of import
                    dialm().showMessageInBox("Fin de l'import");
                    setUpdateLabelText("");
                }

            } finally {
                setUpdateLabelText("");
                releaseOperationLock();
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadManager.runLater(this);
        }
    }

    private void setUpdateLabelText(String text) {
        SwingUtilities.invokeLater(() -> {
            updateLabel.setText(text);
        });
    }

    /**
     * Filter resources depending on specified String
     *
     * @param text
     */
    private void filterResourceList(String text) {
        //TODO
    }

    /**
     * Get the index and refresh the resource list
     */
    private void refreshResourceListLater() {

        GuiUtils.throwIfOnEDT();

        try {

            // remove all first
            SwingUtilities.invokeLater(() -> {
                listPane.removeAll();
                listPane.revalidate();
                listPane.repaint();
            });

            // get index from server
            index = mapm().getMainResourceIndex();

            // add elements to panel, on EDT
            SwingUtilities.invokeLater(() -> {
                for (DistantResource res : new ArrayList<>(index)) {
                    listPane.add(new DistantResourceGUI(res), "width 97%!, wrap");
                }

                listPane.revalidate();
                listPane.repaint();
            });

        } catch (IOException e) {
            dialm().showErrorInBox("Impossible de charger l'index");
            logger.error(e);
        }
    }

    /**
     * Set all element selected
     */
    private void setAllElementsSelected(boolean val) {

        for (Component p : listPane.getComponents()) {
            if (p instanceof DistantResourceGUI) {
                DistantResourceGUI resGui = (DistantResourceGUI) p;

                resGui.setSelected(val);
                resGui.revalidate();
                resGui.repaint();
            }
        }
    }

    /**
     * Return a list of selected resources
     *
     * @return
     */
    private ArrayList<DistantResource> getSelectedResources() {

        ArrayList<DistantResource> result = new ArrayList<>();
        for (Component p : listPane.getComponents()) {
            if (p instanceof DistantResourceGUI) {
                DistantResourceGUI resGui = (DistantResourceGUI) p;

                if (resGui.isSelected()) {
                    result.add(resGui.getResource());
                }
            }
        }

        return result;
    }


}
