package abcmap.geo;

import java.awt.geom.Point2D;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import abcmap.exceptions.InvalidInputException;
import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.CoordinateProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.Utils;

/**
 * Point sur la carte ayant une correspondance pixel / coordonnees
 * 
 * @author remipassmoilesel
 * 
 */
public class Coordinate implements AcceptPropertiesContainer {

	// formatage des chiffres
	private static DecimalFormat minutesDecNF;
	private static DecimalFormat pixelNF;
	private static DecimalFormat degreesIntNF;
	private static DecimalFormat degreesDecNF;
	private static DecimalFormat secondsIntNF;
	private static DecimalFormat minutesIntNF;

	static {

		degreesDecNF = new DecimalFormat("00.000");
		degreesDecNF.setRoundingMode(RoundingMode.UP);

		degreesIntNF = new DecimalFormat("00");
		degreesIntNF.setRoundingMode(RoundingMode.UP);

		minutesIntNF = new DecimalFormat("00");
		minutesIntNF.setRoundingMode(RoundingMode.UP);

		minutesDecNF = new DecimalFormat("00.000");
		minutesDecNF.setRoundingMode(RoundingMode.UP);

		secondsIntNF = new DecimalFormat("00");
		secondsIntNF.setRoundingMode(RoundingMode.UP);

		pixelNF = new DecimalFormat("#");
		pixelNF.setRoundingMode(RoundingMode.UP);

	}

	public double latitudeSec;
	public double longitudeSec;

	public double latitudePx;
	public double longitudePx;

	public Coordinate() {

		// coordonnées en secondes
		latitudeSec = 0d;
		longitudeSec = 0d;

		// coordonnées en pixels
		latitudePx = 0d;
		longitudePx = 0d;

	}

	/**
	 * Creer une coordonnée à partir des valeurs passées en parametre.
	 * 
	 * @param latitudePx
	 * @param longitudePx
	 * @param latitudeDg
	 * @param longitudeDg
	 */
	public Coordinate(double latitudeDg, double longitudeDg, double latitudePx,
			double longitudePx) {
		this();

		// coord en pixels
		this.latitudePx = latitudePx;
		this.longitudePx = longitudePx;

		// coord en secondes
		this.latitudeSec = latitudeDg * 3600;
		this.longitudeSec = longitudeDg * 3600;
	}

	/**
	 * Duplication
	 * 
	 * @param coord
	 */
	public Coordinate(Coordinate coord) {
		this();
		latitudePx = coord.latitudePx;
		longitudePx = coord.longitudePx;
		latitudeSec = coord.latitudeSec;
		longitudeSec = coord.longitudeSec;
	}

	/**
	 * Créer une coordonnée à partir des valeurs en pixel passées en paramétres.
	 * 
	 * @param p
	 */
	public Coordinate(Point2D pixel) {
		this(pixel.getY(), pixel.getX(), 0, 0);
	}

	/**
	 * Affecter des valeurs en degres decimaux.
	 * 
	 * @param pt
	 */
	public void setDegreesPoint(Point2D pt) {
		longitudeSec = pt.getX() * 3600;
		latitudeSec = pt.getY() * 3600;
	}

	/**
	 * Affecter des valeurs en pixel.
	 * 
	 * @param pt
	 */
	public void setPixelPoint(Point2D pt) {
		longitudePx = pt.getX();
		latitudePx = pt.getY();
	}

	/**
	 * Retourne latitude et longitude en degrees et minutes decimales. <br>
	 * [y]=>degres lat <br>
	 * [x]=>degres long <br>
	 * 
	 * @return
	 */
	public Point2D getDegreesPoint() {
		return new Point2D.Double(longitudeSec / 3600, latitudeSec / 3600);
	}

	public Point2D getRoundedDegreesPoint() {
		return new Point2D.Double(Utils.round(longitudeSec / 3600, 6),
				Utils.round(latitudeSec / 3600, 6));
	}

	/**
	 * Retourne latitude et longitude en degrees et minutes decimales
	 * <p>
	 * [0]=>degres lat <br>
	 * [1]=>minutes lat <br>
	 * [2]=>degres long <br>
	 * [3]=>minutes long
	 * 
	 * <p>
	 * Nécéssite d'être remaniée.
	 * 
	 * @return
	 */
	public double[] getDegreesMinutesCoords() {

		// travailler avec des valeurs positives
		double latSec = Math.abs(latitudeSec);
		double lngSec = Math.abs(longitudeSec);

		// calcul de la latitude en degres
		double dLat = Math.floor((latSec / 3600d));

		// calcul des minutes de latitude
		double mLat = Utils.round((latSec / 3600d - dLat) * 60d, 4);

		// calcul de la longitude en degres
		double dLng = Math.floor(lngSec / 3600d);

		// calcul des minutes de longitude
		double mLng = Utils.round((lngSec / 3600d - dLng) * 60d, 4);

		// corriger le signe
		if (latitudeSec < 0) {
			dLat = -dLat;
		}

		if (longitudeSec < 0) {
			dLng = -dLng;
		}

		return new double[] { dLat, mLat, dLng, mLng };
	}

