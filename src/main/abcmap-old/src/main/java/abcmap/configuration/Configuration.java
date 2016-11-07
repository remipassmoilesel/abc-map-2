package abcmap.configuration;

import abcmap.importation.documents.DocumentImporter;
import abcmap.importation.robot.RobotCaptureMode;

/**
 * Objet contenant la configuration souhaitee par l'utilisateur <br>
 * N'utiliser que des noms tout en majuscule <br>
 * N'utiliser que des objets <br>
 * Ne pas utiliser int, float, ...
 *
 * @author remipassmoilesel
 *
 */
public class Configuration {

	/*
	 * Général
	 */

	/** La langue utilisée dans le l'interface */
	public String LANGUAGE = ConfigurationConstants.FRENCH;

	/** Le répertoire home de l'utilisateur */
	public String HOME = ConfigurationConstants.SYSTEM_HOME_PATH;

	/*
	 * Profil de configuration
	 */

	/** Le titre du profil de configuration */
	public String PROFILE_TITLE = "Nouveau profil";

	/** Un éventuel commentaire sur le profil de configuration */
	public String PROFILE_COMMENT = "Commentaires";

	/** Le chemin vers le profil de configuration */
	public String PROFILE_PATH = ConfigurationConstants.DEFAULT_PROFILE_PATH;

	/** Si vrai, sauvegarde du profil en quittant le logiciel */
	public Boolean SAVE_PROFILE_WHEN_QUIT = true;

	/*
	 * Recadrage
	 */

	/** Si vrai active le recadrage */
	public Boolean ENABLE_CROPPING = true;

	/** Valeur x du rectangle du recadrage */
	public Integer CROP_AREA_SELECTION_X = 50;

	/** Valeur y du rectangle du recadrage */
	public Integer CROP_AREA_SELECTION_Y = 50;

	/** Valeur largeur du rectangle du recadrage */
	public Integer CROP_AREA_SELECTION_W = 400;

	/** Valeur hauteur du rectangle du recadrage */
	public Integer CROP_AREA_SELECTION_H = 400;

	/*
	 * Import et analyse, général
	 */

	/**
	 * Delai en MS entre l'ordre de masquer une fenêtre et son masquage effectif
	 */
	public Integer WINDOW_HIDDING_DELAY = 700;

	/** Nombre de points correspondants nécéssaire pour assemblage d'image */
	public Integer MATCHING_POINTS_THRESHOLD = 20;

	/** Modede réglage d'analyse SURF */
	public Integer SURF_MODE = 2;

	/** Si vrai, averti tout de suite l'utilisateur en cas de tuile refusée */
	public Boolean ALERT_NOW_IF_REFUSED_TILES = false;

	/*
	 * Import de répertoire
	 */

	/** Le répertoire d'import de répertoire d'image */
	public String DIRECTORY_IMPORT_PATH = ConfigurationConstants.SYSTEM_HOME_PATH;

	/*
	 * Import de document
	 */

	/** Le chemin d'import de document */
	public String DOCUMENT_IMPORT_PATH = ConfigurationConstants.SYSTEM_HOME_PATH;

	/** Le type d'import du document: en tant que tuile ou image */
	public String DOCUMENT_IMPORT_TYPE = DocumentImporter.IMPORT_AS_TILE;
	/**
	 * La liste des pages à importer. Le compte commence à un. Si un zéro est
	 * dans la liste, toutes les pages seront importées.
	 */
	public String DOCUMENT_IMPORT_PAGES = DocumentImporter.ALL_PAGES;

	/** Le facteur de reproduction des documents à importer */
	public Float DOCUMENT_IMPORT_FACTOR = 1f;

	/*
	 * Import automatique
	 */

	/** Le mode d'import. */
	public String ROBOT_IMPORT_MODE = RobotCaptureMode.START_FROM_ULC.toString();

	/** La surface couverte à chaque bord de tuile */
	public Float ROBOT_IMPORT_COVERING = 0.1f;

	/** La largeur de la surface à importer */
	public Integer ROBOT_IMPORT_WIDTH = 5;

	/** La hauteur de la surface à importer */
	public Integer ROBOT_IMPORT_HEIGHT = 5;

	/** Le delai avant le début de déplacement */
	public Integer ROBOT_IMPORT_MOVING_DELAY = 1000;

	/** Le délai avant la capture d'écran */
	public Integer ROBOT_IMPORT_CAPTURE_DELAY = 2000;

	/*
	 * Import de liste
	 */

	/** Le délai avant la capture d'écran */
	public String DATA_IMPORT_PATH = "";

}