package abcmap.project.writers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.links.LinkRessource;
import abcmap.draw.shapes.Ellipse;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Polygon;
import abcmap.draw.shapes.Polyline;
import abcmap.draw.shapes.Rectangle;
import abcmap.draw.shapes.Symbol;
import abcmap.draw.shapes.Tile;
import abcmap.geo.Coordinate;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.project.Project;
import abcmap.project.ProjectMetadatas;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.project.loaders.AbmConstants;
import abcmap.project.properties.CoordinateProperties;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.LayerProperties;
import abcmap.project.properties.LayoutMarginsProperties;
import abcmap.project.properties.LayoutProperties;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.PrintUtils;
import abcmap.utils.Utils;

public class AbmDescriptorWriter {

	private ArrayList<Exception> minorExceptions;

	public AbmDescriptorWriter() {
		minorExceptions = new ArrayList<Exception>();
	}

	public void saveProjectDescriptor(Project project) throws IOException {

		File path = new File(project.getTempDirectoryFile().getAbsolutePath()
				+ File.separator + AbmConstants.DESCRIPTOR_FILE_NAME);

		// preparation du document xml + ajout racine
		Document descriptor = new Document();
		Element root = new Element(AbmConstants.PROJECT_ROOT_TAG);
		descriptor.addContent(root);

		/*
		 * parcours et ajout des metadonnees
		 */
		// les metadonnees sont CDATA des balises
		ProjectMetadatas metadatas = project.getMetadatas();
		Class<? extends ProjectMetadatas> metaClass = metadatas.getClass();
		Field[] fields = metaClass.getFields();

		serialization: for (Field f : fields) {

			// eviter les champs inutiles
			if (f.getName().matches("^[a-zA-Z_0-9]+$") == false) {
				if (MainManager.isDebugMode())
					PrintUtils.p("+++ Saving descriptor: field error: "
							+ f.getName());
				continue serialization;
			}

			Element elmt = new Element(f.getName().toLowerCase());
			try {
				Object val = f.get(metadatas);
				if (val instanceof Dimension) {
					elmt.setText(Utils.dimensionToString((Dimension) f
							.get(metadatas)));
				}

				else if (val instanceof Color) {
					elmt.setText(Utils.colorToString((Color) f.get(metadatas)));
				}

				else {
					elmt.setText(f.get(metadatas).toString());
				}

			} catch (IllegalArgumentException | IllegalAccessException e) {
				Log.error(e);
				addMinorException(e);
			}

			root.addContent(elmt);

		}

		/*
		 * Parcours et ajout des calques
		 */
		for (MapLayer lay : project.getLayers()) {

			try {
				Element layerXml = serializeLayer((LayerProperties) lay
						.getProperties());

				// ajout des metadonnees
				root.addContent(layerXml);

				// parcours et ajout des elements du calque
				for (LayerElement elmt : lay.getAllElements()) {
					try {
						Element pp = serializeElement(elmt);
						layerXml.addContent(pp);
					}

					catch (Exception ex) {
						Log.error(ex);
						addMinorException(ex);
					}
				}
			}

			catch (Exception ex) {
				Log.error(ex);
				addMinorException(ex);
			}

		}

		// parcours et ajout des sheets
		for (LayoutPaper layout : project.getLayouts()) {
			try {
				Element elmt = serializeLayout((LayoutProperties) layout
						.getProperties());
				root.addContent(elmt);
			}

			catch (Exception ex) {
				Log.error(ex);
				addMinorException(ex);
			}
		}

		// enregistrement des reference de geolocalisation
		ArrayList<Coordinate> coords = MainManager.getMapManager()
				.getGeoReferences();
		for (Coordinate r : coords) {
			try {
				Element elmt = serializeCoordinate((CoordinateProperties) r
						.getProperties());
				root.addContent(elmt);
			}

			catch (Exception ex) {
				Log.error(ex);
				addMinorException(ex);
			}
		}

		// creer le chemin si non-existant
		if (path.isFile() != true) {
			try {
				path.createNewFile();
			} catch (IOException e) {
				Log.error(e);
				throw new IOException("Unable to write: "
						+ path.getAbsolutePath());
			}
		}

		// ecrire le fichier
		try {
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(descriptor, new FileOutputStream(path));
		} catch (IOException e) {
			Log.error(e);
			throw new IOException("Unable to write: " + path.getAbsolutePath());
		}

	}

