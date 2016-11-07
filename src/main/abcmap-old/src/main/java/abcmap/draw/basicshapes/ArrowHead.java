package abcmap.draw.basicshapes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import abcmap.utils.gui.GuiUtils;

public class ArrowHead implements Drawable {

	private static final int MIN_WIDTH = 10;

	private Dimension dimensions;
	private float yfactor;
	private Point endPoint;
	private Point startPoint;
	private double angle;
	private float dimFactor;
	private Polygon polygon;

	private boolean debugMode;

	public ArrowHead() {

		// utilitaires
		debugMode = false;

		this.startPoint = new Point();
		this.endPoint = new Point();

		// forme en y en pourcentage de la hauteur
		this.yfactor = 0.25f;

		// hauteur en pourcentage de largeur, utilisé pour calculs
		this.dimFactor = 1.3f;

		// dimensions
		setWidth(50);

		refreshShape();
	}

	@Override
	public void refreshShape() {

		// calculer la direction de la fleche pour modification des graphics
		double dx = endPoint.x - startPoint.x;
		double dy = endPoint.y - startPoint.y;
		angle = Math.atan2(dy, dx);

		/*
		 * Positions des points de la tete de fleche.
		 * 
		 * Le dessin se fait à partir du point (0,0),
		 * 
		 * Axe x positif dans le prolongement de la fleche,
		 * 
		 * Axe y positif vers la droite.
		 * 
		 */

		int[] xPoints = new int[] { 0, -(int) (dimensions.height * yfactor),
				(int) (dimensions.height - dimensions.height * yfactor),
				-(int) (dimensions.height * yfactor), };
		int[] yPoints = new int[] { 0, -dimensions.width / 2, 0, dimensions.width / 2, };

		// polygone de la fleche
		polygon = new Polygon(xPoints, yPoints, xPoints.length);

	}

	@Override
	public void draw(Graphics2D g, String mode) {

		// graphics speciaux pour la tete
		Graphics2D gr = (Graphics2D) g.create();

		// graphics à la base de la fleche
		AffineTransform t = AffineTransform.getTranslateInstance(endPoint.x, endPoint.y);

		// graphics dans le orientés dans la direction de la fleche
		t.concatenate(AffineTransform.getRotateInstance(angle));

		// transformation et dessin
		gr.transform(t);
		gr.fill(polygon);

		if (debugMode) {
			// dessiner lignes index
			GuiUtils.drawGraphicsAxesLines(gr);

			g.setColor(Color.green);
			g.setPaint(Color.green);

			// dessins des points pour debug
			int hd = 5;
			g.drawLine(startPoint.x, startPoint.x, endPoint.x, endPoint.x);
			g.drawOval(startPoint.x - hd, startPoint.y - hd, hd * 2, hd * 2);
			g.fillOval(endPoint.x - hd, endPoint.y - hd, hd * 2, hd * 2);
		}
	}

	public void setWidth(int width) {

		if (width < MIN_WIDTH)
			width = MIN_WIDTH;

		int height = (int) (width * dimFactor);
		this.dimensions = new Dimension(width, height);
	}

	public void setDimensions(Dimension dimensions) {
		this.dimensions = new Dimension(dimensions);
	}

	/**
	 * Le point d'arrivée de la ligne ou sera dessinée la tête de flêche. La
	 * tête de flêche est déssinée au point d'arrivée.
	 * 
	 * @param startPoint
	 */
	public void setEndPoint(Point endPoint) {
		this.endPoint = new Point(endPoint);
	}

	/**
	 * Le point de depart de la ligne ou sera dessinée la tête de flêche. La
	 * tête de flêche est déssinée au point d'arrivée.
	 * 
	 * @param startPoint
	 */
	public void setStartPoint(Point startPoint) {
		this.startPoint = new Point(startPoint);
	}

	@Override
	public Rectangle getMaximumBounds() {
		return polygon.getBounds();
	}

}
