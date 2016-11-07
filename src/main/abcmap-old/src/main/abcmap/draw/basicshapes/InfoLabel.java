package abcmap.draw.basicshapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;

import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.utils.Utils;

public class InfoLabel implements Drawable {

	public static final int MIN_MARGIN = 10;
	public static final int MIN_TEXT_SIZE = 10;

	public static final String NORTH = "NORTH";
	public static final String NORTH_EAST = "NORTH_EAST";
	public static final String EAST = "EAST";
	public static final String SOUTH_EAST = "SOUTH_EAST";
	public static final String SOUTH = "SOUTH";
	public static final String SOUTH_WEST = "SOUTH_WEST";
	public static final String WEST = "WEST";
	public static final String NORTH_WEST = "NORTH_WEST";

	public static final String DEFAULT_ANCHOR_MODE = NORTH_WEST;

	/** Les lignes de texte */
	private ArrayList<String> lines;

	/** Cooordonnées complètes de la forme */
	private Rectangle bounds;

	private boolean recomputeOnNextPaint;
	private Font font;
	private int textSize;
	private Stroke stroke;
	private Color textColor;
	private Color backgroundColor;
	private Color borderColor;

	/** Marge en pixel autour du texte */
	private int textMargins;

	/** Point autour duquel positionner l'etiquette si non null */
	private Point anchor;
	/** Mode de positionnement autour de l'ancre */
	private String anchorMode;
	/** Marge autour de l'ancre */
	private int anchorMargin;
	private int anchorHalfDiameter;
	private boolean drawAnchor;

	public InfoLabel() {

		// coordonnées de la forme. bounds.setLocation sert de positionnement
		// 'statique'
		this.bounds = new Rectangle();
		this.lines = new ArrayList<String>();
		this.textSize = 10;

		this.stroke = new BasicStroke(2);
		this.textColor = Color.black;
		this.backgroundColor = Color.lightGray;
		this.borderColor = Color.blue;

		this.textMargins = 10;

		// positionnement 'dynamique'
		this.anchor = null;
		this.anchorMode = null;
		this.anchorMargin = MIN_MARGIN;
		this.anchorHalfDiameter = 5;
		this.drawAnchor = true;

		refreshShape();
	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// police
		g.setFont(font);

		// recalculer uniquement si nécéssaire
		if (recomputeOnNextPaint) {

			// calcul des dimensions
			computeDimensions(g);

			// positionnement autour de l'ancre si necessaire
			if (anchor != null) {
				computePositionAroundAnchor();
			}

			// ne plus recalculer
			recomputeOnNextPaint = false;
		}

		// dessin le fond
		if (backgroundColor != null) {
			g.setPaint(backgroundColor);
			g.fill(bounds);
		}

		// calculer la position de depart, en fonction des marges
		// le tracé commence à la ligne de base de la police (en bas)
		int height = g.getFontMetrics().getHeight();
		int x = (int) (bounds.x + textMargins);
		int y = (int) (bounds.y + height + textMargins);

		// couleur du texte
		g.setColor(textColor);

		// écrire le texte
		for (String line : new ArrayList<String>(lines)) {
			g.drawString(line, x, y);
			y += height;
		}

		if (drawAnchor) {
			drawAnchor(g);
		}

		// dessiner le cadre
		g.setColor(borderColor);
		g.setStroke(stroke);
		g.draw(bounds);

	}

	private void computeDimensions(Graphics2D g) {
		// taille de la font pour calculs
		FontMetrics fm = g.getFontMetrics(font);

		// calcul des dimensions de l'étiquette
		bounds = new Rectangle();

		// calcul de la largeur et hauteur en fonction des lignes
		for (String line : lines) {

			// largeur
			int nw = fm.stringWidth(line);
			if (bounds.width < nw)
				bounds.width = nw;

			// hauteur
			bounds.height += fm.getHeight();

		}

		// ajouter de la marge
		bounds.width = bounds.width + textMargins * 2;
		bounds.height = bounds.height + textMargins * 2;
	}

	private void drawAnchor(Graphics2D g) {

		// recuperer l'angle le plus proche de l'ancre
		Point p1 = new Point();
		if (NORTH.equals(anchorMode)) {
			p1.x = bounds.x + bounds.width / 2;
			p1.y = bounds.y + bounds.height;
		}

		else if (NORTH_EAST.equals(anchorMode)) {
			p1.x = bounds.x;
			p1.y = bounds.y + bounds.height;
		}

		else if (EAST.equals(anchorMode)) {
			p1.x = bounds.x;
			p1.y = bounds.y + bounds.height / 2;
		}

		else if (SOUTH_EAST.equals(anchorMode)) {
			p1.x = bounds.x;
			p1.y = bounds.y;
		}

		else if (SOUTH.equals(anchorMode)) {
			p1.x = bounds.x + bounds.width / 2;
			p1.y = bounds.y;
		}

		else if (SOUTH_WEST.equals(anchorMode)) {
			p1.x = bounds.x + bounds.width;
			p1.y = bounds.y;
		}

		else if (WEST.equals(anchorMode)) {
			p1.x = bounds.x + bounds.width;
			p1.y = bounds.y + bounds.height / 2;
		}

		else if (NORTH_WEST.equals(anchorMode)) {
			p1.x = bounds.x + bounds.width;
			p1.y = bounds.y + bounds.height;
		}

		// dessiner un trait entre l'ancre et le coin concerné
		g.setStroke(stroke);
		g.drawLine(p1.x, p1.y, anchor.x, anchor.y);

		// dessiner un rond de couleur opposée à la bordure
		g.setColor(Utils.getOppositeColor(borderColor));
		g.fillOval(anchor.x - anchorHalfDiameter, anchor.y - anchorHalfDiameter,
				anchorHalfDiameter * 2, anchorHalfDiameter * 2);

	}

