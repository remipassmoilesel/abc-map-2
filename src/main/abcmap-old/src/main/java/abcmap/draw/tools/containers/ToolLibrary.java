package abcmap.draw.tools.containers;

public class ToolLibrary {

	private static ToolContainer[] availableTools;
	public static ToolContainer SELECTION_TOOL;
	public static ToolContainer RECTANGLE_TOOL;
	public static ToolContainer TILE_TOOL;
	public static ToolContainer ELLIPSE_TOOL;
	public static ToolContainer POLYLINE_TOOL;
	public static ToolContainer POLYGON_TOOL;
	public static ToolContainer LABEL_TOOL;
	public static ToolContainer SYMBOL_TOOL;
	public static ToolContainer IMAGE_TOOL;
	public static ToolContainer GEOREF_TOOL;
	public static LegendTC LEGEND_TOOL;
	public static LinkTC LINK_TOOL;

	/**
	 * Initialise les conteneurs d'outils.
	 */
	public static void initializeTools() {

		SELECTION_TOOL = new SelectionTC();
		RECTANGLE_TOOL = new RectangleTC();
		TILE_TOOL = new TileTC();
		ELLIPSE_TOOL = new EllipseTC();
		POLYLINE_TOOL = new PolylineTC();
		POLYGON_TOOL = new PolygonTC();
		LABEL_TOOL = new LabelTC();
		SYMBOL_TOOL = new SymbolTC();
		IMAGE_TOOL = new ImageTC();
		GEOREF_TOOL = new GeorefTC();
		LEGEND_TOOL = new LegendTC();
		LINK_TOOL = new LinkTC();

		availableTools = new ToolContainer[] { SELECTION_TOOL, RECTANGLE_TOOL, ELLIPSE_TOOL,
				POLYLINE_TOOL, POLYGON_TOOL, LABEL_TOOL, SYMBOL_TOOL, IMAGE_TOOL, TILE_TOOL,
				GEOREF_TOOL, LEGEND_TOOL, LINK_TOOL };
		
		// // initialiser les panneaux d'options
		// SwingUtilities.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// for (ToolContainer tc : availableTools) {
		// tc.getOptionPanel();
		// }
		// }
		// });
	}

	/**
	 * Retourne la liste des outils disponibles. Doit être initialisé avant.
	 * 
	 * @return
	 */
	public static ToolContainer[] getAvailablesTools() {
		return availableTools;
	}

}
