package abcmap.project.loaders;

import abcmap.geo.Coordinate;

public class AbmConstants {

	public static final String DESCRIPTOR_FILE_NAME = "descriptor.xml";
	public final static String PROJECT_ROOT_TAG = "document";
	public final static String PROJECT_LAYER_TAG_NAME = "layer";
	public final static String PROJECT_LAYOUT_TAG_NAME = "layout";

	/*
	 * Coordonnées
	 */

	public static final String COORDINATE_XML_TAG_NAME = Coordinate.class.getSimpleName()
			.toLowerCase();
	public static final String COORDINATE_XML_PIXEL_ATTRIBUTE = "pixel";
	public static final String COORDINATE_SEPARATOR = ":";
	public static final String COORDINATE_XML_DEGREES_ATTRIBUTE = "degrees";

	/*
	 * Layouts
	 */

	public static final String LAYOUT_XML_TAG_NAME = "layoutsheet";
	public static final String LAYOUT_XML_DIMENSION_ATTRIBUTE = "dimension";
	public static final String LAYOUT_XML_SCALE_ATTRIBUTE = "scale";
	public static final String LAYOUT_XML_MAP_POSITION_ATTRIBUTE = "mapPosition";
	public static final String LAYOUT_XML_DISPLAY_TITLE_ATTRIBUTE = "displayTitle";
	public static final String LAYOUT_XML_DISPLAY_PAGES_ATTRIBUTE = "displayPagesNmr";
	public static final String LAYOUT_XML_ASSEMBLY_PAGE_ATTRIBUTE = "assemblyPage";
	public static final String LAYOUT_XML_DISPLAY_SCALE_ATTRIBUTE = "displayScale";

	/*
	 * Marges
	 */

	public static final String LAYOUT_MARGINS_XML_TAG_NAME = "margins";
	public static final String LAYOUT_MARGINS_XML_NORTH_ATTRIBUTE = "north";
	public static final String LAYOUT_MARGINS_XML_EAST_ATTRIBUTE = "east";
	public static final String LAYOUT_MARGINS_XML_SOUTH_ATTRIBUTE = "south";
	public static final String LAYOUT_MARGINS_XML_WEST_ATTIBUTE = "west";

	public static final String LAYER_NAME_ATTRIBUTE = "name";
	public static final String LAYER_VISIBILITY_ATTRIBUTE = "visible";
	public static final String LAYER_OPACITY_ATTRIBUTE = "opacity";

	/*
	 * Général
	 */

	public static final String ELMT_XML_DIMENSION_ATTRIBUTE = "dimensions";
	public static final String ELMT_XML_POSITION_ATTRIBUTE = "position";
	public static final String ELMT_XML_SOURCE_ATTRIBUTE = "src";
	public static final String ELMT_XML_SIZE_ATTRIBUTE = "size";

	public static final String ELMT_XML_INFOMODE_ATTRIBUTE = "infomode";
	public static final String ELMT_XML_INFOSIZE_ATTRIBUTE = "infosize";

	/*
	 * Strokes
	 */

	public static final String STROKE_XML_TAG_NAME = "stroke";
	public static final String STROKE_XML_THICK_ATTRIBUTE = "thickness";
	public static final String STROKE_XML_COLOR_ATTRIBUTE = "color";
	public static final String STROKE_XML_FILLPAINT_ATTRIBUTE = "fill";
	public static final String STROKE_XML_LINE_ATTRIBUTE = "line";
	public static final String STROKE_XML_TEXTURE_ATTRIBUTE = "texture";
	/*
	 * Liens
	 */

	public static final String LINK_XML_TAG_NAME = "link";
	public static final String LINK_XML_LOCATION_ATTRIBUTE = "location";
	public static final String LINK_XML_ACTION_ATTRIBUTE = "action";

	/*
	 * Polygones
	 */

	public static final String POLYSHAPE_XML_POINT_TAG = "p";
	public static final String POLYSHAPE_XML_POINT_ATTRIBUTE = "xy";

	public static final String POLYSHAPE_XML_CLOSED_ATTRIBUTE = "closed";
	public static final String POLYSHAPE_XML_END_ARROW_ATTR = "endWithArrow";
	public static final String POLYSHAPE_XML_BEGIN_ARROW_ATTR = "beginWithArrow";
	public static final String POLYSHAPE_XML_ARROW_FORCE_ATTR = "arrowForce";

	/*
	 * Symbols
	 */

	public static final String SYMBOL_XML_SETNAME_ATTR = "set";
	public static final String SYMBOL_XML_CODE_ATTR = "code";

	/*
	 * Label
	 */

	public static final String LABEL_XML_FONT_ATTRIBUTE = "fontname";
	public static final String LABEL_XML_BOLD_ATTRIBUTE = "bold";
	public static final String LABEL_XML_ITALIC_ATTRIBUTE = "italic";
	public static final String LABEL_XML_STRIKETHROUGH_ATTRIBUTE = "strikethrough";
	public static final String LABEL_XML_UNDERLINED_ATTRIBUTE = "underlined";
	public static final String LABEL_XML_MODE_ATRIBUTE = "mode";
	public static final String LABEL_LINE_SEPARATOR = "\n";
	public static final String LABEL_XML_BORDER_ATTRIBUTE = "borderActivated";

}
