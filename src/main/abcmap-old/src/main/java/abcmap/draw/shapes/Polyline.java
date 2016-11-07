package abcmap.draw.shapes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.List;

import abcmap.draw.basicshapes.ArrowHead;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.geo.GeoInfoMode;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.gui.GuiUtils;

public class Polyline extends PolypointShape {

	private ArrowHead beginArrow;
	private ArrowHead endArrow;

	public Polyline() {
		refreshShape();
	}

	public Polyline(Polyline poly) {
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

		// pas de points, retour
		if (points.size() < 1)
			return;

		// recalculer la zone d'interaction
		interactionArea = new Area();
		segmentsArea.clear();

		// recréer le path de la ligne
		polyline = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());

		// effacer les poignées
		handles.clear();

		// initialisation de la création des segments
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

		// calcul des informations geographiques
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			computeGeoInformations();
		}

		// fleche de début
		if (isBeginWithArrow && points.size() > 1) {

			// creer si absente
			if (beginArrow == null)
				beginArrow = new ArrowHead();

			beginArrow.setStartPoint(points.get(1));
			beginArrow.setEndPoint(points.get(0));
			beginArrow.setWidth(stroke.getThickness() + arrowForce);
			beginArrow.refreshShape();

		}

		// fleche de fin
		if (isEndWithArrow && points.size() > 1) {

			// creer si absente
			if (endArrow == null)
				endArrow = new ArrowHead();

			endArrow.setStartPoint(points.get(points.size() - 2));
			endArrow.setEndPoint(points.get(points.size() - 1));
			endArrow.setWidth(stroke.getThickness() + arrowForce);
			endArrow.refreshShape();

		}

		// calcul des dimensions maximum
		// TODO: prendre en compte les fleches ???
		int ht = stroke.getHalfThickness();
		Rectangle r = polyline.getBounds();
		maximumBounds.x = r.x - ht;
		maximumBounds.y = r.y - ht;
		maximumBounds.width = r.width - ht;
		maximumBounds.height = r.height - ht;

	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// polyligne
		g.setColor(stroke.getFgColor());
		g.setStroke(stroke.getSwingStroke());
		g.draw(polyline);

		// dessin des fleches
		if (isBeginWithArrow && points.size() > 1) {
			beginArrow.draw(g, null);
		}
		if (isEndWithArrow && points.size() > 1) {
			endArrow.draw(g, null);
		}

		// dessin des informations géographiques
		if (GeoInfoMode.isInformationModeNotEmpty(informationMode)) {
			drawGeoInformations(g);
		}

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// forme sélectionnée
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
		return new Polyline(this);
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		this.isBeginWithArrow = pp.beginWithArrow;
		this.isEndWithArrow = pp.endWithArrow;

		this.stroke = DrawProperties.createNewWith(pp.stroke);
		this.points = pp.points;
		this.informationMode = new GeoInfoMode(pp.geoInfoMode);
		this.infoTextSize = pp.geoInfoSize;

		// lien
		setLinkRessource(pp.linkRessource);

	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();

		pp.geoInfoMode = informationMode.toString();
		pp.geoInfoSize = getGeoTextSize();
		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();
		pp.points = Polygon.deepCopyPoints(this);

		pp.beginWithArrow = isBeginWithArrow();
		pp.endWithArrow = isEndWithArrow();
		pp.arrowForce = getArrowForce();

		// lien
		pp.linkRessource = linkRessource;

		return pp;
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Polyline sample = (Polyline) this.duplicate();

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

		// valider les changements
		sample.refreshShape();

		return sample;
	}

}
