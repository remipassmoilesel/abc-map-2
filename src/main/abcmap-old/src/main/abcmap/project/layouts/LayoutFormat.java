package abcmap.project.layouts;

import java.awt.Dimension;

import abcmap.utils.Utils;

public class LayoutFormat {

	public enum Orientation {

		/** Format d'orientation paysage */
		LANDSCAPE,

		/** Format d'orientation portrait */
		PORTRAIT
	}

	/** Format prédéfini */
	public static final LayoutFormat A5_PORTRAIT = new LayoutFormat(
			"A5 Portrait", new Dimension(148, 210));

	/** Format prédéfini */
	public static final LayoutFormat A5_LANDSCAPE = new LayoutFormat(
			"A5 Landscape", new Dimension(210, 148));

	/** Format prédéfini */
	public static final LayoutFormat A4_PORTRAIT = new LayoutFormat(
			"A4 Portrait", new Dimension(210, 297));

	/** Format prédéfini */
	public static final LayoutFormat A4_LANDSCAPE = new LayoutFormat(
			"A4 Landscape", new Dimension(297, 210));

	/** Format prédéfini */
	public static final LayoutFormat A3_PORTRAIT = new LayoutFormat(
			"A3 Portrait", new Dimension(297, 420));

	/** Format prédéfini */
	public static final LayoutFormat A3_LANDSCAPE = new LayoutFormat(
			"A3 Landscape", new Dimension(420, 297));

	/** Nom du format personnalisé */
	public static final String CUSTOM_FORMAT_NAME = "Custom";

	/**
	 * Le format custom apparait ici uniquement pour l'affichage. Dans le cas
	 * d'un format CUSTOM, un nouveau format est enregistré
	 */
	public static final LayoutFormat CUSTOM = new LayoutFormat(
			CUSTOM_FORMAT_NAME, new Dimension(200, 200));

	/**
	 * Tableau de formats disponibles
	 */
	private static final LayoutFormat[] AVAILABLES_DEFAULT_FORMATS = {
			A5_PORTRAIT, A5_LANDSCAPE, A4_PORTRAIT, A4_LANDSCAPE, A3_PORTRAIT,
			A3_LANDSCAPE, CUSTOM };

	/** Format par défaut */
	public static final LayoutFormat DEFAULT = A4_PORTRAIT;

	/**
	 * Retourne <b>une copie du format</b> correspondant aux dimensions (mm) ou
	 * null si le format ne correspond pas
	 * 
	 * @param label
	 * @return
	 */
	public static LayoutFormat getFormat(Dimension dim) {
		for (LayoutFormat format : AVAILABLES_DEFAULT_FORMATS) {
			if (format.getMillimeterDimensions().equals(dim)) {
				return new LayoutFormat(format);
			}
		}
		return null;
	}

	/**
	 * Retourne le format correspondant au nom ou null si aucun ne correspond
	 * 
	 * @param name
	 * @return
	 */
	public static LayoutFormat getFormat(String name) {
		for (LayoutFormat format : AVAILABLES_DEFAULT_FORMATS) {
			if (format.getName().equals(name)) {
				return format;
			}
		}
		return null;
	}

	/**
	 * Retourne un tableau contenant tous les formats disponibles
	 * 
	 * @return
	 */
	public static LayoutFormat[] getAvailablesFormats() {
		return AVAILABLES_DEFAULT_FORMATS;
	}

	/** Le nom du format */
	private String name;

	/** Les dimensions en millimètres */
	private Dimension mmDimensions;

	/**
	 * Créer un nouveau format à partir des caractéristiques passées en
	 * argument.
	 * 
	 * @param name
	 * @param dim
	 */
	public LayoutFormat(String name, Dimension dim) {
		super();
		this.name = new String(name);
		this.mmDimensions = new Dimension(dim);
	}

	/**
	 * Créer un nouveau format à partir des caractéristiques du format passé en
	 * argument.
	 * 
	 * @param format
	 */
	public LayoutFormat(LayoutFormat format) {
		this(format.getName(), format.mmDimensions);
	}

	/**
	 * Retourne le nom du format
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Modifie le nom du format
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retourne les dimensions en millimètres
	 * 
	 * @return
	 */
	public Dimension getMillimeterDimensions() {
		return new Dimension(mmDimensions);
	}

	/**
	 * Retourne la largeur en millimètres
	 * 
	 * @return
	 */
	public double getWidth() {
		return mmDimensions.width;
	}

	/**
	 * Retourne la hauteur
	 * 
	 * @return
	 */
	public double getHeight() {
		return mmDimensions.height;
	}

	/**
	 * Retourne les dimensions en pixel ou en mm<br>
	 * Les dimensions en Pixel sont soumises à la valeur <b>dpi</b> Les
	 * dimensions sont orientées.
	 * 
	 * @param unit
	 * @return
	 */
	public Dimension getPixelDimensions(Double resDpi) {
		int w = new Double(Utils.millimeterToPixel(mmDimensions.width, resDpi))
				.intValue();

		int h = new Double(Utils.millimeterToPixel(mmDimensions.height, resDpi))
				.intValue();

		return new Dimension(w, h);
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { name, mmDimensions, };
		Object[] keys = new Object[] { "name", "mmDimensions", };

		return Utils.toString(this, keys, values);

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof LayoutFormat == false)
			return false;

		LayoutFormat lsf = (LayoutFormat) obj;
		int i = 0;
		if (Utils.safeEquals(this.mmDimensions, lsf.mmDimensions))
			i++;
		if (Utils.safeEquals(this.name, lsf.name))
			i++;

		return i == 2;
	}

	@Override
	public Object clone() {
		return new LayoutFormat(this);
	}

	/**
	 * Retourne l'orientation de la feuille, portrait ou paysage.
	 * 
	 * @return
	 */
	public Orientation getOrientation() {
		return mmDimensions.width > mmDimensions.height ? Orientation.LANDSCAPE
				: Orientation.PORTRAIT;
	}

}
