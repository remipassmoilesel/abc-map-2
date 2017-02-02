package org.abcmap.ielements.importation.geo;

import net.miginfocom.swing.MigLayout;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmShapefileLayer;
import org.abcmap.core.threads.ThreadManager;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.components.buttons.HtmlButton;
import org.abcmap.gui.dialogs.simple.SimpleFileFilter;
import org.abcmap.gui.utils.BrowseActionListener;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by remipassmoilesel on 13/01/17.
 */
public class AddShapefileLayer extends InteractionElement {

    /**
     * Text field where user specify path of files
     */
    private JTextField shapefileTextField;

    /**
     * List of component of shapefiles
     */
    private ArrayList<String> shapefilePossibleExtensions;

    public AddShapefileLayer() {

        this.label = "Ajouter un fichier de formes";
        this.help = "Ajoutez ici un fichier de forme (.shp) au projet courant. Vous pouvez spécifier le chemin d'un fichier SHP ou d'un dossier. " +
                "Attention: le fichier de forme ne sera pas importé, vous devrez faire en sorte qu'il reste toujours accessible au même chemin.";

        // all in lower case
        this.shapefilePossibleExtensions = new ArrayList<>(Arrays.asList(
                "shp",
                "shx",
                "dbf",
                "shx",
                "sbn",
                "prj",
                "atx",
                "qix"
        ));

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        // main panel
        JPanel panel = new JPanel(new MigLayout("insets 0, gap 5px"));
        GuiUtils.addLabel("Chemin du fichier ou du dossier: ", panel, "wrap");

        // text field
        shapefileTextField = new JTextField();
        panel.add(shapefileTextField, "width 95%, wrap");

        // button browse
        JButton buttonBrowse = new HtmlButton("Parcourir");
        panel.add(buttonBrowse, "wrap");

        SimpleFileFilter shapefileFilter = new SimpleFileFilter(shapefilePossibleExtensions, "*.shp .shx ...");
        BrowseActionListener browseListener = new BrowseActionListener(BrowseActionListener.Type.FILES_ONLY, shapefileTextField, shapefileFilter, null);
        buttonBrowse.addActionListener(browseListener);

        // button valid
        JButton buttonValid = new HtmlButton("Ajouter la couche");
        panel.add(buttonValid, "wrap");

        buttonValid.addActionListener((event) -> {
            ThreadManager.runLater(() -> {
                openLayer(shapefileTextField.getText());
            });
        });

        return panel;

    }

    /**
     * Open a new Shapefile layer with specified arguments
     *
     * @param providedPathStr
     */
    public void openLayer(String providedPathStr) {

        // check current project
        Project project = getCurrentProjectOrShowMessage();
        if (project == null) {
            return;
        }

        // Check type of path and retrieve shape file path
        Path providedPath = Paths.get(providedPathStr);
        Path shapefilePath = null;

        String extension = Utils.getExtension(providedPath).toLowerCase();

        // path is a shape file
        if (extension.equals("shp")) {
            shapefilePath = Paths.get(providedPathStr);
        }

        // path is a directory, get the first shape file available
        else if (Files.isDirectory(providedPath)) {
            try {
                for (Path path : Files.newDirectoryStream(providedPath)) {
                    if (Utils.checkExtension(path, "shp") == true) {
                        shapefilePath = path.toAbsolutePath();
                        break;
                    }
                }
            } catch (IOException e) {
                logger.error(e);
            }

        }

        // path is a one of shape file component, try to get shape file path
        else if (shapefilePossibleExtensions.contains(extension)) {
            String shapefileStr = providedPathStr.substring(0, providedPathStr.length() - extension.length()) + "shp";
            shapefilePath = Paths.get(shapefileStr);
        }

        // error while opening directory or nothing found
        if (shapefilePath == null) {
            dialm().showErrorInBox("Impossible d'ouvrir: " + providedPath.getFileName());
            return;
        }

        // open shape file
        AbmShapefileLayer layer = null;
        try {
            layer = project.addNewShapeFileLayer(shapefilePath);
        } catch (IOException e) {
            logger.error(e);
            dialm().showErrorInBox("Impossible d'ouvrir: " + shapefilePath.getFileName());
            return;
        }

        // delete cache and show changes
        mapm().mainmap.refresh();

        projectm().fireLayerListChanged();

        // empty text field
        Path finalShapefilePath = shapefilePath;
        SwingUtilities.invokeLater(() -> {

            if (shapefileTextField != null) {
                GuiUtils.changeTextWithoutFire(shapefileTextField, "");
            }

            dialm().showMessageInBox("La couche a été ajoutée: " + finalShapefilePath.getFileName());
        });

    }
}