	private static Element serializeCoordinate(CoordinateProperties properties) {

		if (properties == null)
			return null;

		Element elmt = new Element(AbmConstants.COORDINATE_XML_TAG_NAME);

		// coordonnées en pixels
		String value1 = properties.latitudePx
				+ AbmConstants.COORDINATE_SEPARATOR + properties.longitudePx;
		elmt.setAttribute(new Attribute(
				AbmConstants.COORDINATE_XML_PIXEL_ATTRIBUTE, value1));

		// coordonnées en degrés
		String value2 = properties.latitudeDg
				+ AbmConstants.COORDINATE_SEPARATOR + properties.longitudeDg;
		elmt.setAttribute(new Attribute(
				AbmConstants.COORDINATE_XML_PIXEL_ATTRIBUTE, value2));

		return elmt;

	}

	private static Element serializeLayout(LayoutProperties properties) {

		if (properties == null)
			return null;

		// nom de l'element
		Element elmt = new Element(AbmConstants.LAYOUT_XML_TAG_NAME);

		// dimensions
		Attribute dim = new Attribute(
				AbmConstants.LAYOUT_XML_DIMENSION_ATTRIBUTE,
				Utils.dimensionToString(properties.mmDimensions));
		elmt.setAttribute(dim);

		// echelle
		Attribute scl = new Attribute(AbmConstants.LAYOUT_XML_SCALE_ATTRIBUTE,
				String.valueOf(properties.mapScale));
		elmt.setAttribute(scl);

		// translation
		Attribute pos = new Attribute(
				AbmConstants.LAYOUT_XML_MAP_POSITION_ATTRIBUTE,
				Utils.pointToString(properties.positionOnMap));
		elmt.setAttribute(pos);

		// marges
		elmt.addContent(serializeLayoutsMargins((LayoutMarginsProperties) properties.margins));

		// modes d'affichage
		Attribute title = new Attribute(
				AbmConstants.LAYOUT_XML_DISPLAY_TITLE_ATTRIBUTE,
				String.valueOf(properties.displayTitle));
		elmt.setAttribute(title);

		Attribute pages = new Attribute(
				AbmConstants.LAYOUT_XML_DISPLAY_PAGES_ATTRIBUTE,
				String.valueOf(properties.displayPageNumbers));
		elmt.setAttribute(pages);

		Attribute dispScl = new Attribute(
				AbmConstants.LAYOUT_XML_DISPLAY_SCALE_ATTRIBUTE,
				String.valueOf(properties.displayScale));
		elmt.setAttribute(dispScl);

		Attribute assembly = new Attribute(
				AbmConstants.LAYOUT_XML_ASSEMBLY_PAGE_ATTRIBUTE,
				String.valueOf(properties.assemblyPage));
		elmt.setAttribute(assembly);

		return elmt;
	}

	private static Element serializeLayoutsMargins(
			LayoutMarginsProperties properties) {

		Element rslt = new Element(AbmConstants.LAYOUT_MARGINS_XML_TAG_NAME);
		rslt.setAttribute(new Attribute(
				AbmConstants.LAYOUT_MARGINS_XML_NORTH_ATTRIBUTE, String
						.valueOf(properties.north)));

		rslt.setAttribute(new Attribute(
				AbmConstants.LAYOUT_MARGINS_XML_EAST_ATTRIBUTE, String
						.valueOf(properties.east)));

		rslt.setAttribute(new Attribute(
				AbmConstants.LAYOUT_MARGINS_XML_WEST_ATTIBUTE, String
						.valueOf(properties.west)));

		rslt.setAttribute(new Attribute(
				AbmConstants.LAYOUT_MARGINS_XML_SOUTH_ATTRIBUTE, String
						.valueOf(properties.south)));
		return rslt;
	}

