package abcmap.draw.shapes;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextAttribute;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.styles.BackgroundRenderer;
import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.geo.GeoInfoMode;
import abcmap.gui.comps.geo.HasGeoInformations;
import abcmap.managers.stub.MainManager;
import abcmap.project.loaders.AbmConstants;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class Label extends LayerElement implements HasGeoInformations {

	public static final int MAX_TEXT_SIZE = 1000;

	/** Mode d'affichage du texte, libre ou avec coordonnées géographiques */
	private GeoInfoMode geoTextMode;

	private int fontSize;
	private String fontName;
	private Font font;
	private boolean bold;
	private boolean italic;
	private boolean strikethrough;
	private boolean underlined;
	private boolean recomputeTextDimensions = true;

	/** Liste des lignes du texte */
	private ArrayList<String> textLines;

	/** Diametre de dessin du point d'origine */
	private int originPointDiameter;

	/** Marge entre le texte et le cadre */
	private int textMargins;

	private Rectangle bounds;

	private boolean bordersActivated;

	public Label() {

		this.selected = false;
		this.handles.add(new Handle(Handle.FOR_MOVING));
		this.textLines = new ArrayList<String>(10);
		this.fontName = Font.DIALOG;
		this.fontSize = 20;
		this.bold = false;
		this.italic = false;
		this.strikethrough = false;
		this.underlined = false;
		this.geoTextMode = new GeoInfoMode();
		this.textMargins = 5;
		this.bordersActivated = false;

		this.originPointDiameter = 6;

		this.bounds = new Rectangle();

		refreshShape();

	}

	public Label(Label lab) {
		this();

		setProperties(lab.getProperties());
		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Label == false)
			return false;

		Label shp = (Label) obj;

		Object[] toCompare1 = new Object[] { this.bold, this.italic, this.strikethrough,
				this.underlined, this.font, this.fontSize, this.selected, this.stroke };

		Object[] toCompare2 = new Object[] { shp.bold, shp.italic, shp.strikethrough,
				shp.underlined, shp.font, shp.fontSize, shp.selected, this.stroke };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	@Override
	public void refreshShape() {

		// rafraichir la police
		this.font = new Font(fontName, Font.PLAIN, fontSize);
		Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		if (bold)
			map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

		if (italic)
			map.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

		if (strikethrough)
			map.put(TextAttribute.STRIKETHROUGH, true);

		if (underlined)
			map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

		if (stroke.getBgColor() != null)
			map.put(TextAttribute.BACKGROUND, stroke.getBgColor());

		if (stroke.getFgColor() != null)
			map.put(TextAttribute.FOREGROUND, stroke.getFgColor());

		this.font = font.deriveFont(map);

		// Calcul de position en mode géo
		if (GeoInfoMode.isInformationModeNotEmpty(geoTextMode)) {
			setGeoText();
		}

		// placer la poignee en haut a gauche
		handles.get(0).setPosition(getPosition());
		refreshHandles();

		// au prochain dessin recalculer le texte
		recomputeTextDimensions = true;

	}

	/**
	 * Force le recalcul de la forme à partir d'un graphics "écran". Attention,
	 * les données calculées peuvent ne pas être compatibles avec d'autres
	 * périphériques.
	 * 
	 */
	public void forceRefreshShapeForScreen() {
		refreshShape();
		computeTextDimensions((Graphics2D) MainManager.getGuiManager().getMainWindow().getGraphics());
	}

	/**
	 * Calcule les dimensions du texte à l'aide d'un objet graphics
	 * 
	 * @param g
	 */
	private void computeTextDimensions(Graphics2D g) {

		// outil de mesure
		FontMetrics fm = g.getFontMetrics(font);

		// parcourir les lignes pour obtenir les dimensions max
		int w = 0;
		int h = 0;
		for (String line : new ArrayList<String>(textLines)) {

			// largeur
			int nw = fm.stringWidth(line);
			if (w < nw)
				w = nw;

			// hauteur
			h += fm.getHeight();

		}

		// bordure pour calculs
		int halfThickness = bordersActivated ? stroke.getHalfThickness() : 0;
		int thickness = bordersActivated ? stroke.getThickness() : 0;

		// position et taille de la forme
		bounds.x = position.x - halfThickness;
		bounds.y = position.y - halfThickness;
		bounds.width = w + textMargins * 2;
		bounds.height = h + textMargins * 2;

		// taille maximum de la forme
		maximumBounds.x = bounds.x - halfThickness;
		maximumBounds.y = bounds.y - halfThickness;
		maximumBounds.width = bounds.width + thickness;
		maximumBounds.height = bounds.height + thickness;

		// calcul de l'aire d'interation
		int itm = drawm.getInteractionAreaMargin();
		this.interactionArea = new Area(new Rectangle(maximumBounds.x - itm, maximumBounds.y - itm,
				maximumBounds.width + itm * 2, maximumBounds.height + itm * 2));

	}

	private void setGeoText() {

		// convertir la postion de l'etiquette
		boolean exception = false;
		Coordinate co = new Coordinate(this.getPosition());
		try {
			mapCtrl.transformCoords(GeoConstants.SCREEN_TO_WORLD, co);
		} catch (MapManagerException e) {
			exception = true;
		}

		// erreur: affichage
		if (exception) {
			analyseText("Erreur de géoréférencement");
		}

		// affichage des coordonées
		else {

			String text = "";

			// degres decimaux
			if (geoTextMode.isPosDD()) {
				text += co.getStringRepresentation(GeoConstants.DISPLAY_DEGREES_DEC);
			}

			// degres minutes
			if (geoTextMode.isPosDMD()) {
				text += co.getStringRepresentation(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC);
			}

			// degres minutes secondes
			if (geoTextMode.isPosDMS()) {
				text += co.getStringRepresentation(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC);
			}

			analyseText(text);

		}
	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// recalculer les dimensions du texte
		if (recomputeTextDimensions) {
			computeTextDimensions(g);
			recomputeTextDimensions = false;
		}

		// dessin du fond
		if (stroke.getBgColor() != null) {
			BackgroundRenderer.fill(g, bounds, stroke);
		}

		// dessin du cadre
		if (bordersActivated) {
			g.setColor(stroke.getFgColor());
			g.setStroke(stroke.getSwingStroke());
			g.draw(bounds);
		}

		// point si position
		// ne pas se servir de drawAnchor: la position doit être attéchée à un
		// point
		if (GeoInfoMode.isInformationModeNotEmpty(geoTextMode)) {

			// dessiner un premier rond de la couleur opposée
			g.setColor(Utils.getOppositeColor(stroke.getFgColor()));
			g.fillOval(bounds.x - originPointDiameter / 2, bounds.y - originPointDiameter / 2,
					originPointDiameter, originPointDiameter);

			// dessiner un deuxième rond
			g.setColor(stroke.getFgColor());
			g.fillOval(bounds.x - originPointDiameter / 2 + 2,
					bounds.y - originPointDiameter / 2 + 2, originPointDiameter - 4,
					originPointDiameter - 4);
		}

		// dessin du texte
		g.setFont(font);
		int height = g.getFontMetrics().getHeight();
		int x = bounds.x + textMargins;
		int y = bounds.y + height + textMargins / 2;

		for (String line : new ArrayList<String>(textLines)) {
			g.drawString(line, x, y);
			y += height;
		}

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// dessin des attributs de selection
		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode) && isSelected() == true) {

			// dessin du cadre
			g.setColor(drawm.getSelectionColor());
			g.setStroke(drawm.getSelectionStroke());
			g.draw(bounds);

			// dessin des poignées
			drawHandles(g);

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());

	}

	/**
	 * Analyser et affecter le texte en decoupant les lignes.<br>
	 * 
	 * @param t
	 * @return
	 */
	private ArrayList<String> analyseText(String t) {

		// vider le texte actuel
		textLines.clear();

		// analyser le texte
		if (t != null) {

			// réduire le texte si nécéssaire
			if (t.length() > MAX_TEXT_SIZE) {
				t = t.substring(0, MAX_TEXT_SIZE - 3) + "...";
			}

			// scinder les lignes
			for (String l : t.split("( *[\r\n\t]+ *)+")) {
				if (l != null && l.isEmpty() == false) {
					textLines.add(l);
				}
			}
		}

		return textLines;
	}

	public Boolean isBold() {
		return bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public Boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public Boolean isStrikethrough() {
		return strikethrough;
	}

	public void setStrikethrough(boolean strikethrough) {
		this.strikethrough = strikethrough;
	}

	public String getText() {
		String rslt = new String();
		for (String l : textLines) {
			rslt += l + System.lineSeparator();
		}
		return rslt;
	}

	public String getCrossplatformText() {
		String rslt = new String();
		for (String l : textLines) {
			rslt += l + AbmConstants.LABEL_LINE_SEPARATOR;
		}
		return rslt;
	}

	public void setText(String text) {
		analyseText(text);
	}

	public Boolean isUnderlined() {
		return underlined;
	}

	public void setUnderlined(boolean underlined) {
		this.underlined = underlined;
	}

	public void setFontFamily(String font) {
		this.fontName = new String(font);
	}

	public String getFontFamily() {
		return font.getFamily();
	}

	public void setFontSize(int size) {
		this.fontSize = size;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public Font getSwingFont() {
		return font;
	}

	public DrawProperties getTextStroke() {
		return new DrawProperties(stroke);
	}

	@Override
	public Rectangle getMaximumBounds() {

		// prise en compte du trait
		int th = stroke.getThickness();
		float ht = (float) th / 2f;

		int x = Math.round(maximumBounds.x - ht);
		int y = Math.round(maximumBounds.y - ht);
		int w = Math.round(maximumBounds.width + th);
		int h = Math.round(maximumBounds.height + th);

		return new Rectangle(x, y, w, h);
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		this.bordersActivated = pp.borderActivated;
		this.bold = pp.bold;
		this.fontName = pp.font;
		this.italic = pp.italic;
		setPosition(pp.position);
		this.fontSize = pp.size;
		this.strikethrough = pp.strikethrough;
		this.underlined = pp.underlined;
		this.geoTextMode = new GeoInfoMode(pp.geoInfoMode);
		this.stroke = DrawProperties.createNewWith(pp.stroke);

		this.textLines = analyseText(pp.text);

		// lien
		setLinkRessource(pp.linkRessource);

	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();

		pp.borderActivated = bordersActivated;
		pp.bold = bold;
		pp.font = fontName;
		pp.italic = italic;
		pp.position = getPosition();
		pp.size = fontSize;
		pp.strikethrough = strikethrough;
		pp.underlined = underlined;
		pp.geoInfoMode = geoTextMode.toString();
		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();
		pp.text = getCrossplatformText();

		// lien
		pp.linkRessource = linkRessource;

		return pp;
	}

	@Override
	public LayerElement duplicate() {
		return new Label(this);
	}

	@Override
	public GeoInfoMode getGeoInfoMode() {
		return geoTextMode;
	}

	@Override
	public void setGeoInfoMode(GeoInfoMode mode) {
		geoTextMode = mode;
	}

	@Override
	public int getGeoTextSize() {
		return fontSize;
	}

	@Override
	public void setGeoTextSize(int size) {
		// pas d'action ici
	}

	public boolean isBorderActivated() {
		return bordersActivated;
	}

	public void setBorderActivated(boolean activateBorder) {
		this.bordersActivated = activateBorder;
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Label sample = (Label) this.duplicate();

		// adapter la forme
		sample.setText("Ab");
		sample.setFontSize((int) (maxWidth * 0.4));

		// valider les changements
		sample.forceRefreshShapeForScreen();

		return sample;
	}

}
