package abcmap.draw.legend;

import java.awt.Point;
import java.awt.Rectangle;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Polygon;
import abcmap.draw.shapes.Symbol;
import abcmap.managers.Log;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.Utils;

public class LegendSample {

	/** Etape à diminuer pour arriver à la taille desiree d'un element */
	private static final int SIZE_MINUS_STEP = 3;

	/** Taille minimale des echantillongs */
	private static final int SAMPLE_MINIMUM_SIZE = 10;

	private Class<? extends LayerElement> shapeClass;
	private DrawProperties fgStroke;
	private ShapeProperties shapeProperties;

	public LegendSample(LayerElement elmt) {

		this.shapeClass = elmt.getClass();
		this.shapeProperties = (ShapeProperties) elmt.getProperties();

		this.fgStroke = elmt.getStroke();

	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof LegendSample)
			return false;

		LegendSample ls = (LegendSample) obj;

		Object[] tab1 = new Object[] { ls.shapeClass, ls.fgStroke, shapeProperties };
		Object[] tab2 = new Object[] { this.shapeClass, this.fgStroke, this.shapeProperties };

		for (int i = 0; i < tab1.length; i++) {
			if (Utils.safeEquals(tab1[i], tab2[i]) == false) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retourne une instance de cet échantillon
	 * 
	 * @return
	 */
	public LayerElement getInstance(Rectangle bounds) {

		// creer l'element
		LayerElement elmt;
		try {
			elmt = shapeClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Log.error(e);
			return null;
		}

		// ajuster pour creer un echantillon
		adjustElementToSample(elmt, bounds);

		return elmt;
	}

	private void adjustElementToSample(LayerElement elmt, Rectangle bounds) {

		// affecter les proprietes graphiques
		elmt.setStroke(new DrawProperties(fgStroke));

		if (elmt instanceof Label) {

			Label label = (Label) elmt;

			// chercher la taille optimale
			int size = (int) (bounds.height / 3d * 2d);

			do {
				label.setFontSize(size);
				label.refreshShape();

				if (size <= SAMPLE_MINIMUM_SIZE)
					break;

				size -= SIZE_MINUS_STEP;

			} while (bounds.contains(label.getMaximumBounds()) == false);
		}

		else if (elmt instanceof PolypointShape) {
			Polygon poly = (Polygon) elmt;
			poly.addPoint(new Point(bounds.x, bounds.y));
			poly.addPoint(new Point(bounds.width, bounds.height));
			poly.addPoint(new Point(bounds.x, bounds.height));
			poly.addPoint(new Point(bounds.width, bounds.y));

			if (elmt instanceof Polygon)
				poly.closeShape();
		}

		else if (elmt instanceof Symbol) {
			Symbol symbol = (Symbol) elmt;
			symbol.setSymbolSetName(shapeProperties.symbolSetName);
			symbol.setSymbolCode(shapeProperties.symbolCode);

			// chercher la taille optimale
			int size = (int) (bounds.height / 3d * 2d);

			do {
				symbol.setSize(size);
				symbol.refreshShape();

				if (size <= SAMPLE_MINIMUM_SIZE)
					break;

				size -= SIZE_MINUS_STEP;

			} while (bounds.contains(symbol.getMaximumBounds()) == false);

		}

		else if (elmt instanceof RectangleShape) {
			RectangleShape shp = (RectangleShape) elmt;
			shp.getRectangle().setSize(bounds.getSize());
		}

		else {
			throw new IllegalStateException("Unable to represent: " + elmt.getClass().getName());
		}

		// positionner
		elmt.setPosition(bounds.getLocation());

		// rafraichir l'element
		elmt.refreshShape();

	}

}
