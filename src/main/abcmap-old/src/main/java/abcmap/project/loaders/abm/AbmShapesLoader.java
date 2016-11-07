package abcmap.project.loaders.abm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import abcmap.draw.links.LinkAction;
import abcmap.draw.links.LinkLibrary;
import abcmap.draw.links.LinkRessource;
import abcmap.managers.Log;
import abcmap.project.loaders.AbmConstants;
import abcmap.project.properties.CoordinateProperties;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.LayerProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.Utils;

public class AbmShapesLoader {

	public static PropertiesContainer getCoordinatePropertiesFrom(Element elmt) {

		if (elmt == null)
			return null;

		CoordinateProperties properties = new CoordinateProperties();

		String[] toUnserialize = new String[] { AbmConstants.COORDINATE_XML_PIXEL_ATTRIBUTE,
				AbmConstants.COORDINATE_XML_DEGREES_ATTRIBUTE, };

		for (String propertie : toUnserialize) {

			try {

				// recuperation de la valeur
				Attribute attr = elmt.getAttribute(propertie);
				if (attr == null)
					continue;

				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.COORDINATE_XML_PIXEL_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					String[] pixels = value.split(AbmConstants.COORDINATE_SEPARATOR);
					if (pixels.length == 2) {
						properties.latitudePx = Double.valueOf(pixels[0]);
						properties.longitudePx = Double.valueOf(pixels[1]);
					}
				}

				else if (AbmConstants.COORDINATE_XML_DEGREES_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					String[] degrees = value.split(AbmConstants.COORDINATE_SEPARATOR);
					if (degrees.length == 2) {
						properties.latitudeDg = Double.valueOf(degrees[0]);
						properties.longitudeDg = Double.valueOf(degrees[1]);
					}
				}

			} catch (Exception e) {
				Log.error(e);
				return null;
			}
		}