	public double[] getRoundedDegreesMinutesCoords() {
		double[] p = getDegreesMinutesCoords();
		return new double[] { p[0], Utils.round(p[1], 4), p[2],
				Utils.round(p[3], 4), };
	}

	/**
	 * Retourne latitude et longitude en degrees minutes secondes.<br>
	 * Retourne sous la forme d'entiers.
	 * <p>
	 * 
	 * [0]=>degree lat <br>
	 * [1]=>minutes lat <br>
	 * [2]=>secondes lat <br>
	 * [3]=>degree long <br>
	 * [4]=>minutes long <br>
	 * [5]=>secondes long <br>
	 * 
	 * <p>
	 * Nécéssite d'être remaniée.
	 * 
	 * @return
	 */
	public double[] getDMSCoords() {

		// travailler avec des valeurs positives
		double latSec = Math.abs(latitudeSec);
		double lngSec = Math.abs(longitudeSec);

		// calcul de la latitude
		double dLat = Math.floor(latSec / 3600d);
		double mLat = Math.floor((latSec / 3600d - dLat) * 60d);
		double sLat = Math
				.round((((latSec / 3600d - dLat) * 60d) - mLat) * 60d);

		// calcul de la longitude
		double dLng = Math.floor(lngSec / 3600d);
		double mLng = Math.floor((lngSec / 3600d - dLng) * 60d);
		double sLng = Math
				.round((((lngSec / 3600d - dLng) * 60d) - mLng) * 60d);

		// vérifier les secondes après arrondi
		// Exemple: 13.2° peut donner 13° 11'' 60'
		if (sLat > 59) {
			mLat += sLat - 59;
			sLat = 0;
		}

		if (sLng > 59) {
			mLng += sLng - 59;
			sLng = 0;
		}

		// vérifier les minutes
		if (mLat > 59) {
			dLat += mLat - 59;
			mLat = 0;
		}
		if (mLng > 59) {
			dLng += mLng - 59;
			mLng = 0;
		}

		// corriger le signe
		if (latitudeSec < 0)
			dLat = -dLat;

		if (longitudeSec < 0)
			dLng = -dLng;

		return new double[] { dLat, mLat, sLat, dLng, mLng, sLng };
	}

	/**
	 * Coordonnees en pixels<br>
	 * [0]=>x<br>
	 * [1]=>y<br>
	 */
	public Point2D getPixelPoint() {
		return new Point2D.Double(longitudePx, latitudePx);
	}

	/**
	 * Affecter des valeurs en degrés.
	 * 
	 * @param lat
	 * @param lng
	 */
	public void setDegreeValue(double lat, double lng) {
		latitudeSec = lat * 3600;
		longitudeSec = lng * 3600;
	}

	/**
	 * Affecter des valeurs en pixels.
	 * 
	 * @param lat
	 * @param lng
	 */
	public void setPixelValue(double lat, double lng) {
		latitudePx = lat;
		longitudePx = lng;
	}

	/**
	 * Affecter des valeurs en degres et minutes.
	 * 
	 * @param dLat
	 * @param mLat
	 * @param dLng
	 * @param mLng
	 */
	public void setDegreeMinuteValue(double dLat, double mLat, double dLng,
			double mLng) {

		// Utiliser des valeurs absolues car les coordonnées negatives sont
		// exprimée sur les degres seulement, pas sur les minutes
		latitudeSec = Math.abs(dLat) * 3600d + Math.abs(mLat) * 60d;
		longitudeSec = Math.abs(dLng) * 3600d + Math.abs(mLng) * 60d;

		// rectifier le signe en fonction du nombre des degres
		if (dLat < 0) {
			latitudeSec = -latitudeSec;
		}
		if (dLng < 0) {
			longitudeSec = -longitudeSec;
		}

	}

	public void setDMSValue(double dLat, double mLat, double sLat, double dLng,
			double mLng, double sLng) {

		// Utiliser des valeurs absolues car les coordonnées negatives sont
		// exprimée sur les degres seulement, pas sur les minutes
		latitudeSec = Math.abs(dLat) * 3600d + Math.abs(mLat) * 60d
				+ Math.abs(sLat);
		longitudeSec = Math.abs(dLng) * 3600d + Math.abs(mLng) * 60d
				+ Math.abs(sLng);

		// rectifier le signe
		if (dLat < 0) {
			latitudeSec = -latitudeSec;
		}
		if (dLng < 0) {
			longitudeSec = -longitudeSec;
		}

	}

