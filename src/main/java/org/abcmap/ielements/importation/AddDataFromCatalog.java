package org.abcmap.ielements.importation;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.resources.DistantResource;
import org.abcmap.core.resources.ShapefileResource;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.HtmlLabel;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class AddDataFromCatalog extends InteractionElement {

    private JPanel listPane;
    private ArrayList<DistantResource> index;
    private JTextField searchTextField;

    public AddDataFromCatalog() {

        this.label = "Ajouter des données à partir d'un catalogue";
        this.help = "...";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {


        JPanel contentPane = new JPanel(new MigLayout("insets 0, gap 5px"));
        GuiUtils.addLabel("Ressources: ", contentPane, "wrap");

        // add search text field
        searchTextField = new JTextField();
        contentPane.add(searchTextField, "width 90%!, wrap");
        searchTextField.addCaretListener((event) -> {
            filterResourceList(searchTextField.getText());
        });

        listPane = new JPanel(new MigLayout("insets 3"));
        JScrollPane scroll = new JScrollPane(listPane);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(ConfigurationConstants.SCROLLBAR_UNIT_INCREMENT);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPane.add(scroll, "height 200px, width 98%!, wrap");

        // button refresh list
        JButton buttonRefresh = new JButton("Rafraichir");
        contentPane.add(buttonRefresh, "wrap");

        buttonRefresh.addActionListener((event) -> {
            ThreadManager.runLater(()->{
                refreshResourceListLater();
            });
        });

        // button import resources
        JButton buttonValid = new JButton("Importer");
        contentPane.add(buttonValid, "wrap");
        buttonValid.addActionListener((event) -> {
            ThreadManager.runLater(() -> {
                ArrayList<DistantResource> resources = getSelectedResources();

                if (resources.size() < 1) {
                    dialm().showErrorInBox("Vous devez sélectionner une ressource");
                    return;
                }

                setAllElementsSelected(false);

                mapm().importResources(resources, (objects)->{
                    System.out.println();
                    System.out.println(objects);
                    System.out.println(objects[0]);
                    System.out.println(objects[1]);
                    System.out.println(objects[2]);
                });
                dialm().showMessageInBox("Fin de l'import");

            });

        });

        ThreadManager.runLater(()->{
            refreshResourceListLater();
        });

        return contentPane;

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
                    listPane.add(new DistantResourceGUI(res), "width 98%!, wrap");
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

    /**
     * Show a resource in main list
     */
    private class DistantResourceGUI extends JPanel {

        private final DistantResource resource;
        private final HtmlCheckbox chkSelected;

        public DistantResourceGUI(DistantResource res) {
            super(new MigLayout("insets 3, gap 3"));

            setCursor(GuiCursor.HAND_CURSOR);
            this.resource = res;

            chkSelected = new HtmlCheckbox("<b>" + res.getName() + "</b>");

            add(chkSelected, "width 98%!, wrap");
            add(new HtmlLabel(res.getDescription()), "gapleft 10px!, width 98%!, wrap");

            if (res instanceof ShapefileResource) {
                add(new HtmlLabel("Taille: " + ((ShapefileResource) res).getSize() + "mo"), "gapleft 10px!, width 98%!");
            }

        }

        /**
         * Get resource associated with this element
         *
         * @return
         */
        public DistantResource getResource() {
            return resource;
        }

        /**
         * Set this element selected
         *
         * @param selected
         */
        public void setSelected(boolean selected) {
            chkSelected.setSelected(selected);
            revalidate();
            repaint();
        }

        /**
         * Return true if this element is selected
         *
         * @return
         */
        public boolean isSelected() {
            return chkSelected.isSelected();
        }

    }
}