	private static Element serializeLayer(LayerProperties properties) {

		Element elmt = new Element(AbmConstants.PROJECT_LAYER_TAG_NAME);

		elmt.setAttribute(new Attribute(AbmConstants.LAYER_NAME_ATTRIBUTE,
				properties.name));

		elmt.setAttribute(new Attribute(AbmConstants.LAYER_OPACITY_ATTRIBUTE,
				String.valueOf(properties.opacity)));

		elmt.setAttribute(new Attribute(
				AbmConstants.LAYER_VISIBILITY_ATTRIBUTE, String
						.valueOf(properties.visible)));
		return elmt;
	}

	public static Element serializeElement(LayerElement e) {

		ShapeProperties pp = (ShapeProperties) e.getProperties();

		if (e instanceof Ellipse)
			return serializeEllipse(pp);

		else if (e instanceof Image && e instanceof Tile == false)
			return serializeImage(pp);

		else if (e instanceof Label)
			return serializeLabel(pp);

		else if (e instanceof Polygon)
			return serializePolygon(pp);

		else if (e instanceof Polyline)
			return serializePolyline(pp);

		else if (e instanceof Rectangle)
			return serializeRectangle(pp);

		else if (e instanceof Symbol)
			return serializeSymbol(pp);

		else if (e instanceof Tile)
			return serializeTile(pp);

		else
			throw new IllegalArgumentException();

	}

	private static Element serializeTile(ShapeProperties pp) {

		if (pp == null)
			return null;

		// element principal
		Element elmt = new Element(Tile.class.getSimpleName().toLowerCase());

		// position
		Attribute pos = new Attribute(AbmConstants.ELMT_XML_POSITION_ATTRIBUTE,
				Utils.pointToString(pp.position));
		elmt.setAttribute(pos);

		// tuile
		Attribute src = new Attribute(AbmConstants.ELMT_XML_SOURCE_ATTRIBUTE,
				pp.sourceFile);
		elmt.setAttribute(src);

		// pas de liens dans les tuiles
		// pas d'information geo

		return elmt;
	}

	private static Element serializeSymbol(ShapeProperties pp) {

		Element elmt = new Element(Symbol.class.getSimpleName().toLowerCase());

		Attribute[] attr = new Attribute[] {
				new Attribute(AbmConstants.ELMT_XML_SIZE_ATTRIBUTE,
						String.valueOf(pp.size)),
				new Attribute(AbmConstants.SYMBOL_XML_SETNAME_ATTR,
						pp.symbolSetName),
				new Attribute(AbmConstants.SYMBOL_XML_CODE_ATTR,
						String.valueOf(pp.symbolCode)),
				new Attribute(AbmConstants.ELMT_XML_POSITION_ATTRIBUTE,
						Utils.pointToString(pp.position)), };

		for (Attribute a : attr) {
			elmt.setAttribute(a);
		}

		// couleurs
		serializeStroke(pp.stroke, elmt);

		// lien

		serializeLink(pp.linkRessource, elmt);

		return elmt;
	}

	private static Element serializeRectangle(ShapeProperties pp) {

		Element elmt = new Element(Rectangle.class.getSimpleName()
				.toLowerCase());

		// attributs geo
		serializeGeoMode(pp, elmt);

		// position et dimensions
		serializePositionAndDimensions(pp, elmt);

		// caracteristiques de dessin
		serializeStroke(pp.stroke, elmt);

		// lien
		serializeLink(pp.linkRessource, elmt);

		return elmt;
	}

	private static Element serializePolygon(ShapeProperties pp) {

		Element elmt = new Element(Polygon.class.getSimpleName().toLowerCase());

		// forme clause
		Attribute cl = new Attribute(
				AbmConstants.POLYSHAPE_XML_CLOSED_ATTRIBUTE,
				String.valueOf(pp.polyshapeClosed));
		elmt.setAttribute(cl);

		// attributs geo
		serializeGeoMode(pp, elmt);

		// stroke
		serializeStroke(pp.stroke, elmt);

		// points
		for (Point p : pp.points) {
			Element e = new Element(AbmConstants.POLYSHAPE_XML_POINT_TAG);
			Attribute attr = new Attribute(
					AbmConstants.POLYSHAPE_XML_POINT_ATTRIBUTE,
					Utils.pointToString(p));

			e.setAttribute(attr);
			elmt.addContent(e);
		}

		// lien
		serializeLink(pp.linkRessource, elmt);

		return elmt;
	}

