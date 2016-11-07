package abcmap.draw.basicshapes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;

import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.geo.GeoInfoMode;
import abcmap.gui.comps.geo.HasGeoInformations;
import abcmap.utils.Utils;

public abstract class PolypointShape extends LayerElement implements HasGeoInformations {

	public static final int MIN_ARROW_FORCE = 10;

	protected GeneralPath polyline;
	protected boolean shapeClosed;
	protected ArrayList<Point> points;

	protected boolean isBeginWithArrow;
	protected boolean isEndWithArrow;
	protected int arrowForce;

	protected int infoTextSize;
	protected GeoInfoMode informationMode;
	protected ArrayList<Area> segmentsArea;

	protected Font font;
	private ArrayList<InfoLabel> infoLabels;

	public PolypointShape() {

		// les points de la forme
		this.points = new ArrayList<Point>(20);

		// les poignées
		this.handles = new ArrayList<Handle>(20);

		// les segments d'aire d'interaction
		this.segmentsArea = new ArrayList<Area>(20);

		// les informations geographiques par defaut
		this.informationMode = new GeoInfoMode();
		setGeoTextSize(15);

		// taille de la fleche par defaut
		setArrowForce(15);

		// les etiquettes d'info geographiques
		this.infoLabels = new ArrayList<InfoLabel>();

		// forme fermée pour les polygone
		this.shapeClosed = false;

		refreshShape();

	}

	@Override
	public abstract boolean equals(Object obj);

	protected boolean testEquality(Object obj, Class shapeClass) {

		if (shapeClass.isInstance(obj) == false)
			return false;

		PolypointShape shp = (PolypointShape) obj;

		// proprietes
		Object[] toCompare1 = new Object[] { this.selected, this.stroke, this.shapeClosed,
				this.informationMode, this.infoTextSize, };

		Object[] toCompare2 = new Object[] { shp.selected, shp.stroke, shp.shapeClosed,
				shp.informationMode, shp.infoTextSize, };

		boolean b1 = Arrays.deepEquals(toCompare1, toCompare2);
		if (b1 == false)
			return false;

		// points
		boolean b2 = Utils.safeEquals(this.points, shp.points);
		return b2;

	}

	@Override
	public void setPosition(Point p) {

		// position actuelle
		Point actual = getPosition();

		// calculer le deplacement et mettre a jour les points
		int mX = p.x - actual.x;
		int mY = p.y - actual.y;

		for (Point p2 : points) {
			p2.translate(mX, mY);
		}

		position = p;

	}

	/**
	 * Calcule une zone d'interaction pour un segement, en fonction de son
	 * orientation.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	protected Area computeInteractionAreaFor(Point p1, Point p2) {

		// marge d'interaction
		int m = drawm.getInteractionAreaMargin() + stroke.getThickness();

		// les 4 angles du polygone
		Point[] p = new Point[4];

		// ligne orientee nord est
		if (p2.x > p1.x && p2.y < p1.y) {
			p[0] = new Point(p1.x - m, p1.y - m);
			p[1] = new Point(p1.x + m, p1.y + m);
			p[2] = new Point(p2.x + m, p2.y + m);
			p[3] = new Point(p2.x - m, p2.y - m);
		}

		// ligne dirigee sud est
		else if (p2.x > p1.x && p2.y > p1.y) {
			p[0] = new Point(p1.x - m, p1.y + m);
			p[1] = new Point(p1.x + m, p1.y - m);
			p[2] = new Point(p2.x + m, p2.y - m);
			p[3] = new Point(p2.x - m, p2.y + m);
		}

		// ligne dirigee sud ouest
		if (p2.x < p1.x && p2.y > p1.y) {
			p[0] = new Point(p1.x - m, p1.y - m);
			p[1] = new Point(p1.x + m, p1.y + m);
			p[2] = new Point(p2.x + m, p2.y + m);
			p[3] = new Point(p2.x - m, p2.y - m);
		}

		// ligne orientee nord ouest
		else if (p2.x < p1.x && p2.y < p1.y) {
			p[0] = new Point(p1.x - m, p1.y + m);
			p[1] = new Point(p1.x + m, p1.y - m);
			p[2] = new Point(p2.x + m, p2.y - m);
			p[3] = new Point(p2.x - m, p2.y + m);
		}

		// ligne verticale
		else if (p2.y == p1.y) {
			p[0] = new Point(p1.x, p1.y + m);
			p[1] = new Point(p1.x, p1.y - m);
			p[2] = new Point(p2.x, p2.y - m);
			p[3] = new Point(p2.x, p2.y + m);
		}

		// ligne horizontale
		else if (p2.x == p1.x) {
			p[0] = new Point(p1.x + m, p1.y);
			p[1] = new Point(p1.x - m, p1.y);
			p[2] = new Point(p2.x - m, p2.y);
			p[3] = new Point(p2.x + m, p2.y);
		}

		// creation du polygone
		GeneralPath intArea = new GeneralPath(GeneralPath.WIND_EVEN_ODD, p.length);
		intArea.moveTo(p[0].x, p[0].y);
		for (int i = 1; i < p.length; i++) {
			intArea.lineTo(p[i].x, p[i].y);
		}
		intArea.closePath();

		return new Area(intArea);
	}

	/**
	 * Dessiner les informations geographiques
	 * 
	 * @param g
	 */
	protected void drawGeoInformations(Graphics2D g) {
		for (InfoLabel lbl : infoLabels) {
			lbl.draw(g, null);
		}
	}

