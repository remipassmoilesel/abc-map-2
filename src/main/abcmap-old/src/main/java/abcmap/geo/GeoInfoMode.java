package abcmap.geo;

import java.util.Arrays;

import abcmap.managers.Log;

/**
 * Gestion de chaine de description d'informations géographique.
 * 
 * @author remipassmoilesel
 *
 */
public class GeoInfoMode {

	public static final int MIN_TEXT_SIZE = 5;

	public static final GeoInfoMode ALL_INFORMATIONS = new GeoInfoMode("111111");

	public static final GeoInfoMode NO_INFORMATIONS = new GeoInfoMode("000000");

	// 1 caractere max
	public static final String INFOMODE_TRUE = "1";
	public static final String INFOMODE_FALSE = "0";

	private static final int POS_DD_INDEX = 0;
	private static final int POS_DMD_INDEX = 1;
	private static final int POS_DMS_INDEX = 2;
	private static final int RANGE_INDEX = 3;
	private static final int AZIMUTH_INDEX = 4;
	private static final int DRAW_ANCHOR = 5;

	private Boolean posDD;
	private Boolean posDMD;
	private Boolean posDMS;
	private Boolean range;
	private Boolean azimuth;
	private boolean drawAnchor;

	public GeoInfoMode() {

		posDD = false;
		posDMD = false;
		posDMS = false;
		range = false;
		azimuth = false;
		drawAnchor = false;

	}

	public GeoInfoMode(String str) {

		this();

		// la chaine est trop courte, ajout de faux
		if (str.length() < getArray().length) {

			Log.debug(new IllegalArgumentException("String to short"));

			for (int i = str.length(); i < getArray().length; i++) {
				str += INFOMODE_FALSE;
			}
		}

		posDD = str.substring(POS_DD_INDEX, POS_DD_INDEX + 1).equals(INFOMODE_TRUE);
		posDMD = str.substring(POS_DMD_INDEX, POS_DMD_INDEX + 1).equals(INFOMODE_TRUE);
		posDMS = str.substring(POS_DMS_INDEX, POS_DMS_INDEX + 1).equals(INFOMODE_TRUE);
		range = str.substring(RANGE_INDEX, RANGE_INDEX + 1).equals(INFOMODE_TRUE);
		azimuth = str.substring(AZIMUTH_INDEX, AZIMUTH_INDEX + 1).equals(INFOMODE_TRUE);
		drawAnchor = str.substring(DRAW_ANCHOR, DRAW_ANCHOR + 1).equals(INFOMODE_TRUE);

	}

	/**
	 * 
	 * 0: DD<br>
	 * 1: DMD<br>
	 * 2: DMS<br>
	 * 3: RANGE<br>
	 * 4: AZ<br>
	 * 5: Ancre<br>
	 * 
	 * @param v
	 */
	public GeoInfoMode(boolean[] v) {

		this();

		if (v.length < getArray().length) {

			Log.debug(new IllegalArgumentException("Array to short"));

			boolean[] lastV = v;
			v = new boolean[getArray().length];
			for (int i = 0; i < v.length; i++) {
				if (i < lastV.length) {
					v[i] = lastV[i];
				} else {
					v[i] = false;
				}
			}
		}

		posDD = v[POS_DD_INDEX];
		posDMD = v[POS_DMD_INDEX];
		posDMS = v[POS_DMS_INDEX];
		range = v[RANGE_INDEX];
		azimuth = v[AZIMUTH_INDEX];
		drawAnchor = v[DRAW_ANCHOR];

	}

	public GeoInfoMode(GeoInfoMode m) {

		this.posDD = m.posDD;
		this.posDMD = m.posDMD;
		this.posDMS = m.posDMS;
		this.range = m.range;
		this.azimuth = m.azimuth;
		this.drawAnchor = m.drawAnchor;

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof GeoInfoMode == false)
			return false;

		GeoInfoMode shp = (GeoInfoMode) obj;

		Object[] toCompare1 = new Object[] { this.posDD, this.posDMD, this.posDMS, this.range,
				this.azimuth, this.drawAnchor };
		Object[] toCompare2 = new Object[] { shp.posDD, shp.posDMD, shp.posDMS, shp.range,
				shp.azimuth, shp.drawAnchor };

		if (toCompare1.length != toCompare2.length)
			throw new IllegalStateException();

		return Arrays.deepEquals(toCompare1, toCompare2);
	}

	public boolean[] getArray() {
		return new boolean[] { posDD, posDMD, posDMS, range, azimuth, drawAnchor };
	}

	@Override
	public String toString() {

		boolean[] values = getArray();

		StringBuilder sb = new StringBuilder(values.length);
		for (boolean v : values) {
			sb.append(v ? INFOMODE_TRUE : INFOMODE_FALSE);
		}

		return sb.toString();
	}

	public static GeoInfoMode valueOf(String str) {
		return new GeoInfoMode(str);
	}

	/**
	 * Renvoie vrai si une information doit être affichée
	 * 
	 * @param mode
	 * @return
	 */
	public static boolean isInformationModeNotEmpty(GeoInfoMode mode) {

		if (mode == null)
			return false;

		// ne pas prendre en compte le flag non significatif
		GeoInfoMode m2 = new GeoInfoMode(mode);
		m2.drawAnchor = false;

		return !NO_INFORMATIONS.equals(m2);
	}

	public boolean isDrawAnchor() {
		return drawAnchor;
	}

	public void setDrawAnchor(boolean drawAnchor) {
		this.drawAnchor = drawAnchor;
	}

	public boolean isPosDD() {
		return posDD;
	}

	public void setPosDD(boolean posDD) {
		this.posDD = posDD;
	}

	public boolean isPosDMD() {
		return posDMD;
	}

	public void setPosDMD(boolean posDMD) {
		this.posDMD = posDMD;
	}

	public boolean isPosDMS() {
		return posDMS;
	}

	public void setPosDMS(boolean posDMS) {
		this.posDMS = posDMS;
	}

	public boolean isRange() {
		return range;
	}

	public void setRange(boolean range) {
		this.range = range;
	}

	public boolean isAzimuth() {
		return azimuth;
	}

	public void setAzimuth(boolean azimuth) {
		this.azimuth = azimuth;
	}

}
