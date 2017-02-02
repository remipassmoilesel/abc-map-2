package org.abcmap.core.managers;

import com.labun.surf.Params;
import org.abcmap.core.cancel.UndoableOperationWrapper;
import org.abcmap.core.crop.CropConfigurator;
import org.abcmap.core.events.ImportManagerEvent;
import org.abcmap.core.events.manager.EventNotificationManager;
import org.abcmap.core.events.manager.HasEventNotificationManager;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.resources.DistantResource;
import org.abcmap.core.resources.DistantResourceProgressEvent;
import org.abcmap.core.resources.MapImportException;
import org.abcmap.core.robot.ScreenCatcher;
import org.abcmap.core.tileanalyse.TileFactory;
import org.abcmap.core.utils.Utils;
import org.abcmap.gui.utils.GuiUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by remipassmoilesel on 08/12/16.
 */
public class ImportManager extends ManagerTreeAccessUtil implements HasEventNotificationManager {

    private static final CustomLogger logger = LogManager.getLogger(UndoableOperationWrapper.class);

    /**
     * Factory used to create tiles when user catch screen manually
     */
    private TileFactory manualImporter;

    /**
     * Current data headers
     */
    private ArrayList<String> currentDataImportHeaders;

    /**
     * Current crop configurator
     */
    private CropConfigurator cropConfigurator;

    /**
     * List of valid extensions for import
     */
    private static ArrayList<String> validExtensionsForTiles;

    private ClipboardManager clipboardm;
    private EventNotificationManager notifm;


    public ImportManager() {

        this.notifm = new EventNotificationManager(this);
        this.cropConfigurator = null;

        validExtensionsForTiles = new ArrayList<>();
        validExtensionsForTiles.addAll(Arrays.asList(Utils.getAllImageSupportedFormats()));

        currentDataImportHeaders = new ArrayList<>();

        manualImporter = new TileFactory();

    }

    /**
     * Fire that import parameters have changed
     */
    public void fireParametersChanged() {
        fireEvent(ImportManagerEvent.PARAMETERS_CHANGED);
    }

    /**
     * Fire an event from the import manager
     *
     * @param name
     */
    private void fireEvent(String name) {
        notifm.fireEvent(new ImportManagerEvent(name, null));
    }

    /**
     * Hide all components specified and catch screen
     *
     * @param componentsToHide
     * @return
     * @throws MapImportException
     */
    public BufferedImage catchScreen(ArrayList<Component> componentsToHide) throws MapImportException {
        return catchScreen(componentsToHide, true);
    }

    /**
     * Hide all components specified and catch screen
     * <p>
     * If displayAgainAfterCatch is set to true, components will be shown again after screen capture
     *
     * @param componentsToHide
     * @return
     * @throws MapImportException
     */

    public BufferedImage catchScreen(ArrayList<Component> componentsToHide, boolean displayAgainAfterCatch) throws MapImportException {

        GuiUtils.throwIfOnEDT();

        ScreenCatcher ct = new ScreenCatcher(componentsToHide);
        ct.setDisplayAgainAfterCatch(displayAgainAfterCatch);
        ct.run();

        return ct.getScreenCapture();
    }

    /**
     * Return a list of extensions that should be accepted as a valid tile
     *
     * @return
     */
    public ArrayList<String> getValidExtensionsForTiles() {
        return validExtensionsForTiles;
    }

    /**
     * Return true if specified extension should be accepted as a valid tile
     *
     * @param ext
     * @return
     */
    public boolean isValidExtensionsForTile(String ext) {
        return validExtensionsForTiles.contains(ext.toLowerCase());
    }