	/**
	 * Calcule la position des informations géographiques
	 */
	protected void computeGeoInformations() {

		// la font d'écriture
		this.font = new Font(Font.DIALOG, Font.BOLD, infoTextSize);

		// vider les précédents labels
		infoLabels.clear();

		// la distance totale du premier au dernier point
		double distanceTT = 0;

		int i = 1;
		for (Point p : points) {

			// recuperer le precedent point
			Point lastPxPt;
			try {
				lastPxPt = points.get(points.indexOf(p) - 1);
			} catch (IndexOutOfBoundsException e) {
				lastPxPt = null;
			}

			// l'etiquette finale
			InfoLabel lbl = new InfoLabel();

			// les coordonnées du point en cours
			Coordinate co = new Coordinate(p);

			// les coorodonnées du precedent point
			Coordinate co2 = null;

			// azimut distance
			Double[] azDist = new Double[2];

			// le precedent point est indisponible
			// transformation du point actuel seulement
			if (lastPxPt == null) {
				co = new Coordinate(p);
				try {
					mapCtrl.transformCoords(GeoConstants.SCREEN_TO_WORLD, co);
				} catch (MapManagerException e) {
					co = null;
					co2 = null;
				}
			}

			// les deux points sont disponibles
			// transformation des deux points + calcul de l'azimut / distance
			else {

				co2 = new Coordinate(lastPxPt);
				try {
					mapCtrl.transformCoords(GeoConstants.SCREEN_TO_WORLD, co);
					mapCtrl.transformCoords(GeoConstants.SCREEN_TO_WORLD, co2);
					azDist = mapCtrl.azimuthDistance(co2, co);
				} catch (MapManagerException e) {
					co = null;
					co2 = null;
				}
			}

			// ajouter le nom du point
			lbl.addLine("Point " + i);

			lbl.setDrawAnchor(informationMode.isDrawAnchor());

			// coordonnées disponible, affichage
			if (co != null) {

				// degres decimaux
				if (informationMode.isPosDD()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_DEC, co);
				}

				// degres minutes
				if (informationMode.isPosDMD()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC, co);
				}

				// degres minutes secondes
				if (informationMode.isPosDMS()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC, co);
				}

			}
			if (co2 != null) {
				// azimut
				if (informationMode.isAzimuth()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_AZIMUTH, azDist[0]);
				}

				// distance
				if (informationMode.isRange()) {
					distanceTT += azDist[1];
					lbl.addGeoLine(GeoConstants.DISPLAY_RANGE,
							new Double[] { azDist[1], distanceTT });
				}
			}

			// afficher un message d'erreur sauf pour la premiere pos
			if (co2 == null && i != 1) {
				lbl.addLine("Erreur de géoréférencement");
			}

			// position de l'etiquette
			lbl.setAnchor(p, InfoLabel.SOUTH_WEST, stroke.getThickness());

			// couleur et traits
			lbl.setTextSize(infoTextSize);
			lbl.setColors(stroke.getFgColor(), stroke.getFgColor(), stroke.getBgColor());

			// rafraichir et ajouter
			lbl.refreshShape();
			infoLabels.add(lbl);

			i++;

		}

	}

	@Override
	public Rectangle getMaximumBounds() {
		float t = ((float) stroke.getThickness()) / 2f;
		Rectangle r = new Rectangle(polyline.getBounds());

		r.x -= t;
		r.y -= t;
		r.width += t;
		r.height += t;
		return r;
	}

	public void addPoint(Point p) {
		points.add(p);
		refreshShape();
	}

	public void addPointAtPosition(int index, Point p) {
		points.add(index, p);
		refreshShape();
	}

	public void removePoint(Point p) {
		points.remove(p);
		refreshShape();
	}

	public void removePoint(int index) {
		points.remove(index);
		refreshShape();
	}

	public ArrayList<Point> getPoints() {
		return new ArrayList<Point>(points);
	}

	public void clearPoints() {
		points.clear();
	}

	public ArrayList<Area> getSegmentsArea() {
		return new ArrayList<Area>(segmentsArea);
	}

	public void setBeginWithArrow(boolean beginWithArrow) {
		this.isBeginWithArrow = beginWithArrow;
	}

	public boolean isBeginWithArrow() {
		return isBeginWithArrow;
	}

	public void setEndWithArrow(boolean endWithArrow) {
		this.isEndWithArrow = endWithArrow;
	}

	public boolean isEndWithArrow() {
		return isEndWithArrow;
	}

	public void setArrowForce(int af) {

		if (af < MIN_ARROW_FORCE)
			af = MIN_ARROW_FORCE;

		this.arrowForce = af;
	}

	public int getArrowForce() {
		return arrowForce;
	}

	public void closeShape() {
		this.shapeClosed = true;
	}

	public static ArrayList<Point> deepCopyPoints(PolypointShape poly) {
		ArrayList<Point> copy = new ArrayList<Point>();
		for (Point p : poly.getPoints()) {
			copy.add(new Point(p));
		}
		return copy;
	}

	@Override
	public GeoInfoMode getGeoInfoMode() {
		return informationMode;
	}

	@Override
	public void setGeoInfoMode(GeoInfoMode m) {
		informationMode = new GeoInfoMode(m);
	}

	@Override
	public int getGeoTextSize() {
		return infoTextSize;
	}

	@Override
	public void setGeoTextSize(int s) {

		if (s < GeoInfoMode.MIN_TEXT_SIZE)
			s = GeoInfoMode.MIN_TEXT_SIZE;

		infoTextSize = s;
	}

}