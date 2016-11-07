package abcmap.importation;

import java.awt.Color;

/**
 * Indications concernant la charge mémoire d'une image en fonction de sa
 * taille.
 * 
 * @author remipassmoilesel
 *
 */
public class ImageMemoryIndicator {

	public static final int LEVEL_0 = 0;
	public static final int LEVEL_1 = 1;
	public static final int LEVEL_2 = 2;
	public static final int LEVEL_3 = 3;

	public static final ImageMemoryIndicator MIN_CHARGE = new ImageMemoryIndicator(LEVEL_0, 0, 1);
	public static final ImageMemoryIndicator NORMAL_CHARGE = new ImageMemoryIndicator(LEVEL_1, 1,
			5);
	public static final ImageMemoryIndicator LARGE_CHARGE = new ImageMemoryIndicator(LEVEL_2, 5,
			15);
	public static final ImageMemoryIndicator CONSIDERABLE_CHARGE = new ImageMemoryIndicator(LEVEL_3,
			15, Float.MAX_VALUE);

	/**
	 * Retourne un entier correspondant à un niveau de charge à partir duquel
	 * une charge correspondante est considérée comme lourde.
	 * 
	 * @return
	 */
	public static int getWeightThreshold() {
		return LEVEL_2;
	}

	/**
	 * Indicateurs de charge disponibles. Doivent être présent dans l'ordre
	 * croissant.
	 */
	private static ImageMemoryIndicator[] charges = new ImageMemoryIndicator[] { MIN_CHARGE,
			NORMAL_CHARGE, LARGE_CHARGE, CONSIDERABLE_CHARGE };

	public static ImageMemoryIndicator getIndicatorFor(double imageSizeMP) {

		for (ImageMemoryIndicator c : charges) {
			if (c.concernedBy(imageSizeMP)) {
				return c;
			}
		}

		return CONSIDERABLE_CHARGE;
	}

	public static ImageMemoryIndicator getIndicatorFor(double pixelWidth, double pixelHeight) {
		double valueMp = pixelWidth * pixelHeight / 1000000d;
		return getIndicatorFor(valueMp);
	}

	private float minVal;
	private float maxVal;
	private int level;
	private Color bgColor;
	private String levelLabel;
	private Color fgColor;

	private ImageMemoryIndicator(int level, float minVal, float maxVal) {

		this.minVal = minVal;
		this.maxVal = maxVal;
		this.level = level;

		if (LEVEL_0 == level) {
			this.bgColor = Color.white;
			this.fgColor = Color.black;
			this.levelLabel = "Charge non significative";
		}

		else if (LEVEL_1 == level) {
			this.bgColor = Color.yellow;
			this.fgColor = Color.black;
			this.levelLabel = "Charge normale";
		}

		else if (LEVEL_2 == level) {
			this.bgColor = Color.orange;
			this.fgColor = Color.black;
			this.levelLabel = "Charge lourde";
		}

		else if (LEVEL_3 == level) {
			this.bgColor = Color.red;
			this.fgColor = Color.white;
			this.levelLabel = "Charge considérable";
		}

	}

	public boolean concernedBy(double valueMp) {

		if (valueMp < 0) {
			throw new IllegalArgumentException("Invalid value: " + valueMp);
		}

		return valueMp >= minVal && valueMp < maxVal;
	}

	public boolean concernedBy(int pixelWidth, int pixelHeight) {
		double valueMp = pixelWidth * pixelHeight / 1000000d;
		return concernedBy(valueMp);
	}

	public int getLevel() {
		return level;
	}

	public float getMinVal() {
		return minVal;
	}

	public float getMaxVal() {
		return maxVal;
	}

	public String getLevelLabel() {
		return levelLabel;
	}

	public Color getFgColor() {
		return fgColor;
	}

	public Color getBgColor() {
		return bgColor;
	}

}
