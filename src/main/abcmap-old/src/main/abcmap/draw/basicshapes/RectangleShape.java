package abcmap.draw.basicshapes;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.geo.GeoInfoMode;
import abcmap.gui.comps.geo.HasGeoInformations;

public abstract class RectangleShape extends LayerElement implements HasGeoInformations {

	public static final Integer MIDDLE_HANDLE_INDEX = 0;
	public static final Integer ULC_HANDLE_INDEX = 1;
	public static final Integer BRC_HANDLE_INDEX = 2;

	public static final Integer ULC_GEOLABEL = 0;
	public static final Integer URC_GEOLABEL = 1;
	public static final Integer BLC_GEOLABEL = 2;
	public static final Integer BRC_GEOLABEL = 3;

	/** La forme à dessiner: rectangle, ellipse,... */
	protected Rectangle bounds;
	protected GeoInfoMode informationMode;
	protected int infoTextSize;
	protected ArrayList<InfoLabel> geoLabels;

	public RectangleShape() {

		this.handles.add(new Handle(Handle.FOR_MOVING));
		this.handles.add(new Handle(Handle.FOR_RESIZING));
		this.handles.add(new Handle(Handle.FOR_RESIZING));

		this.bounds = new Rectangle();

		this.informationMode = new GeoInfoMode();
		this.infoTextSize = 15;

		this.geoLabels = new ArrayList<>();
		geoLabels.add(new InfoLabel());
		geoLabels.add(new InfoLabel());
		geoLabels.add(new InfoLabel());
		geoLabels.add(new InfoLabel());

	}

	/**
	 * Retourne le rectangle de base, sans prise en compte du trait ou d'autres
	 * éléments comme getBounds()
	 * 
	 * @return
	 */
	public Rectangle getRectangle() {
		return new Rectangle(bounds);
	}

	/**
	 * Affecte les dimensions du rectangle interne,
	 * 
	 * @param x
	 * @param y
	 */
	public void setDimensions(Dimension dim) {
		setDimensions(dim.width, dim.height);
	}

	/**
	 * Retourne les dimensions du rectangle interne,
	 * 
	 * @return
	 */
	public Dimension getDimensions() {
		return bounds.getSize();
	}

	/**
	 * Affecte la position et les dimensions du rectangle interne,
	 * 
	 * @param x
	 * @param y
	 */
	public void setRectangle(Rectangle rectangle) {
		this.bounds = rectangle;
	}

	/**
	 * Affecte les dimensions du rectangle interne,
	 * 
	 * @param x
	 * @param y
	 */
	public void setDimensions(int x, int y) {
		bounds.setSize(x, y);
	}

	@Override
	public GeoInfoMode getGeoInfoMode() {
		return informationMode;
	}

	@Override
	public void setGeoInfoMode(GeoInfoMode m) {
		informationMode = m;
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

	protected void updateGeoLabels() {

		Point[] points = getGeoLabelsAnchors();
		String[] anchorModes = getGeoLabelsAnchorModes();
		GeoInfoMode infos = new GeoInfoMode(informationMode);

		// rafraichir les positions
		for (int i = 0; i < geoLabels.size(); i++) {

			// recuperer l'etiquette
			InfoLabel lbl = geoLabels.get(i);
			lbl.clearText();

			// caracteristiques d'affichage
			lbl.setTextSize(infoTextSize);
			lbl.setColors(stroke.getFgColor(), stroke.getFgColor(), stroke.getBgColor());

			// position de l'etiquette
			Point p = points[i];
			lbl.setAnchor(p, anchorModes[i], stroke.getThickness());
			lbl.setDrawAnchor(infos.isDrawAnchor());

			// recuperer les coordonnées reelle
			Coordinate co = new Coordinate(p);
			try {
				mapCtrl.transformCoords(GeoConstants.SCREEN_TO_WORLD, co);
			} catch (MapManagerException e) {
				lbl.addLine("Erreur de géo-référencement");
				co = null;
			}

			// coordonnées disponible, affichage
			if (co != null) {

				// degres decimaux
				if (infos.isPosDD()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_DEC, co);
				}

				// degres minutes
				if (infos.isPosDMD()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_MINUTES_DEC, co);
				}

				// degres minutes secondes
				if (infos.isPosDMS()) {
					lbl.addGeoLine(GeoConstants.DISPLAY_DEGREES_MINUTES_SEC, co);
				}

			}

			lbl.refreshShape();
		}

	}

	protected void drawGeoLabels(Graphics2D g) {
		for (InfoLabel lbl : geoLabels) {
			lbl.draw(g, null);
		}
	}

	protected Point[] getGeoLabelsAnchors() {
		return new Point[] { new Point(bounds.x, bounds.y),
				new Point(bounds.x + bounds.width, bounds.y),
				new Point(bounds.x + bounds.width, bounds.y + bounds.height),
				new Point(bounds.x, bounds.y + bounds.height), };
	}

	protected String[] getGeoLabelsAnchorModes() {
		return new String[] { InfoLabel.NORTH_WEST, InfoLabel.NORTH_EAST, InfoLabel.SOUTH_EAST,
				InfoLabel.SOUTH_WEST, };
	}

	protected void adjustHandlesToBounds() {

		handles.get(RectangleShape.MIDDLE_HANDLE_INDEX).setPosition((int) bounds.getCenterX(),
				(int) bounds.getCenterY());

		handles.get(RectangleShape.ULC_HANDLE_INDEX).setPosition(new Point(bounds.x, bounds.y));

		handles.get(RectangleShape.BRC_HANDLE_INDEX)
				.setPosition(new Point(bounds.x + bounds.width, bounds.y + bounds.height));

		refreshHandles();
	}

}