	/**
	 * Vérifier que les points soient bien distincts
	 * 
	 * @param ref1
	 * @param ref2
	 * @return
	 */
	public static boolean testIfDifferent(Coordinate ref1, Coordinate ref2) {

		double dDg = Point2D.distance(ref1.longitudeSec, ref1.latitudeSec,
				ref2.longitudeSec, ref2.latitudeSec);
		double dPx = Point2D.distance(ref1.longitudePx, ref1.latitudePx,
				ref2.longitudePx, ref2.latitudePx);

		return dDg != 0 && dPx != 0;
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		if (properties == null)
			throw new NullPointerException();

		CoordinateProperties pp = (CoordinateProperties) properties;
		if (pp.latitudePx != null) {
			this.latitudePx = new Double(pp.latitudePx);
		}

		if (pp.longitudePx != null) {
			this.longitudePx = new Double(pp.longitudePx);
		}

		if (pp.latitudeDg != null) {
			this.latitudeSec = new Double(pp.latitudeDg);
		}

		if (pp.longitudeDg != null) {
			this.longitudeSec = new Double(pp.longitudeDg);
		}

	}

	public PropertiesContainer getProperties() {
		CoordinateProperties pp = new CoordinateProperties();
		pp.latitudePx = latitudePx;
		pp.longitudePx = longitudePx;
		pp.latitudeDg = latitudeSec;
		pp.longitudeDg = longitudeSec;

		return pp;
	}

	/**
	 * Retourne la latitude en pixels.
	 * 
	 * @return
	 */
	public Double getLatitudePx() {
		return latitudePx;
	}

	/**
	 * Affecte la latitude en pixels.
	 * 
	 * @param val
	 */
	public void setLatitudePx(Double val) {
		latitudePx = val;
	}

	/**
	 * Retourne la latitude en secondes.
	 * 
	 * @return
	 */
	public Double getLatitudeSec() {
		return latitudeSec;
	}

	/**
	 * Affecte la latitude en secondes.
	 * 
	 * @param val
	 */
	public void setLatitudeSec(Double val) {
		latitudeSec = val;
	}

	/**
	 * Retourne la longitude en pixels.
	 * 
	 * @return
	 */
	public Double getLongitudePx() {
		return longitudePx;
	}

	/**
	 * Retourne la latitude en pixels.
	 * 
	 * @param val
	 */
	public void setLongitudePx(Double val) {
		longitudePx = val;
	}

	/**
	 * Retourne la longitude en secondes.
	 * 
	 * @return
	 */
	public Double getLongitudeSec() {
		return longitudeSec;
	}

	/**
	 * Affecte la longitude en secondes/
	 * 
	 * @param val
	 */
	public void setLongitudeSec(Double val) {
		longitudeSec = val;
	}

	/**
	 * Obtenir un affichage selon les unites desirees
	 * 
	 * @param mode
	 * @return
	 */
	public String getStringRepresentation(GeoConstants mode) {

		String latLabel = "Lat: ";
		String lngLabel = " Long: ";

		if (GeoConstants.DISPLAY_PIXELS.equals(mode)) {

			String x = pixelNF.format(latitudePx);
			String y = pixelNF.format(longitudePx);

			return "(pixel) " + latLabel + y + lngLabel + x;

		} else if (GeoConstants.DISPLAY_DEGREES_DEC.equals(mode)) {

			Point2D c = getDegreesPoint();

			String signLat = (c.getY() < 0) ? "-" : "+";
			String signLng = (c.getX() < 0) ? "-" : "+";

			String y = signLat + " " + degreesDecNF.format(Math.abs(c.getY()));
			String x = signLng + " " + degreesDecNF.format(Math.abs(c.getX()));

			return latLabel + y + "  " + lngLabel + x;

		} else if (GeoConstants.DISPLAY_DEGREES_MINUTES_DEC.equals(mode)) {

			double[] c = getDegreesMinutesCoords();

			String signLat = (c[0] < 0) ? "-" : "+";
			String signLng = (c[2] < 0) ? "-" : "+";

			String y = signLat + " " + degreesIntNF.format(Math.abs(c[0]))
					+ "° " + minutesDecNF.format(Math.abs(c[1])) + "'";
			String x = signLng + " " + degreesIntNF.format(Math.abs(c[2]))
					+ "° " + minutesDecNF.format(Math.abs(c[3])) + "'";

			return latLabel + y + "  " + lngLabel + x;

		} else if (GeoConstants.DISPLAY_DEGREES_MINUTES_SEC.equals(mode)) {

			double c[] = getDMSCoords();

			String signLat = (c[0] < 0) ? "-" : "+";
			String signLng = (c[3] < 0) ? "-" : "+";

			String y = signLat + " " + degreesIntNF.format(Math.abs(c[0]))
					+ "° " + minutesIntNF.format(Math.abs(c[1])) + "' "
					+ secondsIntNF.format(Math.abs(c[2])) + "\"";
			String x = signLng + " " + degreesIntNF.format(Math.abs(c[3]))
					+ "° " + minutesIntNF.format(Math.abs(c[4])) + "' "
					+ secondsIntNF.format(Math.abs(c[5])) + "\"";

			return latLabel + y + "  " + lngLabel + x;

		}

		else {
			throw new IllegalArgumentException("Unknown mode: " + mode);
		}
	}

