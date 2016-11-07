package abcmap.project;

import java.awt.Color;
import java.awt.Dimension;

import abcmap.configuration.ConfigurationConstants;
import abcmap.geo.GeoSystemsContainer;
import abcmap.utils.Utils;

/**
 * Toutes variables en majuscules pour serialisation xml
 * 
 * Utiliser uniquement des objets type Boolean, Integer, ....
 * 
 * @author remipassmoilesel
 *
 */
public class ProjectMetadatas {

	public Boolean GEOREFMODE_ENABLED = false;
	public String GEOSYSTEM_EPSG_CODE = GeoSystemsContainer.EMPTY_CRS;

	public String PROJECT_TITLE = "";
	public Boolean MAP_DIMENSIONS_FIXED = false;
	public Dimension MAP_DIMENSIONS = ConfigurationConstants.MINIMUM_MAP_DIMENSIONS;

	public String PROJECT_COMMENT = "Comments";
	public String CREATION_DATE = "";
	public String PROFILE_ATTACHED = "./prf/default.prf";
	public String BACKGROUND_COLOR = Utils.colorToString(Color.white);

	// affichage pour tableau d'assemblage
	public Float LAYOUT_FRAME_OPACITY = 0.6f;
	public Integer LAYOUT_FRAME_THICKNESS = 20;
	public Color LAYOUT_FRAME_COLOR_1 = Color.blue;
	public Color LAYOUT_FRAME_COLOR_2 = Color.red;
}