	private static Element serializePolyline(ShapeProperties pp) {

		Element elmt = new Element(Polyline.class.getSimpleName().toLowerCase());

		// fleches
		Attribute ea = new Attribute(AbmConstants.POLYSHAPE_XML_END_ARROW_ATTR,
				String.valueOf(pp.endWithArrow));
		elmt.setAttribute(ea);
		Attribute ba = new Attribute(
				AbmConstants.POLYSHAPE_XML_BEGIN_ARROW_ATTR,
				String.valueOf(pp.beginWithArrow));
		elmt.setAttribute(ba);
		Attribute af = new Attribute(
				AbmConstants.POLYSHAPE_XML_ARROW_FORCE_ATTR,
				String.valueOf(pp.arrowForce));
		elmt.setAttribute(af);

		// stroke
		serializeStroke(pp.stroke, elmt);

		// informations
		Attribute inf = new Attribute(AbmConstants.ELMT_XML_INFOMODE_ATTRIBUTE,
				pp.geoInfoMode);
		elmt.setAttribute(inf);

		Attribute txtSize = new Attribute(
				AbmConstants.ELMT_XML_INFOSIZE_ATTRIBUTE,
				String.valueOf(pp.geoInfoSize));
		elmt.setAttribute(txtSize);

		// points
		for (Point p : pp.points) {
			Element e = new Element(AbmConstants.POLYSHAPE_XML_POINT_TAG);
			Attribute attr = new Attribute(
					AbmConstants.POLYSHAPE_XML_POINT_ATTRIBUTE,
					Utils.pointToString(p));

			e.setAttribute(attr);
			elmt.addContent(e);
		}

		// lien

		serializeLink(pp.linkRessource, elmt);

		return elmt;

	}

	private static Element serializeLabel(ShapeProperties pp) {

		Element elmt = new Element(Label.class.getSimpleName().toLowerCase());

		// ajout du stroke
		serializeStroke(pp.stroke, elmt);

		// police et caracteristiques
		ArrayList<Attribute> list = new ArrayList<>();
		list.add(new Attribute(AbmConstants.ELMT_XML_POSITION_ATTRIBUTE, Utils
				.pointToString(pp.position)));
		list.add(new Attribute(AbmConstants.LABEL_XML_FONT_ATTRIBUTE, pp.font));
		list.add(new Attribute(AbmConstants.ELMT_XML_SIZE_ATTRIBUTE, String
				.valueOf(pp.size)));
		list.add(new Attribute(AbmConstants.LABEL_XML_BOLD_ATTRIBUTE, String
				.valueOf(pp.bold)));
		list.add(new Attribute(AbmConstants.LABEL_XML_ITALIC_ATTRIBUTE, String
				.valueOf(pp.italic)));
		list.add(new Attribute(AbmConstants.LABEL_XML_STRIKETHROUGH_ATTRIBUTE,
				String.valueOf(pp.strikethrough)));
		list.add(new Attribute(AbmConstants.LABEL_XML_UNDERLINED_ATTRIBUTE,
				String.valueOf(pp.underlined)));
		list.add(new Attribute(AbmConstants.LABEL_XML_MODE_ATRIBUTE, String
				.valueOf(pp.geoInfoMode)));
		list.add(new Attribute(AbmConstants.LABEL_XML_BORDER_ATTRIBUTE, String
				.valueOf(pp.borderActivated)));

		for (Attribute t : list)
			elmt.setAttribute(t);

		// lien
		serializeLink(pp.linkRessource, elmt);

		// ajout du texte
		elmt.addContent(pp.text);

		return elmt;
	}

	private static Element serializeImage(ShapeProperties pp) {

		// element principal
		Element elmt = new Element(Image.class.getSimpleName().toLowerCase());

		// image
		Attribute src = new Attribute(AbmConstants.ELMT_XML_SOURCE_ATTRIBUTE,
				pp.sourceFile);
		elmt.setAttribute(src);

		// attributs geo
		serializeGeoMode(pp, elmt);

		// position et dimensions
		serializePositionAndDimensions(pp, elmt);

		// caracteristiques de dessin
		serializeStroke(pp.stroke, elmt);

		// lien
		serializeLink(pp.linkRessource, elmt);

		return elmt;
	}

