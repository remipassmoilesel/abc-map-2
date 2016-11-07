package abcmap.importation.data.reader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import abcmap.exceptions.DataImportException;
import abcmap.importation.data.DataEntryList;

public abstract class AbstractDataParser {

	/** Préfixe par défaut des champs */
	public static final String HEADER_DEFAULT_PREFIX = "field_";

	/** Maximum à parser */
	public static final int MAX_DATA_PARSING = 20000;

	/** Nombre de champs minimum pour qu'une ligne soit prise en compte */
	public static final int MINIMUM_LINE_SIZE = 2;

	/** Ligne ou sont présents les entete, en partant de zéro */
	public static final int HEADERS_LABELS_INDEX = 1;

	/** Index du header latitude, en partant de zéro */
	public static final int HEADER_INDEX_LATITUDE = 0;

	/** Index du header longitude, en partant de zéro */
	public static final int HEADER_INDEX_LONGITUDE = 1;

	/** Index du premier champs personnalisé éventuel, en partant de zero */
	public static final int HEADER_FIRST_FIELD_INDEX = 2;

	/** Texte représentant le champs 'latitude' */
	public static final String LABEL_LATITUDE = "latitude";

	/** Texte représentant le champs 'longitude' */
	public static final String LABEL_LONGITUDE = "longitude";

	/** Texte représentant le champs 'altitude' */
	public static final String LABEL_ELEVATION = "elevation";

	/** Texte représentant le champs 'temps' */
	public static final String LABEL_TIME = "time";

	/** Texte représentant le champs 'type de forme' */
	public static final String LABEL_TYPE = "type";

	/** Texte représentant le champs 'commentaire sur le point' */
	public static final String LABEL_COMMENT = "comment";

	/** Texte représentant le champs 'nom de point' */
	public static final String LABEL_NAME = "name";

	/** Texte représentant le champs 'description' */
	public static final String LABEL_DESCRIPTION = "description";

	/** Type d'objet GPX représentant 'route' */
	public static final String GPX_TYPE_ROUTE = "gpx_route";

	/** Type d'objet GPX représentant 'chemin de point' */
	public static final String GPX_TYPE_WAYPOINT = "gpx_waypoint";

	/** Type d'objet GPX représentant 'trace' */
	public static final String GPX_TYPE_TRACK = "gpx_track";

	/**
	 * Retourne un lecteur compatible avec l'extension ou null si aucun ne
	 * correspond.
	 * 
	 * @param extension
	 * @return
	 */
	public static AbstractDataParser getParserFor(String extension) {

		if (extension == null) {
			throw new NullPointerException("Extension is null");
		}

		// minuscules et sans espaces superflus
		extension = extension.toLowerCase().trim();

		// recuperer tous les lecteurs disponibles
		for (AbstractDataParser p : AbstractDataParser.getAvailableParsers()) {
			if (p.isSupportedExtension(extension)) {
				return p;
			}
		}

		// pas de parser trouvé, retour null
		return null;

	}

	public static AbstractDataParser[] getAvailableParsers() {
		return new AbstractDataParser[] { new CsvDataParser(), new GpxDataParser() };
	}

	/**
	 * Retourne la liste des extensions supportées en minuscules
	 * 
	 * @return
	 */
	public abstract String[] getSupportedExtensions();

	/**
	 * Retourne vrai si l'extension est supportée par le lecteur
	 * 
	 * @param extension
	 * @return
	 */
	public abstract boolean isSupportedExtension(String extension);

	/**
	 * Parse le fichier et retourne une liste d'enregistrements
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws DataImportException
	 */
	public abstract DataEntryList parseFile(File file) throws IOException, DataImportException;

	/**
	 * Parse le fichier et retourne la liste des entetes. Si un entete est vide
	 * génére un nom.
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws DataImportException
	 */
	public abstract ArrayList<String> getHeaders(File file) throws IOException, DataImportException;

}
