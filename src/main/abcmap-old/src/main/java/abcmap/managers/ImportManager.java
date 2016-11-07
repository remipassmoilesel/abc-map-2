package abcmap.managers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import abcmap.events.ImportManagerEvent;
import abcmap.exceptions.DataImportException;
import abcmap.exceptions.MapImportException;
import abcmap.importation.CropConfigurator;
import abcmap.importation.ImageMemoryIndicator;
import abcmap.importation.ScreenCatcher;
import abcmap.importation.data.reader.AbstractDataParser;
import abcmap.importation.directory.DirectoryImporter;
import abcmap.importation.directory.DirectoryImporterListener;
import abcmap.importation.documents.AbstractDocumentRenderer;
import abcmap.importation.documents.DocumentImporter;
import abcmap.importation.documents.DocumentImporterListener;
import abcmap.importation.manual.ManualImportListener;
import abcmap.importation.robot.RobotImporter;
import abcmap.importation.robot.RobotImporterListener;
import abcmap.importation.tile.TileMaker;
import abcmap.managers.stub.MainManager;
import abcmap.surf.Params;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

public class ImportManager implements HasNotificationManager {

	/** Entetes du fichier courant d'import de données. */
	private ArrayList<String> currentDataImportHeaders;

	/** Référence vers l'objet courant de configuration de recadrage */
	private CropConfigurator cropConfigurator;

	/** Référence vers l'objet courant d'import automatique */
	private RobotImporter robotImporter;

	/** Référence vers l'objet courant d'import manuel */
	private TileMaker manualImporter;

	/** Référence vers l'objet courant d'import de répertoire */
	private DirectoryImporter directoryImporter;

	/** Référence vers l'objet courant d'import de document */
	private DocumentImporter documentImporter;

	/** Liste des extensions valides pour import */
	private static ArrayList<String> validExtensionsForTiles;

	private ClipboardManager clipboardm;
	private NotificationManager notifm;
	private ConfigurationManager confm;

	public ImportManager() {

		this.clipboardm = MainManager.getClipboardManager();
		this.confm = MainManager.getConfigurationManager();
		this.notifm = new NotificationManager(this);

		// Permet de lancer la configuration visuelle d'import
		this.cropConfigurator = null;

		// extensions d'images valides pour import
		validExtensionsForTiles = new ArrayList<>();
		validExtensionsForTiles.addAll(Arrays.asList(Utils
				.getAllImageSupportedFormats()));

		currentDataImportHeaders = new ArrayList<String>();

	}

	public ImageMemoryIndicator getMemoryChargeIndicatorFor(double pixelWidth,
			double pixelHeight) {
		return ImageMemoryIndicator.getIndicatorFor(pixelWidth, pixelHeight);
	}

	public ImageMemoryIndicator getMemoryChargeIndicatorFor(double valueMp) {
		return ImageMemoryIndicator.getIndicatorFor(valueMp);
	}

	public void fireParametersChanged() {
		fireEvent(ImportManagerEvent.PARAMETERS_CHANGED);
	}

	public void fireEvent(String name) {
		notifm.fireEvent(new ImportManagerEvent(name, null));
	}

	/**
	 * Cache les éléments passés en argument puis capture l'écran.
	 * 
	 * @return
	 * @throws MapImportException
	 */
	public BufferedImage catchScreen(ArrayList<Component> componentsToHide)
			throws MapImportException {
		return catchScreen(componentsToHide, true);
	}

	/**
	 * Cache les éléments passés en argument puis capture l'écran.
	 * 
	 * @throws MapImportException
	 */
	public BufferedImage catchScreen(ArrayList<Component> componentsToHide,
			boolean displayAgainAfterCatch) throws MapImportException {

		GuiUtils.throwIfOnEDT();

		// capture
		ScreenCatcher ct = new ScreenCatcher(componentsToHide);
		ct.displayAgainAfterCatch(displayAgainAfterCatch);
		ct.run();

		// retour du résultats
		return ct.getResult();
	}

