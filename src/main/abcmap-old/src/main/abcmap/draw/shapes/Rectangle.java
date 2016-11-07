package abcmap.draw.shapes;

import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.draw.styles.BackgroundRenderer;
import abcmap.geo.GeoInfoMode;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.gui.GuiUtils;

public class Rectangle extends RectangleShape {

	public Rectangle() {
		refreshShape();
	}

	public Rectangle(Rectangle rect) {
		this();
		setProperties(rect.getProperties());
		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Rectangle == false)
			return false;

		Rectangle shp = (Rectangle) obj;
		Object[] toCompare1 = new Object[] { this.bounds, this.stroke, this.selected, };
		Object[] toCompare2 = new Object[] { shp.bounds, shp.stroke, shp.selected };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	@Override
	public void refreshShape() {

		// traits pour calculs
		boolean borderIsActived = stroke.getFgColor() != null;
		int hth = borderIsActived ? stroke.getHalfThickness() : 0;
		int thickness = borderIsActived ? stroke.getThickness() : 0;

		// position de la forme
		bounds.x = position.x + hth;
		bounds.y = position.y + hth;

		// taille maximum de la forme
		maximumBounds.x = bounds.x - hth;
		maximumBounds.y = bounds.y - hth;
		maximumBounds.width = bounds.width + hth * 2;
		maximumBounds.height = bounds.height + hth * 2;

		// calculer l'aire d'interaction
		int margin = drawm.getInteractionAreaMargin() + thickness;
		int halfMargin = Math.round(margin / 2);

		// a partir du rectangle exterieur
		Rectangle2D extEll = new Rectangle2D.Double(bounds.x - halfMargin, bounds.y - halfMargin,
				bounds.width + margin, bounds.height + margin);

		// et interieur
		Rectangle2D intEll = new Rectangle2D.Double(bounds.x + halfMargin, bounds.y + halfMargin,
				bounds.width - margin, bounds.height - margin);

		// creer la zone et retirer le fond si necessaire
		this.interactionArea = new Area(extEll);
		if (stroke.getBgColor() == null) {
			this.interactionArea.subtract(new Area(intEll));
		}

		// ajuster les poignees
		adjustHandlesToBounds();

		// ajuster les etiquettes geographiques
		updateGeoLabels();
	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// dessiner le fond
		if (stroke.getBgColor() != null) {
			BackgroundRenderer.fill(g, bounds, stroke);
		}

		// dessiner la bordure
		if (stroke.getFgColor() != null) {
			g.setColor(stroke.getFgColor());
			g.setStroke(stroke.getSwingStroke());
			g.draw(bounds);
		}

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// dessin des etiquettes geo
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			drawGeoLabels(g);
		}

		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode) && isSelected() == true) {

			// dessin du cadre autour
			g.setColor(drawm.getSelectionColor());
			g.setStroke(drawm.getSelectionStroke());
			g.draw(bounds);

			// dessin des poignées
			drawHandles(g);

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());

	}

	@Override
	public LayerElement duplicate() {
		return new Rectangle(this);
	}

	@Override
	public void setProperties(PropertiesContainer properties) {
		ShapeProperties pp = (ShapeProperties) properties;
		this.setPosition(pp.position);
		this.setDimensions(pp.dimensions);
		this.stroke = DrawProperties.createNewWith(pp.stroke);
		setLinkRessource(pp.linkRessource);

	}

	@Override
	public PropertiesContainer getProperties() {
		ShapeProperties pp = new ShapeProperties();
		pp.dimensions = maximumBounds.getSize();
		pp.position = maximumBounds.getLocation();
		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();
		pp.linkRessource = linkRessource;

		return pp;
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Rectangle sample = (Rectangle) this.duplicate();

		// dimensionner
		int t = stroke.getThickness();
		sample.setDimensions(maxWidth - t, maxHeight - t);

		// valider leschangements
		sample.refreshShape();

		return sample;
	}

}
