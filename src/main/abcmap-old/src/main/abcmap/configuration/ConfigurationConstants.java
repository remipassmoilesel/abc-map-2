package abcmap.configuration;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.csv.CSVFormat;

import abcmap.project.loaders.AbmConstants;
import abcmap.surf.Params;

public class ConfigurationConstants {

	/**
	 * Racine des paquetages d'éléments d'interaction - permet la recherche par
	 * introspection
	 */
	public static final String IE_PACKAGE_ROOT = "abcmap.gui.ie";
	public static final String PLUGINS_PACKAGE_ROOT = "abcmap.plugins";

	/** Package des formes du canevas */
	public final static String DRAW_PACKAGE = "abcmap.draw.shapes";

	// langues du logiciel
	public static final String FRENCH = "fr";
	public static final String ENGLISH = "en";
	public static final String SPANISH = "es";

	public static final String[] LANGUAGES = new String[] { FRENCH, ENGLISH,
			SPANISH };
	public static final String[] LANGUAGE_NAMES = new String[] { "Français",
			"English", "Español" };

	// nom et version du logiciel
	public static final String SOFTWARE_NAME = "Abc-Map.fr";
	public static final String SOFTWARE_VERSION = "2.00";

	// urls du site et de l'aide
	public static final String WEBSITE_URL = "http://abc-map.fr/";
	public static final String WEBSITE_FAQ_URL = "http://abc-map.fr/faq/";
	public static final String PROJECT_PRES_PAGE_URL = WEBSITE_URL + "project/";
	public static final String VOTE_PAGE_URL = WEBSITE_URL + "vote/";
	public static final String NEWS_PAGE_URL = WEBSITE_URL + "news/";
	public static final String HELP_PAGE_URL = WEBSITE_URL + "help/";
	public static final String BUG_REPORT_URL = WEBSITE_URL
			+ "contact/?action=report";
	public static final String ASK_FORM_URL = WEBSITE_URL
			+ "contact/?action=ask";

	// Parametres d'affichage et d'impression
	// En DPI
	public static final Float SCREEN_RESOLUTION = (float) Toolkit
			.getDefaultToolkit().getScreenResolution();
	public static final Float JAVA_RESOLUTION = 72f;
	public static final Integer DEFAULT_PRINT_RESOLUTION = 300;

	// chemins du logiciel
	public static String SYSTEM_HOME_PATH = FileSystemView.getFileSystemView()
			.getDefaultDirectory().getAbsolutePath();
	public static final String SYMBOLS_DIR_PATH = "./symbols/";
	public static final String PROFILE_ROOT_PATH = "./profiles/";
	public static final File TEMP_PGRM_DIRECTORY = new File("./tmp/");
	public static final String LOG_DIR_PATH = "./log/";
	public static final String HELP_DIR = "./help/";

	// ihm
	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	public static final int SCROLLBAR_UNIT_INCREMENT = 20;
	public static final Dimension MINIMUM_MAP_DIMENSIONS = new Dimension(800,
			600);

	/**
	 * Panel de parametres surf. 5 niveaux, du plus rapide au plus complet
	 * 
	 * Params( int octaves, int layers, float threshold, int initStep, boolean
	 * upright, boolean displayOrientationVectors, boolean
	 * displayDescriptorWindows, int lineWidth, boolean displayStatistics)
	 * 
	 */
	public static Params[] SURF_PARAMS = new Params[] {
			new Params(4, 4, 0.01f, 2, false, false, false, 1, false),
			new Params(4, 4, 0.001f, 2, false, false, false, 1, false),
			new Params(4, 4, 0.0001f, 2, false, false, false, 1, false),
			new Params(4, 4, 0.00001f, 2, false, false, false, 1, false),
			new Params(4, 4, 0.000001f, 2, false, false, false, 1, false), };

	// configuration
	public static final String PROFILE_EXTENSION = "prf";
	public static final String DEFAULT_PROFILE_PATH = ConfigurationConstants.PROFILE_ROOT_PATH
			+ "default." + PROFILE_EXTENSION;
	public static final String SYSTEM_PROFILE_PATH = ConfigurationConstants.PROFILE_ROOT_PATH
			+ "system";

	public static final String XML_PARAMETER_TAG = "parameter";
	public static final String XML_ROOT_NAME = "parameters";
	public static final String XML_PARAMETER_ATTRIBUTE_NAME = "name";

	// Projet
	public static final String DESCRIPTOR_NAME = AbmConstants.DESCRIPTOR_FILE_NAME;
	public static final String PROJECT_EXTENSION = "abm";
	public static final Dimension PROJECT_DEFAULT_DIMENSIONS = new Dimension(
			600, 400);

	public static final CSVFormat DEFAULT_CSV_FORMAT = CSVFormat.RFC4180;

}
