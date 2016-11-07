package abcmap.draw.shapes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.InfoLabel;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.draw.styles.BackgroundRenderer;
import abcmap.geo.GeoInfoMode;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.gui.GuiUtils;

public class Ellipse extends RectangleShape {

	public Ellipse() {
		super();
		refreshShape();
	}

	public Ellipse(Ellipse ell) {
		this();
		setProperties(ell.getProperties());
		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Ellipse == false)
			return false;

		Ellipse shp = (Ellipse) obj;

		Object[] toCompare1 = new Object[] { this.maximumBounds, this.bounds, this.stroke,
				this.selected, };
		Object[] toCompare2 = new Object[] { shp.maximumBounds, this.bounds, shp.stroke,
				shp.selected };

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
		Ellipse2D extEll = new Ellipse2D.Double(bounds.x - halfMargin, bounds.y - halfMargin,
				bounds.width + margin, bounds.height + margin);

		// et interieur
		Ellipse2D intEll = new Ellipse2D.Double(bounds.x + halfMargin, bounds.y + halfMargin,
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
	protected Point[] getGeoLabelsAnchors() {
		return new Point[] { new Point(bounds.x + bounds.width / 2, bounds.y),
				new Point(bounds.x + bounds.width, bounds.y + bounds.height / 2),
				new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height),
				new Point(bounds.x, bounds.y + bounds.height / 2), };
	}

	@Override
	protected String[] getGeoLabelsAnchorModes() {
		return new String[] { InfoLabel.NORTH, InfoLabel.EAST, InfoLabel.SOUTH, InfoLabel.WEST, };
	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// dessiner le fond
		if (stroke.getBgColor() != null) {
			BackgroundRenderer.fillOval(g, stroke.getTexture(), stroke.getBgColor(), bounds.x,
					bounds.y, bounds.width, bounds.height);
		}

		// dessiner le cercle
		g.setColor(stroke.getFgColor());
		g.setStroke(stroke.getSwingStroke());
		g.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// dessin des etiquettes geo
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			drawGeoLabels(g);
		}

		// dessin des attributs de selection
		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode) && isSelected() == true) {

			// dessin du cadre autour
			g.setColor(drawm.getSelectionColor());
			g.setStroke(drawm.getSelectionStroke());
			g.draw(bounds);

			// dessin des poignees
			drawHandles(g);

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;
		setDimensions(pp.dimensions);
		setPosition(pp.position);
		setStroke(DrawProperties.createNewWith(pp.stroke));
		setLinkRessource(pp.linkRessource);
	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();

		pp.dimensions = bounds.getSize();
		pp.position = getPosition();
		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();
		pp.linkRessource = linkRessource;

		return pp;
	}

	@Override
	public LayerElement duplicate() {
		return new Ellipse(this);
	}

	/**
	 * 0: la poignée du milieu <br>
	 * 1: ULC <br>
	 * 2: BRC
	 */
	@Override
	public ArrayList<Handle> getHandles() {
		return super.getHandles();
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Ellipse sample = (Ellipse) this.duplicate();

		// dimensionner
		int t = stroke.getThickness();
		sample.setDimensions(maxWidth - t, maxHeight - t);

		// valider leschangements
		sample.refreshShape();

		return sample;
	}

}