		return properties;
	}

	public static PropertiesContainer getLayerPropertiesFrom(Element elmt) {

		if (elmt == null)
			return null;

		LayerProperties pp = new LayerProperties();

		String[] toUnserialize = new String[] { AbmConstants.LAYER_NAME_ATTRIBUTE,
				AbmConstants.LAYER_VISIBILITY_ATTRIBUTE, AbmConstants.LAYER_OPACITY_ATTRIBUTE };

		for (String propertie : toUnserialize) {

			try {

				Attribute attr = elmt.getAttribute(propertie);
				if (attr == null)
					continue;

				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.LAYER_NAME_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.name = value;
				}

				else if (AbmConstants.LAYER_VISIBILITY_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.visible = Boolean.valueOf(value);
				}

				else if (AbmConstants.LAYER_OPACITY_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.opacity = Float.valueOf(value);
				}

			} catch (Exception e) {
				Log.error(e);
				// continuer si erreur
			}
		}

		return pp;
	}

	/**
	 * Analyse un element XML et retourne un conteneur de proprietes.
	 * 
	 * @param elmt
	 * @return
	 */
	public static PropertiesContainer getElementPropertiesFrom(Element elmt) {

		if (elmt == null)
			throw new NullPointerException();

		// construction du conteneur a retourner
		ShapeProperties pp = new ShapeProperties();

		// recherche d'eventuels elements de proprietes de dessin
		pp.stroke = getDrawStrokesFrom(elmt);

		// recherche d'eventuels elements de points
		ArrayList<Point> points = getPointsFrom(elmt);
		if (points != null)
			pp.points = points;

		// texte eventuel 
		pp.text = elmt.getText() != null ? elmt.getText().trim() : null;
		
		// recherche d'envetuels liens
		LinkRessource link = getLinkRessourceFrom(elmt);
		if (link != null) {
			pp.linkRessource = link;
		}

		String[] toUnserialize = new String[] { AbmConstants.ELMT_XML_DIMENSION_ATTRIBUTE,
				AbmConstants.ELMT_XML_POSITION_ATTRIBUTE, AbmConstants.ELMT_XML_SIZE_ATTRIBUTE,
				AbmConstants.ELMT_XML_SOURCE_ATTRIBUTE, AbmConstants.POLYSHAPE_XML_CLOSED_ATTRIBUTE,
				AbmConstants.ELMT_XML_INFOMODE_ATTRIBUTE, AbmConstants.ELMT_XML_INFOSIZE_ATTRIBUTE,
				AbmConstants.POLYSHAPE_XML_END_ARROW_ATTR,
				AbmConstants.POLYSHAPE_XML_BEGIN_ARROW_ATTR,
				AbmConstants.POLYSHAPE_XML_ARROW_FORCE_ATTR, AbmConstants.SYMBOL_XML_SETNAME_ATTR,
				AbmConstants.SYMBOL_XML_CODE_ATTR, AbmConstants.LABEL_XML_FONT_ATTRIBUTE,
				AbmConstants.LABEL_XML_BOLD_ATTRIBUTE, AbmConstants.LABEL_XML_ITALIC_ATTRIBUTE,
				AbmConstants.LABEL_XML_STRIKETHROUGH_ATTRIBUTE,
				AbmConstants.LABEL_XML_UNDERLINED_ATTRIBUTE, AbmConstants.LABEL_XML_MODE_ATRIBUTE };

		for (String propertie : toUnserialize) {

			try {
				Attribute attr = elmt.getAttribute(propertie);
				if (attr == null)
					continue;

				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.ELMT_XML_DIMENSION_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.dimensions = Utils.stringToDimension(value);
				}

				else if (AbmConstants.ELMT_XML_POSITION_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.position = Utils.stringToPoint(value);
				}

				else if (AbmConstants.ELMT_XML_SIZE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.size = Integer.valueOf(value);
				}

				else if (AbmConstants.ELMT_XML_SOURCE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					// attention: ne pas ajouter le chemin temporaire du projet
					pp.sourceFile = value; 
				}

				else if (AbmConstants.POLYSHAPE_XML_CLOSED_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.polyshapeClosed = Boolean.valueOf(value);
				}

				else if (AbmConstants.ELMT_XML_INFOMODE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.geoInfoMode = value;
				}

				else if (AbmConstants.ELMT_XML_INFOSIZE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.geoInfoSize = Integer.valueOf(value);
				}

				else if (AbmConstants.POLYSHAPE_XML_END_ARROW_ATTR.equalsIgnoreCase(propertie)) {
					pp.endWithArrow = Boolean.valueOf(value);
				}

				else if (AbmConstants.POLYSHAPE_XML_BEGIN_ARROW_ATTR.equalsIgnoreCase(propertie)) {
					pp.beginWithArrow = Boolean.valueOf(value);
				}

				else if (AbmConstants.POLYSHAPE_XML_ARROW_FORCE_ATTR.equalsIgnoreCase(propertie)) {
					pp.arrowForce = Integer.valueOf(value);
				}

				else if (AbmConstants.SYMBOL_XML_SETNAME_ATTR.equalsIgnoreCase(propertie)) {
					pp.symbolSetName = value;
				}

				else if (AbmConstants.SYMBOL_XML_CODE_ATTR.equalsIgnoreCase(propertie)) {
					pp.symbolCode = Integer.valueOf(value);
				}

				else if (AbmConstants.LABEL_XML_FONT_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.font = value;
				}

				else if (AbmConstants.LABEL_XML_BOLD_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.bold = Boolean.valueOf(value);
				}

				else if (AbmConstants.LABEL_XML_ITALIC_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.italic = Boolean.valueOf(value);
				}

				else if (AbmConstants.LABEL_XML_STRIKETHROUGH_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.strikethrough = Boolean.valueOf(value);
				}

				else if (AbmConstants.LABEL_XML_MODE_ATRIBUTE.equalsIgnoreCase(propertie)) {
					pp.geoInfoMode = value;
				}

			} catch (Exception e) {
				Log.error(e);
				// continuer si erreur
			}

		}

		return pp;
	}

	private static LinkRessource getLinkRessourceFrom(Element xmlelmt) {

		if (xmlelmt == null)
			throw new NullPointerException();

		// recuperer les enfants correspondants
		List<Element> childsList = xmlelmt.getChildren(AbmConstants.LINK_XML_TAG_NAME);

		// l'element ne contient aucun style
		if (childsList.size() <= 0)
			return null;

		// ne prendre en compte que le premier enfant
		Element xmlChild = childsList.get(0);

		String location = null;
		LinkAction action = null;

		// liste des attributs possibles à explorer
		String[] toUnserialize = new String[] { AbmConstants.LINK_XML_ACTION_ATTRIBUTE,
				AbmConstants.LINK_XML_LOCATION_ATTRIBUTE };

		// parcour des attributs possibles
		for (String propertie : toUnserialize) {

			try {

				// recuperer l'attribut et la valeur, u continuer si null
				Attribute attr = xmlChild.getAttribute(propertie);
				if (attr == null)
					continue;

				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.LINK_XML_ACTION_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					action = LinkAction.valueOf(value);
				}

				else if (AbmConstants.LINK_XML_LOCATION_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					location = value;
				}

			} catch (Exception e1) {
				Log.error(e1);
				// continuer
			}

		}

		if (location != null && action != null)
			return LinkLibrary.getLink(location, action);

		else
			return null;
	}

	/**
	 * Analyse un element et retourne un tableau des conteneurs de style
	 * enfants. Si l'element ne contient aucun style renvoi null.<br>
	 * [0]: foreground<br>
	 * [1]: background
	 * 
	 * @param xmlelmt
	 * @return
	 */
	private static DrawPropertiesContainer getDrawStrokesFrom(Element xmlelmt) {

		if (xmlelmt == null)
			throw new NullPointerException();

		// recuperer les elements enfants pouvant correspondre aux
		// styles
		List<Element> childsList = xmlelmt.getChildren(AbmConstants.STROKE_XML_TAG_NAME);

		// l'element ne contient aucun style
		if (childsList.size() <= 0)
			return null;

		// ne prendre en compte que le premier enfant
		Element xmlChild = childsList.get(0);

		// creer le style et l'ajouter à la liste
		DrawPropertiesContainer pp = new DrawPropertiesContainer();

		// liste des attributs possibles à explorer
		String[] toUnserialize = new String[] { AbmConstants.STROKE_XML_COLOR_ATTRIBUTE,
				AbmConstants.STROKE_XML_FILLPAINT_ATTRIBUTE, AbmConstants.STROKE_XML_LINE_ATTRIBUTE,
				AbmConstants.STROKE_XML_THICK_ATTRIBUTE,
				AbmConstants.STROKE_XML_TEXTURE_ATTRIBUTE };

		// parcour des attributs possibles
		for (String propertie : toUnserialize) {

			try {

				// recuperer l'attribut et la valeur, u continuer si null
				Attribute attr = xmlChild.getAttribute(propertie);
				if (attr == null)
					continue;

				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.STROKE_XML_COLOR_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.fgColor = Utils.stringToColor(value);
				}

				else if (AbmConstants.STROKE_XML_FILLPAINT_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.bgColor = Utils.stringToColor(value);
				}

				else if (AbmConstants.STROKE_XML_LINE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.linestyle = value;
				}

				else if (AbmConstants.STROKE_XML_THICK_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.thickness = new Integer(value);
				}

				else if (AbmConstants.STROKE_XML_TEXTURE_ATTRIBUTE.equalsIgnoreCase(propertie)) {
					pp.texture = value;
				}

			} catch (Exception e1) {
				Log.error(e1);
				// continuer
			}

		}

		return pp;

	}

	private static ArrayList<Point> getPointsFrom(Element elmt) {
		try {
			List<Element> pointsElmt = elmt.getChildren(AbmConstants.POLYSHAPE_XML_POINT_TAG);

			if (pointsElmt.size() > 0) {
				ArrayList<Point> points = new ArrayList<Point>();
				for (Element e : pointsElmt) {
					try {
						// recuperer attribut + valeur ou continuer si nulls
						Attribute attr = e.getAttribute(AbmConstants.POLYSHAPE_XML_POINT_ATTRIBUTE);
						if (attr == null)
							continue;

						String value = attr.getValue();
						if (value == null)
							continue;

						Point p = Utils.stringToPoint(value);
						if (p != null)
							points.add(p);

					} catch (Exception e1) {
						Log.error(e1);
						// continuer en cas d'erreur
					}
				}

				return points;
			}

		} catch (Exception e) {
			Log.debug(e);
		}

		return null;
	}
}
