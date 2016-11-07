package abcmap.draw.shapes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.List;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.draw.styles.BackgroundRenderer;
import abcmap.geo.GeoInfoMode;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.gui.GuiUtils;

public class Polygon extends PolypointShape {

	public Polygon() {
		super();
		refreshShape();
	}

	public Polygon(Polygon poly) {
		this();
		setProperties(poly.getProperties());
		refreshShape();
	}

	@Override
	public boolean equals(Object obj) {
		return testEquality(obj, this.getClass());
	}

	@Override
	public void refreshShape() {

		if (points.size() < 1)
			return;

		// calculs des segments et de la zone d'interaction

		// reinitialisation
		interactionArea = new Area();
		segmentsArea.clear();
		polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
		handles.clear();

		// commencer au premier point
		Point first = points.get(0);
		polyline.moveTo(first.x, first.y);

		Point nextPoint;

		int i = 1;
		for (Point point : points) {

			// recupere le point suivant
			if (i < points.size()) {
				nextPoint = points.get(i);
			} else {
				nextPoint = null;
			}

			if (nextPoint != null) {
				Area a = computeInteractionAreaFor(point, nextPoint);
				segmentsArea.add(a);
				interactionArea.add(a);
				polyline.lineTo(nextPoint.x, nextPoint.y);
			}

			// placement des poignees
			Handle h = new Handle(Handle.FOR_RESIZING);
			h.setPosition(point);
			handles.add(h);

			i++;
		}

		refreshHandles();

		// fermer la forme si necessaire
		if (shapeClosed == true) {
			polyline.closePath();
			Area a = computeInteractionAreaFor(points.get(points.size() - 1), points.get(0));
			segmentsArea.add(a);
			interactionArea.add(a);
		}

		// ajouter la zone d'interaction du fond
		if (stroke.getBgColor() != null) {
			interactionArea.add(new Area(polyline));
		}

		// calcul des dimensions maximum
		int ht = stroke.getHalfThickness();
		Rectangle r = polyline.getBounds();
		maximumBounds.x = r.x - ht;
		maximumBounds.y = r.y - ht;
		maximumBounds.width = r.width - ht;
		maximumBounds.height = r.height - ht;

		// calcul des informations géographiques
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			computeGeoInformations();
		}

	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// peindre l'interieur
		if (shapeClosed == true && stroke.getBgColor() != null) {
			BackgroundRenderer.fill(g, polyline, stroke);
		}

		// peindre l'exterieur
		g.setColor(stroke.getFgColor());
		g.setStroke(stroke.getSwingStroke());
		g.draw(polyline);

		// dessin des informations géographiques
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			drawGeoInformations(g);
		}

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// selection
		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode) && isSelected() == true) {

			g.setColor(drawm.getSelectionColor());
			g.setStroke(drawm.getSelectionStroke());
			g.draw(polyline);

			// dessin des poignees
			drawHandles(g);

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());

	}

	@Override
	public LayerElement duplicate() {
		return new Polygon(this);
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		this.stroke = DrawProperties.createNewWith(pp.stroke);
		this.points = pp.points;
		this.shapeClosed = pp.polyshapeClosed;
		this.informationMode = new GeoInfoMode(pp.geoInfoMode);
		this.infoTextSize = pp.geoInfoSize;

		// lien
		setLinkRessource(pp.linkRessource);

	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();

		pp.polyshapeClosed = shapeClosed;
		pp.geoInfoMode = informationMode.toString();
		pp.geoInfoSize = getGeoTextSize();

		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();
		pp.points = Polygon.deepCopyPoints(this);

		// lien
		pp.linkRessource = linkRessource;

		return pp;
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Polygon sample = (Polygon) this.duplicate();

		// adapter la forme
		sample.clearPoints();
		int t = stroke.getHalfThickness();

		List<Point> newPoints = Arrays.asList(new Point(t, t), // ULC
				new Point(maxWidth - t, maxHeight - t), // BRC
				new Point(maxWidth - t, 0), // URC
				new Point(0, maxHeight) // BLC
		);

		sample.points.addAll(newPoints);
		sample.closeShape();

		// valider leschangements
		sample.refreshShape();

		return sample;
	}

}