	private static Element serializeEllipse(ShapeProperties pp) {

		Element elmt = new Element(Ellipse.class.getSimpleName().toLowerCase());

		// attributs geo
		serializeGeoMode(pp, elmt);

		// position et dimensions
		serializePositionAndDimensions(pp, elmt);

		// caracteristiques de dessin
		serializeStroke(pp.stroke, elmt);

		// lien
		serializeLink(pp.linkRessource, elmt);

		return elmt;
	}

	private static void serializeStroke(DrawPropertiesContainer pp,
			Element parent) {

		if (parent == null)
			throw new NullPointerException("Parent is null");

		if (pp != null) {

			Element elmt = new Element(AbmConstants.STROKE_XML_TAG_NAME);

			Attribute line = new Attribute(
					AbmConstants.STROKE_XML_LINE_ATTRIBUTE, pp.linestyle);
			elmt.setAttribute(line);

			Attribute thick = new Attribute(
					AbmConstants.STROKE_XML_THICK_ATTRIBUTE,
					String.valueOf(pp.thickness));
			elmt.setAttribute(thick);

			Attribute col = new Attribute(
					AbmConstants.STROKE_XML_COLOR_ATTRIBUTE,
					Utils.colorToString(pp.fgColor));
			elmt.setAttribute(col);

			Attribute fi = new Attribute(
					AbmConstants.STROKE_XML_FILLPAINT_ATTRIBUTE,
					Utils.colorToString(pp.bgColor));
			elmt.setAttribute(fi);

			Attribute tx = new Attribute(
					AbmConstants.STROKE_XML_TEXTURE_ATTRIBUTE, pp.texture);
			elmt.setAttribute(tx);

			parent.addContent(elmt);

		}

	}

	private static void serializeLink(LinkRessource lnk, Element parent) {

		if (parent == null)
			throw new NullPointerException("Parent is null");

		if (lnk != null) {

			// creer un element de lien
			Element elmt = new Element(AbmConstants.LINK_XML_TAG_NAME);

			// source du lien
			Attribute location = new Attribute(
					AbmConstants.LINK_XML_LOCATION_ATTRIBUTE, lnk.getLocation());
			elmt.setAttribute(location);

			// action a effetuere
			Attribute action = new Attribute(
					AbmConstants.LINK_XML_ACTION_ATTRIBUTE, lnk.getAction()
							.toString());
			elmt.setAttribute(action);

			// ajout
			parent.addContent(elmt);

		}

	}

	private static void serializeGeoMode(ShapeProperties pp, Element elmt) {

		if (elmt == null)
			throw new NullPointerException("Parent is null");

		// informations à afficher
		if (pp.geoInfoMode != null) {
			Attribute geoMode = new Attribute(
					AbmConstants.ELMT_XML_INFOMODE_ATTRIBUTE, pp.geoInfoMode);
			elmt.setAttribute(geoMode);
		}

		// taille du texte
		if (pp.geoInfoSize != 0) {
			Attribute geoSize = new Attribute(
					AbmConstants.ELMT_XML_INFOSIZE_ATTRIBUTE,
					Integer.toString(pp.geoInfoSize));
			elmt.setAttribute(geoSize);
		}

	}

	private static void serializePositionAndDimensions(ShapeProperties pp,
			Element elmt) {

		if (elmt == null)
			throw new NullPointerException("Parent is null");

		// position
		if (pp.position != null) {
			Attribute pos = new Attribute(
					AbmConstants.ELMT_XML_POSITION_ATTRIBUTE,
					Utils.pointToString(pp.position));
			elmt.setAttribute(pos);
		}

		// dimensions
		if (pp.dimensions != null) {
			Attribute dim = new Attribute(
					AbmConstants.ELMT_XML_DIMENSION_ATTRIBUTE,
					Utils.dimensionToString(pp.dimensions));
			elmt.setAttribute(dim);
		}

	}

	public ArrayList<Exception> getMinorExceptions() {
		return minorExceptions;
	}

	private void addMinorException(Exception e) {
		minorExceptions.add(e);
	}
}