    /**
     * Import specified resources in project
     *
     * @param selectedResources
     * @param updates
     */
    public void importDistantResources(ArrayList<DistantResource> selectedResources, Consumer<DistantResourceProgressEvent> updates) {
        for (DistantResource res : selectedResources) {
            try {
                res.importIn(projectm().getProject(), (event) -> {
                    if (updates != null) {
                        updates.accept(event);
                    }
                });
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }


    /**
     * Return true if we are currently importing
     *
     * @return
     */
    public boolean isManualImporting() {
        return manualImporter != null && manualImporter.isWorking();
    }

    /**
     * Demarrer l'import manuel
     *
     * @return
     * @throws MapImportException
     */
    public void startManualImport() throws MapImportException {

        if (isManualImporting()) {
            throw new MapImportException(MapImportException.ALREADY_IMPORTING);
        }

        /*
        // creation d'un objet d'import
        manualImporter = new TileFactory();
        manualImporter.getListenerHandler().add(new ManualImportListener());
        manualImporter.enableCropping(confm.isCroppingEnabled());
        manualImporter.setCropRectangle(confm.getCropRectangle());

        // surveiller le presse papier
        clipboardm.watchClipBoardForImages(true, manualImporter);

        // notifications
        fireEvent(ImportManagerEvent.DIRECTORY_IMPORT_STARTED);
        */
    }

    /**
     * Arret de l'import manuel
     */
    public void stopManualImportLater() {

        if (manualImporter == null)
            return;

        manualImporter.stopImportLater();
        manualImporter = null;

        /*
        clipboardm.watchClipBoardForImages(false, null);
        */
        // la notification de fin se fait dans le listener associé à l'objet,
        // puisque l'import peut continuer après l'ordre d'arrêt
    }

    /**
     * Commencer un import par repertoire. Un seul possible la fois.
     *
     * @throws MapImportException
     * @throws IOException
     */
    public void startDirectoryImport() throws MapImportException {

        if (isDirectoryImporting()) {
            throw new MapImportException(MapImportException.ALREADY_IMPORTING);
        }

        /*
        // objet d'importation
        directoryImporter = new DirectoryImporter();
        directoryImporter.getListenerHandler().add(
                new DirectoryImporterListener());
        directoryImporter.enableCropping(confm.isCroppingEnabled());
        directoryImporter.setCropRectangle(confm.getCropRectangle());

        // import
        directoryImporter.addAllFileFrom(new File(confm
                .getDirectoryImportPath()));

        // notifications
        fireEvent(ImportManagerEvent.DIRECTORY_IMPORT_STARTED);
        */
    }

    /**
     * Arret de l'import par dossier
     */
    public void stopDirectoryImportLater() {

        /*
        if (directoryImporter == null)
            return;

        directoryImporter.stopImportLater();
        directoryImporter = null;

        // la notification de fin se fait dans le listener associé à l'objet,
        // puisque l'import peut continuer après l'ordre d'arrêt
        */
    }

    /**
     * Renvoi vrai si un import de repertoire est en cours.
     *
     * @return
     */
    public Boolean isDirectoryImporting() {
        /*
        return directoryImporter != null && directoryImporter.isWorking();
         */

        return false;
    }

    /**
     * Démarrer l'import de document.
     *
     * @throws MapImportException
     */
    public void startDocumentImport() throws MapImportException {

        /*
        if (isDocumentImporting()) {
            throw new MapImportException(MapImportException.ALREADY_IMPORTING);
        }

        // lancer l'import
        documentImporter = new DocumentImporter();
        documentImporter.getListenerHandler().add(
                new DocumentImporterListener());
        documentImporter.startLater();

        // notifications
        fireEvent(ImportManagerEvent.DOCUMENT_IMPORT_STARTED);
        */
    }

    public void stopDocumentImportLater() {

        /*
        if (documentImporter == null)
            return;

        // TODO: Réviser les moyens d'arreter l'import
        // documentImporter.stopImportLater();

        documentImporter = null;

        // la notification de fin se fait dans le listener associé à l'objet,
        // puisque l'import peut continuer après l'ordre d'arrêt
        */
    }

    public Boolean isDocumentImporting() {
        /*
        return documentImporter != null && documentImporter.isWorking();
        */
        return false;
    }

    /**
     * Import automatique
     *
     * @throws MapImportException
     */
    public void startRobotImport() throws MapImportException {

        if (isDocumentImporting()) {
            throw new MapImportException(MapImportException.ALREADY_IMPORTING);
        }
        /*
        // lancer l'import
        robotImporter = new RobotImporter();
        robotImporter.addImporterListener(new RobotImporterListener());
        robotImporter.start();

        // notifications
        fireEvent(ImportManagerEvent.ROBOT_IMPORT_STARTED);
        */
    }

    /**
     * Etat de l'import auto
     *
     * @return
     */
    public Boolean isRobotImporting() {

        /*
        return robotImporter != null && robotImporter.isWorking();
        */
        return false;
    }

    /**
     * Arreter l'import auto
     */
    public void stopRobotImportLater() {

        /*
        if (robotImporter == null)
            return;

        robotImporter.abortImportLater();
        robotImporter = null;

        // la notification de fin se fait dans le listener associé à l'objet,
        // puisque l'import peut continuer après l'ordre d'arrêt
        */
    }

    public void startDataImport() throws MapImportException {

    }

    public void stopDataImportLater() {

    }

    public boolean isDataImportLater() {
        return false;
    }

    /**
     * Configuration visuelle d'aire de recadrage
     */
    public void startCropAreaConfiguration(String mode) throws IOException,
            MapImportException {

        // verifier que la configuration ne soit pas déjà en cours
        if (cropConfigurator != null) {
            //logger.error(new IllegalStateException("Invalid starting"));
        }

        // lancer la configuration
        cropConfigurator = new CropConfigurator(mode);
        cropConfigurator.start();

        // notifications
        fireEvent(ImportManagerEvent.CROP_AREA_CONFIGURATION_START);

    }

    /**
     * Arret de la configuraton visuelle
     */
    public void stopCropConfiguration() {

        if (cropConfigurator != null) {

            // arreter la configuration
            cropConfigurator.stop();

            // detruire la reference du configurator
            cropConfigurator = null;

            // notifications
            fireEvent(ImportManagerEvent.CROP_AREA_CONFIGURATION_STOP);
        }

    }

    @Override
    public EventNotificationManager getNotificationManager() {
        return notifm;
    }

    public Params getSurfParameters() {
        return configm().getSurfConfiguration();
    }

    public ArrayList<File> getAllValidPicturesFrom(File directory)
            throws IOException {

        if (directory.isDirectory() == false) {
            throw new IOException("Not a directory: "
                    + directory.getAbsolutePath());
        }

        // verifier si les fichiers existes et si ils sont images
        ArrayList<File> files = new ArrayList<File>();
        for (String s : directory.list()) {

            File f = Paths.get(directory.getAbsolutePath(), s).toFile();
            String ext = Utils.getExtension(f.getPath()).toLowerCase();

            if (f.isFile() && isValidExtensionsForTile(ext)) {
                files.add(f);
            }

        }

        return files;

    }

    /**
     * Retourne la taille originale du document sélectionné dans la
     * configuration.
     * <p>
     * Retourne null si aucun lecteur ne peut être utilisé pour ce fichier.
     *
     * @return
     */
    public Dimension[] getDocumentImportSize() {

        /*
        // le fichier à importer
        File file = new File(confm.getDocumentImportPath());

        // rechercher un lecteur adequat
        AbstractDocumentRenderer adr = AbstractDocumentRenderer
                .getRendererFor(Utils.getExtension(file.getName()));

        if (adr == null) {
            return null;
        }

        try {
            // recuperer les dimensions du document
            return adr.getDocumentDimensions(file);

        } catch (IOException e) {

            // erreur : retour null
            Log.error(e);
            return null;

        }
        */

        return null;
    }

    /**
     * Mettre à jour les entetes d'import de données stockés dans le
     * gestionnaire d'import. Ces entetes sont utilisés par les composants
     * graphiques pour être manipulés par les utilisateurs. Stocker ici ces
     * entetes évite de demander le parsage du fichier original de trop
     * nombreuses fois.
     * <p>
     * Cette méthode est appelée par le gestionnaire de configuration en cas de
     * changement de chemin d'import de fichier de données.
     * <p>
     * Si une erreur survient pendant l'analyse du fichier, la liste est vidée
     * et aucune erreur n'est signalée.
     */
    public void updateCurrentsDataImportHeaders() {

        /*
        // reinitiliser la liste
        currentDataImportHeaders.clear();

        // verifier le fichier à analyser
        File file = new File(confm.getDataImportPath());
        if (file.isFile() == false) {
            Log.error(new Exception("Not a file: " + file.getAbsolutePath()));
            fireEvent(ImportManagerEvent.DATA_HEADERS_CHANGED);
            return;
        }

        // trouver l'analyseur adequat
        AbstractDataParser parser = AbstractDataParser.getParserFor(Utils
                .getExtension(file));
        if (parser == null) {
            Log.error(new Exception("Unknown format: " + file.getAbsolutePath()));
            fireEvent(ImportManagerEvent.DATA_HEADERS_CHANGED);
            return;
        }

        // recuperer les headers
        try {
            currentDataImportHeaders.addAll(parser.getHeaders(file));
        } catch (IOException | DataImportException e) {
            Log.error(e);
            fireEvent(ImportManagerEvent.DATA_HEADERS_CHANGED);
            return;
        }

        // notification
        fireEvent(ImportManagerEvent.DATA_HEADERS_CHANGED);
        */
    }

    /**
     * Retourne la liste des en tete du fichier courant d'import de donnée, ou
     * une liste vide si aucun entete n'a pu être analysé.
     * <p>
     * Ces entetes sont utilisés par les composants graphiques pour être
     * manipulés par les utilisateurs. Stocker ici ces en tetes évite de
     * demander le parsage du fichier original de trop nombreuses fois.
     */
    public ArrayList<String> getDataImportCurrentHeaders() {
        return new ArrayList<>(currentDataImportHeaders);
    }
}
