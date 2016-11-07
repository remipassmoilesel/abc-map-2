package abcmap.draw.symbols;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import abcmap.managers.Log;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class SymbolImage {

	private String set;
	private int code;
	private Color color;
	private int size;
	private BufferedImage image;

	private int width;
	private int height;
	private int factor;

	SymbolImage(String set, int code, int size, Color color) {
		this.set = set;
		this.code = code;
		this.size = size;
		this.color = color;

		this.width = -1;
		this.height = -1;

		// l'image est représentée x "factor" plus grande
		this.factor = 1;
	}

	/**
	 * Créer l'image à partir de ses caracteristiques
	 */
	public void createImage() {

		// la police à utiliser
		Font font = SymbolImageLibrary.getSetFont(set);

		// font indisponible, prendre la premiere disponible
		if (font == null) {
			set = SymbolImageLibrary.getAvailablesSets().get(0);
			font = SymbolImageLibrary.getSetFont(set);
		}

		// ajustement de la fonte
		Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		if (color != null)
			map.put(TextAttribute.FOREGROUND, color);
		map.put(TextAttribute.SIZE, size * factor);
		font = font.deriveFont(map);

		// verifier le code du caractere à dessiner
		if (SymbolImageLibrary.isCodeValid(set, code) == false) {
			code = SymbolImageLibrary.getAvailablesCodesFor(set).get(0);
		}

		// le caractère à dessiner
		String symbol = Character.toString((char) code);

		// creer une image plus grande que necessaire
		// la taille du caractere ne pourra être calculée qu'avec un contexte
		// graphique
		// attention: en dessous de x3, fm.getwidth * factor peut être plus
		// grand que size * factor * 3
		image = new BufferedImage(size * factor * 3, size * factor * 3,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) image.getGraphics();

		// antialiasing
		GuiUtils.applyQualityRenderingHints(g2d);

		// objet de mesures
		FontMetrics fm = g2d.getFontMetrics(font);

		// recadrer une premiere fois l'image
		// * 2 la hauteur à cause d'une imprécision de getHeight
		image = image.getSubimage(0, 0, fm.stringWidth(symbol), fm.getHeight() * 2);

		// dessin du symbole
		g2d.setFont(font);
		g2d.drawString(symbol, 0, fm.getHeight());

		// recadrage final de l'image
		try {
			image = Utils.removeTransparentBorders(image);

			// reajuster les dimensions
			width = image.getWidth() / factor;
			height = image.getHeight() / factor;

		} catch (Exception e) {
			Log.error(e);
		}

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof SymbolImage == false)
			return false;

		SymbolImage shp = (SymbolImage) obj;

		Object[] toCompare1 = new Object[] { this.set, this.code, this.color, this.size, this.width,
				this.height };

		Object[] toCompare2 = new Object[] { shp.set, shp.code, shp.color, shp.size, shp.width,
				shp.height };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	public String getSet() {
		return set;
	}

	public void setSet(String set) {
		this.set = set;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getFactor() {
		return factor;
	}

}
