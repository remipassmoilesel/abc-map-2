package org.abcmap.ielements.layers;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.project.layers.AbmShapeFileLayer;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.core.wms.WmsLayerEntry;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class LayerInformationsPanel extends InteractionElement {

    private JPanel informationPanel;

    public LayerInformationsPanel() {

        this.label = "Informations sur la couche";
        this.help = "Affiche des informations sur la couche active (en bleu dans le sélecteur de calque). " +
                "Permet de faire quelques réglages basiques comme la couleur des fichiers Shapefile.";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        informationPanel = new JPanel(new MigLayout("insets 0, gap 3"));

        InformationUpdater formUpdater = new InformationUpdater();
        formUpdater.addEventNameFilter(ProjectEvent.NEW_PROJECT_LOADED);
        formUpdater.addEventNameFilter(ProjectEvent.PROJECT_CLOSED);
        formUpdater.addEventNameFilter(ProjectEvent.LAYERS_LIST_CHANGED);

        notifm.addEventListener(formUpdater);
        projectm().getNotificationManager().addObserver(this);

        formUpdater.updateAllLater();

        return informationPanel;

    }

    class InformationUpdater extends FormUpdater {

        @Override
        protected void updateFormFields() {
            super.updateFormFields();

            informationPanel.removeAll();

            if (projectm().isInitialized() == true) {

                ArrayList<Object[]> fields = new ArrayList<>();

                AbmAbstractLayer activeLayer = projectm().getProject().getActiveLayer();

                System.out.println(activeLayer);

                fields.add(new Object[]{"Type:", activeLayer.getReadableType()});

                // add repaint button
                JButton redraw = new JButton("Redessiner cette couche");
                redraw.addActionListener((event) -> {
                    Project project = projectm().getProject();
                    project.deleteCacheForLayer(project.getActiveLayer().getId(), null);
                });
                informationPanel.add(redraw, "span, width 80%, align center, wrap 10px");

                // layer is a shapefile layer
                if (activeLayer instanceof AbmShapeFileLayer) {

                    fields.add(new Object[]{"Taille:", "256mo"});
                    fields.add(new Object[]{"Formes:", "polygones"});

                    JButton buttonForeground = new JButton("Couleur");
                    fields.add(new Object[]{"Trait:", buttonForeground});
                    buttonForeground.addActionListener((ev) -> {

                    });

                    JButton buttonBackground = new JButton("Couleur");
                    fields.add(new Object[]{"Remplissage:", buttonBackground});
                    buttonBackground.addActionListener((ev) -> {

                    });

                }

                // layer is a shapefile layer
                if (activeLayer instanceof AbmWMSLayer) {

                    WmsLayerEntry entry = ((AbmWMSLayer) activeLayer).getWmsEntry();

                    fields.add(new Object[]{"Adresse:", entry.getUrl()});
                    fields.add(new Object[]{"Nom distant:", entry.getWmsLayerName()});
                }

                for (Object[] f : fields) {

                    // add name
                    informationPanel.add(getTextFieldForElement((String) f[0]), "width 30%!");

                    String wrap = "width 60%!, wrap 10";

                    // add value
                    // value is a string, display a text field
                    if (f[1] instanceof String) {
                        JTextField field = getTextFieldForElement((String) f[1]);
                        informationPanel.add(field, wrap);
                    }

                    // value is already a component, add it as is
                    else if (f[1] instanceof Component) {
                        informationPanel.add((Component) f[1], wrap);
                    }

                    // unknown type of value
                    else {
                        throw new IllegalArgumentException("Unknown type: " + f[1]);
                    }

                }

            } else {
                informationPanel.add(new JLabel("Aucun projet chargé"));
            }


            informationPanel.revalidate();
            informationPanel.repaint();

        }

        private JTextField getTextFieldForElement(String s) {
            JTextField textField = new JTextField(s);
            textField.setEditable(false);
            textField.setCursor(GuiCursor.NORMAL_CURSOR);
            textField.setBorder(BorderFactory.createLineBorder(Color.lightGray));
            textField.setCaretPosition(0);
            return textField;
        }
    }
}