	/**
	 * Retourne les extensions acceptés pour import
	 * 
	 * @return
	 */
	public ArrayList<String> getValidExtensionsForTiles() {
		return validExtensionsForTiles;
	}

	/**
	 * Retourne vrai si l'extension est acceptée pour import
	 * 
	 * @return
	 */
	public boolean isValidExtensionsForTile(String ext) {
		return validExtensionsForTiles.contains(ext.toLowerCase());
	}

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

		// creation d'un objet d'import
		manualImporter = new TileMaker();
		manualImporter.getListenerHandler().add(new ManualImportListener());
		manualImporter.enableCropping(confm.isCroppingEnabled());
		manualImporter.setCropRectangle(confm.getCropRectangle());

		// surveiller le presse papier
		clipboardm.watchClipBoardForImages(true, manualImporter);

		// notifications
		fireEvent(ImportManagerEvent.DIRECTORY_IMPORT_STARTED);

	}

	/**
	 * Arret de l'import manuel
	 */
	public void stopManualImportLater() {

		if (manualImporter == null)
			return;

		manualImporter.stopImportLater();
		manualImporter = null;

		clipboardm.watchClipBoardForImages(false, null);

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
	}

	/**
	 * Arret de l'import par dossier
	 */
	public void stopDirectoryImportLater() {

		if (directoryImporter == null)
			return;

		directoryImporter.stopImportLater();
		directoryImporter = null;

		// la notification de fin se fait dans le listener associé à l'objet,
		// puisque l'import peut continuer après l'ordre d'arrêt
	}

	/**
	 * Renvoi vrai si un import de repertoire est en cours.
	 * 
	 * @return
	 */
	public Boolean isDirectoryImporting() {
		return directoryImporter != null && directoryImporter.isWorking();
	}

	/**
	 * Démarrer l'import de document.
	 * 
	 * @throws MapImportException
	 */
	public void startDocumentImport() throws MapImportException {

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
	}

	public void stopDocumentImportLater() {

		if (documentImporter == null)
			return;

		// TODO: Réviser les moyens d'arreter l'import
		// documentImporter.stopImportLater();

		documentImporter = null;

		// la notification de fin se fait dans le listener associé à l'objet,
		// puisque l'import peut continuer après l'ordre d'arrêt
	}

	public Boolean isDocumentImporting() {
		return documentImporter != null && documentImporter.isWorking();
	}

	/**
	 * Import automatique
	 * 
	 * @param parent
	 * @param obj
	 * @throws MapImportException
	 */
	public void startRobotImport() throws MapImportException {

		if (isDocumentImporting()) {
			throw new MapImportException(MapImportException.ALREADY_IMPORTING);
		}

		// lancer l'import
		robotImporter = new RobotImporter();
		robotImporter.addImporterListener(new RobotImporterListener());
		robotImporter.start();

		// notifications
		fireEvent(ImportManagerEvent.ROBOT_IMPORT_STARTED);
	}

	/**
	 * Etat de l'import auto
	 * 
	 * @return
	 */
	public Boolean isRobotImporting() {
		return robotImporter != null && robotImporter.isWorking();
	}

	/**
	 * Arreter l'import auto
	 */
	public void stopRobotImportLater() {

		if (robotImporter == null)
			return;

		robotImporter.abortImportLater();
		robotImporter = null;

		// la notification de fin se fait dans le listener associé à l'objet,
		// puisque l'import peut continuer après l'ordre d'arrêt
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
	 * 
	 */
	public void startCropAreaConfiguration(String mode) throws IOException,
			MapImportException {

		// verifier que la configuration ne soit pas déjà en cours
		if (cropConfigurator != null) {
			Log.error(new IllegalStateException("Invalid starting"));
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
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	public Params getSurfParameters() {
		return confm.getSurfParameters();
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
