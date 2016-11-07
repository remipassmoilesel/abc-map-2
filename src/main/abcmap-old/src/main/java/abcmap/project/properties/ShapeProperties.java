package abcmap.project.properties;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import abcmap.draw.links.LinkRessource;

public class ShapeProperties extends PropertiesContainer {

	/*
	 * General
	 */
	public Point position;
	public String sourceFile;
	public Dimension dimensions;
	public int size;

	/**
	 * Proprietes de premier plan. Proprietes courantes.
	 */
	public DrawPropertiesContainer stroke;

	/*
	 * Poly
	 */
	public ArrayList<Point> points;
	public boolean polyshapeClosed;
	public boolean beginWithArrow;
	public boolean endWithArrow;
	public int arrowForce;

	/*
	 * symbols
	 */

	public int symbolCode;
	public String symbolSetName;

	/*
	 * Geo
	 */
	public String geoInfoMode;
	public int geoInfoSize;

	/*
	 * Label
	 */
	public String font;
	public boolean bold;
	public boolean italic;
	public boolean strikethrough;
	public boolean underlined;
	public String text;
	public boolean borderActivated;

	/*
	 * 
	 */
	public LinkRessource linkRessource;

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ShapeProperties == false)
			return false;

		ShapeProperties pp = (ShapeProperties) obj;

		Object[] pps1 = new Object[] { position, sourceFile, dimensions, size, stroke, points,
				polyshapeClosed, geoInfoMode, geoInfoSize, beginWithArrow, endWithArrow, arrowForce,
				symbolCode, symbolSetName, font, bold, italic, strikethrough, underlined,
				geoInfoMode, text, linkRessource, borderActivated };

		Object[] pps2 = new Object[] { pp.position, pp.sourceFile, pp.dimensions, pp.size,
				pp.stroke, pp.points, pp.polyshapeClosed, pp.geoInfoMode, pp.geoInfoSize,
				pp.beginWithArrow, pp.endWithArrow, pp.arrowForce, pp.symbolCode, pp.symbolSetName,
				pp.font, pp.bold, pp.italic, pp.strikethrough, pp.underlined, pp.geoInfoMode,
				pp.text, pp.linkRessource, pp.borderActivated };

		if (pps1.length != pps2.length)
			throw new IllegalStateException();

		return Arrays.deepEquals(pps1, pps2);

	}

}
