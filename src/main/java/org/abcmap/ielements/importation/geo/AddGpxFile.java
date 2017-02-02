package org.abcmap.ielements.importation.geo;

import com.vividsolutions.jts.geom.Coordinate;
import net.miginfocom.swing.MigLayout;
import org.abcmap.core.draw.builder.LineBuilder;
import org.abcmap.core.gpx.GpxParser;
import org.abcmap.core.gpx.GpxPoint;
import org.abcmap.core.gpx.GpxPointsList;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmFeatureLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.dialogs.simple.SimpleFileFilter;
import org.abcmap.gui.utils.BrowseActionListener;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;
import org.opengis.feature.simple.SimpleFeature;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class AddGpxFile extends InteractionElement {

    /**
     * Text field where user specify path of files
     */
    private JTextField gpxTextField;

    /**
     * List of component of shapefiles
     */
    private ArrayList<String> gpxPossibleExtensions;

    /**
     * JList used to select tracks to add to map
     */
    private JList trackSelectionList;

    /**
     * List of track user can choose
     */
    private DefaultListModel trackSelectionModel;

    /**
     * Element added in list while GPX file is parsing
     */
    private String pleaseWaitListElement = "Veuillez patienter...";

    /**
     * Element added in list when no files are selected
     */
    private String pleaseSelectFileElement = "Choisissez un fichier...";

    public AddGpxFile() {

        this.label = "Ajouter une trace GPS";
        this.help = "...";

        // all in lower case
        this.gpxPossibleExtensions = new ArrayList<>(Arrays.asList(
                "gpx"
        ));

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        // main panel
        JPanel panel = new JPanel(new MigLayout("insets 0, gap 5px"));
        GuiUtils.addLabel("Chemin du fichier : ", panel, "wrap");

        // text field
        gpxTextField = new JTextField();
        panel.add(gpxTextField, "width 95%, wrap");

        // track selection
        trackSelectionModel = new DefaultListModel<>();
        trackSelectionList = new JList(trackSelectionModel);
        trackSelectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JScrollPane scrollpane = new JScrollPane(trackSelectionList);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollpane, "width 98%!, height 100px!, wrap");

        // button browse
        JButton buttonBrowse = new HtmlButton("Parcourir");
        panel.add(buttonBrowse, "wrap");

        SimpleFileFilter gpxFileFilter = new SimpleFileFilter(gpxPossibleExtensions, "*.gpx");
        BrowseActionListener browseListener = new BrowseActionListener(BrowseActionListener.Type.FILES_ONLY, gpxTextField, gpxFileFilter, () -> {

            // remove previous tracks
            trackSelectionModel.clear();
            trackSelectionModel.addElement(pleaseWaitListElement);
            trackSelectionList.repaint();

            ThreadManager.runLater(() -> {
                updateTrackSelectionList();
            });

        });
        buttonBrowse.addActionListener(browseListener);

        // button valid
        JButton buttonValid = new HtmlButton("Ajouter les traces");
        panel.add(buttonValid, "wrap");

        buttonValid.addActionListener((event) -> {
            int[] selectedIndexes = trackSelectionList.getSelectedIndices();
            ThreadManager.runLater(() -> {
                addGpxTracks(gpxTextField.getText(), selectedIndexes);
            });
        });

        return panel;

    }

    /**
     * Update track selection from current file
     */
    private void updateTrackSelectionList() {

        GuiUtils.throwIfOnEDT();

        // remove previous tracks
        trackSelectionModel.clear();

        // try to parse current file
        GpxParser parser = new GpxParser();
        try {
            parser.setGpxSource(Paths.get(gpxTextField.getText()));
            parser.parse();

            SwingUtilities.invokeLater(() -> {
                for (GpxPointsList list : parser.getPointsLists()) {
                    String entry = list.getType() + ": " + list.getPoints().size() + " points";
                    trackSelectionModel.addElement(entry);
                }

                trackSelectionList.repaint();
            });

        } catch (Exception e) {
            logger.error(e);
            trackSelectionModel.addElement(pleaseSelectFileElement);
            trackSelectionList.repaint();
        }
    }

    /**
     * Open a feature layer with specified gpx data
     *
     * @param providedPathStr
     */
    public void addGpxTracks(String providedPathStr, int[] trackIndexes) {

        // check current project
        Project project = getCurrentProjectOrShowMessage();
        if (project == null) {
            return;
        }

        // Check type of path and retrieve shape file path
        Path gpxPath = null;

        // check extension
        String extension = Utils.getExtension(providedPathStr).toLowerCase();
        if (extension.equals("gpx") == false) {
            gpxPath = Paths.get(providedPathStr + ".gpx");
        } else {
            gpxPath = Paths.get(providedPathStr);
        }

        // try to parse file
        GpxParser parser = new GpxParser();
        try {
            parser.setGpxSource(gpxPath);
            parser.parse();
        } catch (Exception e) {
            logger.error(e);
            dialm().showErrorInBox("Impossible d'ouvrir le fichier: " + gpxPath.getFileName());
            return;
        }

        // select tracks to add
        ArrayList<GpxPointsList> tracksToAdd = new ArrayList<>();
        ArrayList<GpxPointsList> availableTracks = parser.getPointsLists();

        // add all tracks
        if (trackIndexes == null) {
            tracksToAdd.addAll(availableTracks);
        }

        // add only specified tracks
        else {
            for (int i : trackIndexes) {
                try {
                    tracksToAdd.add(availableTracks.get(i));
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid index: " + i);
                }
            }
        }

        // create a new feature layer
        AbmFeatureLayer featureLayer;
        try {
            featureLayer = (AbmFeatureLayer) project.addNewFeatureLayer("GPX tracks ", true, project.getHigherZindex());
            project.setActiveLayer(featureLayer);
        } catch (IOException e) {
            logger.error(e);
            dialm().showErrorInBox("Impossible d'ouvrir le fichier: " + gpxPath.getFileName());
            return;
        }

        // create features
        ArrayList<SimpleFeature> features = new ArrayList<SimpleFeature>();
        LineBuilder builder = drawm().getLineBuilder();
        for (GpxPointsList track : availableTracks) {

            ArrayList<GpxPoint> points = track.getPoints();

            builder.newLine(points.get(0).getCoordinatePoint());
            for (int i = 0; i < points.size() - 2; i++) {
                Coordinate point = points.get(i).getCoordinatePoint();
                builder.addPoint(point);
            }

            SimpleFeature feat = builder.terminateLine(points.get(points.size() - 1).getCoordinatePoint());
            features.add(feat);
        }

        // add features to the new layer
        featureLayer.addFeatures(features);

        // delete cache and show changes
        mapm().mainmap.refresh();

        // notify that a new layer have been created
        projectm().fireLayerListChanged();

        // display a message
        dialm().showMessageInBox("Les données ont été ajoutées: " + gpxPath.getFileName());

    }
}
