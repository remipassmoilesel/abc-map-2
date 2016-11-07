package abcmap.project.loaders.abm;

import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;

import abcmap.managers.Log;
import abcmap.project.loaders.AbmConstants;
import abcmap.project.properties.LayoutMarginsProperties;
import abcmap.project.properties.LayoutProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.Utils;

public class AbmLayoutsLoader {

	public static PropertiesContainer constructPropertiesForLayout(Element elmt) {

		if (elmt == null)
			return null;

		LayoutProperties pp = new LayoutProperties();

		// marges eventuelles
		try {
			List<Element> marginsElmts = elmt
					.getChildren(AbmConstants.LAYOUT_MARGINS_XML_TAG_NAME);

			if (marginsElmts.size() > 0) {
				PropertiesContainer ppm = constructPropertiesForLayoutMargins(marginsElmts
						.get(0));
				if (pp != null) {
					pp.margins = ppm;
				}
			}
		} catch (Exception e) {
			Log.debug(e);
		}

		// parametres a configurer ** dans l'ordre **
		String[] toUnserialize = new String[] {
				AbmConstants.LAYOUT_XML_DIMENSION_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_MAP_POSITION_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_SCALE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_TITLE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_PAGES_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_SCALE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_ASSEMBLY_PAGE_ATTRIBUTE, };

		for (String propertie : toUnserialize) {

			try {

				// recuperation de la valeur
				Attribute attr = elmt.getAttribute(propertie);
				if (attr == null)
					continue;
				
				String value = attr.getValue();
				if (value == null)
					continue;
				
				if (AbmConstants.LAYOUT_XML_DIMENSION_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.mmDimensions = Utils.stringToDimension(value);
				}

				else if (AbmConstants.LAYOUT_XML_MAP_POSITION_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.positionOnMap = Utils.stringToPoint(value);
				}

				else if (AbmConstants.LAYOUT_XML_SCALE_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.mapScale = Float.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_XML_DISPLAY_PAGES_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.displayPageNumbers = Boolean.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_XML_DISPLAY_TITLE_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.displayTitle = Boolean.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_XML_ASSEMBLY_PAGE_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.assemblyPage = Boolean.valueOf(value);
				}

			} catch (Exception e) {
				Log.error(e);
				// continuer si erreur
			}
		}

		return pp;
	}

	public static PropertiesContainer constructPropertiesForLayoutMargins(
			Element elmt) {

		if (elmt == null)
			return null;

		LayoutMarginsProperties pp = new LayoutMarginsProperties();

		// parametres configurer ** dans l'ordre **
		String[] toUnserialize = new String[] {
				AbmConstants.LAYOUT_XML_DIMENSION_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_MAP_POSITION_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_SCALE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_TITLE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_PAGES_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_DISPLAY_SCALE_ATTRIBUTE,
				AbmConstants.LAYOUT_XML_ASSEMBLY_PAGE_ATTRIBUTE, };

		for (String propertie : toUnserialize) {

			try {

				// recuperation de la valeur
				Attribute attr = elmt.getAttribute(propertie);
				if (attr == null)
					continue;
				
				String value = attr.getValue();
				if (value == null)
					continue;

				if (AbmConstants.LAYOUT_MARGINS_XML_NORTH_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.north = Integer.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_MARGINS_XML_EAST_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.east = Integer.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_MARGINS_XML_SOUTH_ATTRIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.south = Integer.valueOf(value);
				}

				else if (AbmConstants.LAYOUT_MARGINS_XML_WEST_ATTIBUTE
						.equalsIgnoreCase(propertie)) {
					pp.west = Integer.valueOf(value);
				}
			}

			catch (Exception e) {
				Log.error(e);
				// continuer si erreur
			}
		}

		return pp;
	}

}
