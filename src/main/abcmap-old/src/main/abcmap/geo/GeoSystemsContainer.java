package abcmap.geo;

import java.util.Collection;
import java.util.HashMap;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.managers.Log;

public class GeoSystemsContainer {

	public static final String EMPTY_CRS = "EMPTY_CRS";
	public static final String WEB_MERCATOR = "3857";
	public static final String WGS_84 = "3395";

	public static final String DEFAULT_GEOSYSTEM = WGS_84;

	public static final String EPSG = "EPSG:";

	private HashMap<String, CoordinateReferenceSystem> availableCRS;

	/**
	 * Charger les systèmes de coordonnées
	 */
	public void loadDefaultCRS() {

		// creer la liste des disponibles
		availableCRS = new HashMap<String, CoordinateReferenceSystem>();

		// liste à ajouter dès le début
		String[] list = getPredefinedCodes();

		for (String code : list) {
			createAndReferenceSystem(code);
		}

	}

	public static String[] getPredefinedCodes() {
		return new String[] { WGS_84, WEB_MERCATOR, };
	}

	/**
	 * Retourne le systeme correspondant au code epsg spécifié ou null
	 * 
	 * @param epsgCode
	 * @return
	 */
	public CoordinateReferenceSystem getCRS(String epsgCode) {

		if (availableCRS.containsKey(epsgCode)) {
			return availableCRS.get(epsgCode);
		}

		else {
			return createAndReferenceSystem(epsgCode);
		}

	}

	/**
	 * Renvoi le systeme correspondant au code epsg spécifié ou null si erreur.
	 * 
	 * @param epsg
	 * @return
	 */
	private CoordinateReferenceSystem createAndReferenceSystem(String epsgCode) {

		try {

			// creer le systeme
			CoordinateReferenceSystem system = CRS.decode(EPSG + epsgCode);

			if (system == null) {
				return null;
			}

			// garder une reference
			availableCRS.put(epsgCode, system);

			return system;

		} catch (FactoryException e) {
			Log.debug(e);
			return null;
		}

	}

	public CoordinateReferenceSystem get(String name) {
		return availableCRS.get(name);
	}

	public Collection<CoordinateReferenceSystem> getAvailablesCRS() {
		return availableCRS.values();
	}

	public CoordinateReferenceSystem[] getAvailablesCRSarray() {
		return availableCRS.values().toArray(
				new CoordinateReferenceSystem[availableCRS.size()]);
	}

}