	@Override
	public boolean equals(Object obj) {

		Object[] fields1 = new Object[] { this.latitudePx, this.longitudePx,
				this.latitudeSec, this.longitudeSec, };

		Object[] fields2 = null;
		if (obj instanceof Coordinate) {
			Coordinate obj2 = (Coordinate) obj;
			fields2 = new Object[] { obj2.latitudePx, obj2.longitudePx,
					obj2.latitudeSec, obj2.longitudeSec, };
		}

		return Utils.equalsUtil(this, obj, fields1, fields2);

	}

	@Override
	public String toString() {

		Object[] keys = new Object[] { "longitudeSec", "latitudeSec",
				"longitudePx", "latitudePx", };
		Object[] values = new Object[] { longitudeSec, latitudeSec,
				longitudePx, latitudePx, };

		return Utils.toString(this, keys, values);
	}

	/**
	 * Parsage de latitude ou de longitude au format décimal. Nécéssite
	 * d'enlever les caracteres blancs au préalable.
	 */
	private static final Pattern decimalDegreesPattern = Pattern
			.compile("^(-?\\d+[\\.,]?\\d*)°?$");
	private static final Pattern dmdPattern = Pattern
			.compile("^(-?\\d+)°(\\d+[\\.,]?\\d*)'?$");
	private static final Pattern dmsPattern = Pattern
			.compile("^(-?\\d+)°(\\d+)'(\\d+[\\.,]?\\d*)\"?$");

	/**
	 * Retourne un objet de coordonnées <i>géographiques</i> a partir des
	 * chaines latitude et longitude ou null si le format n'est pas reconnu.
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws InvalidInputException
	 */
	public static Coordinate valueOf(String latitude, String longitude) {

		// enlever les carcteres blancs
		latitude = latitude.replaceAll("\\s+", "");
		longitude = longitude.replaceAll("\\s+", "");

		// Cas n1, le plus courant, il y a point dans la chaine mais pas ' ni "
		// les coordonnées sont en degrés décimaux
		Matcher mtLat = decimalDegreesPattern.matcher(latitude);
		Matcher mtLng = decimalDegreesPattern.matcher(longitude);
		if (mtLat.find() && mtLng.find()) {

			double lat = Double.valueOf(mtLat.group(1));
			double lng = Double.valueOf(mtLng.group(1));

			return new Coordinate(lat, lng, 0, 0);

		}

		// Cas n2, il y a les caracteres ° et ', coordonnées en degrés et
		// minutes décimales
		mtLat = dmdPattern.matcher(latitude);
		mtLng = dmdPattern.matcher(longitude);
		if (mtLat.find() && mtLng.find()) {

			Coordinate coord = new Coordinate();

			double dLat = Double.valueOf(mtLat.group(1));
			double mLat = Double.valueOf(mtLat.group(2));
			double dLng = Double.valueOf(mtLng.group(1));
			double mLng = Double.valueOf(mtLng.group(2));

			coord.setDegreeMinuteValue(dLat, mLat, dLng, mLng);

			return coord;

		}

		// Cas n3, il y a les caracteres ° ' et ", coordonnées en degrés et
		// minutes et secondes décimales
		mtLat = dmsPattern.matcher(latitude);
		mtLng = dmsPattern.matcher(longitude);
		if (mtLat.find() && mtLng.find()) {

			Coordinate coord = new Coordinate();

			double dLat = Double.valueOf(mtLat.group(1));
			double mLat = Double.valueOf(mtLat.group(2));
			double sLat = Double.valueOf(mtLat.group(3));
			double dLng = Double.valueOf(mtLng.group(1));
			double mLng = Double.valueOf(mtLng.group(2));
			double sLng = Double.valueOf(mtLng.group(3));

			coord.setDMSValue(dLat, mLat, sLat, dLng, mLng, sLng);

			return coord;

		}

		// aucun format ne correspond, retour null
		return null;
	}
}