	private void computePositionAroundAnchor() {

		// verifier l'ancre
		if (anchor == null)
			return;

		// verifier le mode
		if (anchorMode == null)
			anchorMode = DEFAULT_ANCHOR_MODE;

		// verifier la marge
		if (anchorMargin < MIN_MARGIN)
			anchorMargin = MIN_MARGIN;

		if (NORTH.equals(anchorMode)) {
			bounds.x = anchor.x - bounds.width / 2;
			bounds.y = anchor.y - bounds.height - anchorMargin;
		}

		else if (NORTH_EAST.equals(anchorMode)) {
			bounds.x = anchor.x + anchorMargin;
			bounds.y = anchor.y - bounds.height - anchorMargin;
		}

		else if (EAST.equals(anchorMode)) {
			bounds.x = anchor.x + anchorMargin;
			bounds.y = anchor.y - bounds.height / 2;
		}

		else if (SOUTH_EAST.equals(anchorMode)) {
			bounds.x = anchor.x + anchorMargin;
			bounds.y = anchor.y + anchorMargin;
		}

		else if (SOUTH.equals(anchorMode)) {
			bounds.x = anchor.x - bounds.width / 2;
			bounds.y = anchor.y + anchorMargin;
		}

		else if (SOUTH_WEST.equals(anchorMode)) {
			bounds.x = anchor.x - bounds.width - anchorMargin;
			bounds.y = anchor.y + anchorMargin;
		}

		else if (WEST.equals(anchorMode)) {
			bounds.x = anchor.x - bounds.width - anchorMargin;
			bounds.y = anchor.y - bounds.height / 2;
		}

		else if (NORTH_WEST.equals(anchorMode)) {
			bounds.x = anchor.x - bounds.width - anchorMargin;
			bounds.y = anchor.y - bounds.height - anchorMargin;
		}

		else
			throw new IllegalArgumentException("Unknown mode: " + anchorMode);

	}

	public void addGeoLine(GeoConstants mode, Coordinate coordVal) {
		addLine(coordVal.getStringRepresentation(mode));
	}

	public void addGeoLine(GeoConstants mode, Double numVal) {

		if (GeoConstants.DISPLAY_AZIMUTH.equals(mode)) {
			int az = new Double(Utils.round(numVal, 0)).intValue();
			az = (az < 0) ? az += 360 : az;
			addLine(az + " °");
		}

		if (GeoConstants.DISPLAY_RANGE.equals(mode)) {
			addGeoLine(mode, new Double[] { numVal });
		}

		else {
			throw new IllegalArgumentException("Unknown mode: " + mode);
		}

	}

	public void addGeoLine(GeoConstants mode, Double[] numVals) {

		if (GeoConstants.DISPLAY_RANGE.equals(mode)) {

			if (numVals.length < 1)
				throw new IllegalArgumentException();

			// distance partielle ou totale si seule
			String line = numVals[0] < 1000 ? Math.round(numVals[0]) + " m"
					: Utils.round(numVals[0] / 1000d, 2) + " km";

			// distance totale
			if (numVals.length > 1) {
				String s2 = numVals[1] < 1000 ? Math.round(numVals[1]) + " m"
						: Utils.round(numVals[1] / 1000d, 2) + " km";
				line += " / " + s2;
			}

			addLine(line);
		}

		else {
			throw new IllegalArgumentException("Unknown mode: " + mode);
		}

	}

	public void setTextSize(int s) {

		if (s < MIN_TEXT_SIZE)
			s = MIN_TEXT_SIZE;

		textSize = s;
	}

	@Override
	public void refreshShape() {

		// font d'ecriture de l'etiquette
		this.font = new Font(Font.DIALOG, Font.BOLD, textSize);

		// flag de recalcul au prochain dessin des dimensions
		this.recomputeOnNextPaint = true;
	}

	@Override
	public Rectangle getMaximumBounds() {
		return bounds;
	}

	/**
	 * Attribuer une position fixe
	 * 
	 * @param position
	 */
	public void setPosition(Point position) {
		this.bounds.setLocation(new Point(position));
	}

	public void setAnchor(Point anchor, String mode, int margin) {

		if (margin < MIN_TEXT_SIZE)
			margin = MIN_TEXT_SIZE;

		this.anchor = anchor;
		this.anchorMode = mode;
		this.anchorMargin = margin;
	}

	public void setDrawAnchor(boolean drawAnchor) {
		this.drawAnchor = drawAnchor;
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public void setStrokeWidth(int width) {
		this.stroke = new BasicStroke(width);
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public void setColors(Color text, Color border, Color background) {
		setTextColor(text);
		setBorderColor(border);
		setBackgroundColor(background);
	}

	public void clearText() {
		lines.clear();
	}

}
