package org.abcmap.ielements.layers;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.events.ProjectEvent;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmAbstractLayer;
import org.abcmap.core.project.layers.AbmShapefileLayer;
import org.abcmap.core.project.layers.AbmWMSLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.wms.WmsLayerEntry;
import org.abcmap.gui.GuiCursor;
import org.abcmap.gui.utils.FormUpdater;
import org.abcmap.ielements.InteractionElement;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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

            String wrap = "width 60%!, wrap 10px";
            String buttonCentered = "span, width 80%, align center, wrap 10px";

            if (projectm().isInitialized() == true) {

                ArrayList<Object[]> fields = new ArrayList<>();

                AbmAbstractLayer activeLayer = projectm().getProject().getActiveLayer();

                fields.add(new Object[]{"Type:", activeLayer.getReadableType()});

                CoordinateReferenceSystem crs = activeLayer.getCrs();
                final JTextField crsLabel = getTextFieldForElement("Waiting...");
                fields.add(new Object[]{"CRS:", crsLabel});

                // get crs name can be very slow, so display value later (and not on EDT)
                ThreadManager.runLater(() -> {
                    crsLabel.setText(GeoUtils.crsToString(crs, true));
                    crsLabel.repaint();
                    crsLabel.setCaretPosition(0);
                });

                // add repaint button
                JButton redraw = new JButton("Redessiner cette couche");
                redraw.addActionListener((event) -> {
                    Project project = projectm().getProject();
                    project.deleteCacheForLayer(project.getActiveLayer().getId(), null);
                });
                informationPanel.add(redraw, buttonCentered);

                //
                // layer is a shape file layer
                //
                if (activeLayer instanceof AbmShapefileLayer) {

                    fields.add(new Object[]{"Taille:", "256mo"});
                    fields.add(new Object[]{"Formes:", "polygones"});

                    JButton buttonReproject = new JButton("Ouvrir une version reprojetée");
                    fields.add(new Object[]{buttonReproject});
                    buttonReproject.addActionListener((ev) -> {
                        ThreadManager.runLater(() -> {
                            mapm().openReprojectedShapefile((AbmShapefileLayer) activeLayer, true);
                        });
                    });

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

                    if (f[0] instanceof String) {
                        // add name
                        informationPanel.add(getTextFieldForElement((String) f[0]), "width 30%!");

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
                            informationPanel.add(new JLabel(), wrap);
                            logger.error(new IllegalArgumentException("Unknown type for '" + f[0] + "': " + f[1]));
                        }
                    }


                    // insert a component on both column
                    else if (f[0] instanceof Component) {
                        informationPanel.add((Component) f[0], buttonCentered);
                    }

                    // type not found
                    else {
                        logger.error(new IllegalArgumentException("Unknown type for '" + f[0] + "': " + f[1]));
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
