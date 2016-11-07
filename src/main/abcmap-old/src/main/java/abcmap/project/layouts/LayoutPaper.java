package abcmap.project.layouts;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import abcmap.cancel.memento.HasMementoManager;
import abcmap.cancel.memento.MementoManager;
import abcmap.cancel.memento.PropertiesContainerCanceler;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layouts.LayoutFormat.Orientation;
import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.LayoutProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.Utils;

/**
 * Feuille de mise en page. Une feuille doit avoir un format imprimable et
 * affiche une portion de carte.
 * 
 * @author remipassmoilesel
 *
 */
public class LayoutPaper extends Paper implements
		HasMementoManager<PropertiesContainer>, Printable,
		AcceptPropertiesContainer {

	// TODO
	/**
	 * Les retours de getImageableX, Y, W et H doivent ils être en 72 dpi ?
	 */

	private static final double JAVA_DISPLAY_RES = 72d;

	public static DecimalFormat meterFormat;
	public static DecimalFormat kmFormat;
	{
		meterFormat = new DecimalFormat("###");
		meterFormat.setRoundingMode(RoundingMode.UP);

		kmFormat = new DecimalFormat("###.##");
		kmFormat.setRoundingMode(RoundingMode.UP);
	}

	/**
	 * La position (Upper Left Corner) de la feuille sur la carte, en pixels
	 */
	private Point positionOnMap;

	/**
	 * La résolution d'impression d'une feuille, pour transformation des
	 * dimensions de pixel à mm
	 */
	private double printResolution;

	/**
	 * Si vrai, le titre du document sera affiché
	 */
	private boolean displayTitle;

	/**
	 * Si vrai, le numéro de page sera affiché
	 */
	private boolean displayPageNumbers;

	/**
	 * Si vrai, les cadres de mise en page seront affichés
	 */
	private boolean assemblyPage;

	/**
	 * Si vrai, l'échelle sera affichée
	 */
	private boolean displayScale;

	/** Coefficient d'affichage de la carte */
	private float mapScale;

	/**
	 * Les marges de la feuille
	 */
	private LayoutMargins margins;

	/** Les informations de dimensions de la feuille */
	private LayoutFormat format;

	/** Si vrai, la feuille sera concernée par les modifications */
	private boolean active;

	/** Gestion des etats pour annulation/repetition */
	private PropertiesContainerCanceler canceler;

	private MapManager mapm;

	private ProjectManager projectm;

	public LayoutPaper() {

		mapm = MainManager.getMapManager();
		projectm = MainManager.getProjectManager();

		this.printResolution = 200;

		this.displayTitle = false;
		this.displayPageNumbers = false;
		this.assemblyPage = false;

		this.displayScale = false;

		this.margins = new LayoutMargins();

		this.format = new LayoutFormat(LayoutFormat.A4_PORTRAIT);

		this.canceler = new PropertiesContainerCanceler(this);

		// positionnée par défaut au centre de la carte
		setPositionOnMapCenter();

	}

	/**
	 * Positionner la feuille au centre de la carte
	 */
	public void setPositionOnMapCenter() {

		Dimension mapDim = projectm.getMapDimensions();
		Dimension papDim = format.getPixelDimensions(JAVA_DISPLAY_RES);

		int xm = mapDim.width / 2 + papDim.width / 2;
		int ym = mapDim.height / 2 + papDim.height / 2;

		setPositionOnMap(new Point(xm, ym));

	}

	/**
	 * Dessiner la feuille, avec les marges
	 * 
	 * @param g2d
	 */
	public void render(Graphics2D g2d) {

	}

	/**
	 * Renvoyer les dimensions en pixel de la feuille, à la résolution
	 * d'impression
	 * 
	 * @return
	 */
	public Dimension getPixelDimensions() {
		return getPixelDimensions(this.printResolution);
	}

	public Dimension getPixelDimensions(double resolution) {
		return format.getPixelDimensions(resolution);
	}

	/**
	 * Retourne la résolution d'impression de la feuille
	 * 
	 * @return
	 */
	public double getPrintResolution() {
		return printResolution;
	}

	/**
	 * Retourne les dimensions en millimètre de la feuille
	 * 
	 * @return
	 */
	public Dimension getDimensionsMm() {
		return format.getMillimeterDimensions();
	}

	/**
	 * Retourne l'orientation de la feuille, portrait ou paysage.
	 * 
	 * @return
	 */
	public Orientation getOrientation() {
		return format.getOrientation();
	}

	/**
	 * Retourne la position de la feuille sur la carte
	 * 
	 * @return
	 */
	public Point getPositionOnMap() {
		return positionOnMap;
	}

	/**
	 * Retourne la position sur la carte inversée.
	 * 
	 * @return
	 */
	public Point getMapTranslation() {
		return new Point(-positionOnMap.x, -positionOnMap.y);
	}

	public void setPositionOnMap(Point paperPosition) {
		this.positionOnMap = new Point(paperPosition);
	}

	public void setResolution(int resolution) {
		this.printResolution = resolution;
	}

	/**
	 * Méthode nécéssaire pour impression. Retourne les dimensions en pixel en
	 * 72DPI, et selon l'orientation du papier
	 * 
	 * @return
	 */
	@Override
	public double getWidth() {
		Dimension dim = format.getPixelDimensions(JAVA_DISPLAY_RES);
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			return dim.width;
		} else {
			return dim.height;
		}
	}

	/**
	 * Méthode nécéssaire pour impression. Retourne les dimensions en
	 * pixel/72DPI, et selon l'orientation du papier
	 * 
	 * @return
	 */
	@Override
	public double getHeight() {
		Dimension dim = format.getPixelDimensions(JAVA_DISPLAY_RES);
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			return dim.height;
		} else {
			return dim.width;
		}
	}

	/**
	 * Méthode nécéssaire pour impression. Retourne le point X de la zone
	 * d'impression en pixel/72DPI, et selon l'orientation du papier
	 */
	@Override
	public double getImageableX() {

		double rslt = 0;
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			rslt = Utils.millimeterToPixel(margins.getWest(), JAVA_DISPLAY_RES);
		} else {
			rslt = Utils
					.millimeterToPixel(margins.getSouth(), JAVA_DISPLAY_RES);
		}

		return rslt;
	}

	/**
	 * Méthode nécéssaire pour impression. Retourne le point Y de la zone
	 * d'impression en pixel/72DPI, et selon l'orientation du papier
	 */
	@Override
	public double getImageableY() {

		double rslt = 0;
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			rslt = Utils
					.millimeterToPixel(margins.getNorth(), JAVA_DISPLAY_RES);
		} else {
			rslt = Utils.millimeterToPixel(margins.getWest(), JAVA_DISPLAY_RES);
		}

		return rslt;

	}

	/**
	 * Méthode nécéssaire pour impression. Retourne le point Y de la zone
	 * d'impression en pixel/72DPI, et selon l'orientation du papier
	 */
	@Override
	public double getImageableWidth() {

		double rslt = 0;
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			rslt = format.getWidth() - margins.getWest() - margins.getEast();
		} else {
			rslt = format.getHeight() - margins.getNorth() - margins.getSouth();
		}

		// conversion en pixel puis retour
		rslt = Utils.millimeterToPixel(rslt, JAVA_DISPLAY_RES);

		return rslt;

	}

	/**
	 * Résultat en 72dpi
	 */
	@Override
	public double getImageableHeight() {

		double rslt = 0;
		if (Orientation.PORTRAIT.equals(format.getOrientation())) {
			rslt = format.getHeight() - margins.getNorth() - margins.getSouth();
		} else {
			rslt = format.getWidth() - margins.getWest() - margins.getEast();
		}

		// conversion en pixel puis retour
		rslt = Utils.millimeterToPixel(rslt, JAVA_DISPLAY_RES);

		return rslt;

	}

	/**
	 * En resolution d'impression
	 * 
	 * @return
	 */
	public Rectangle getTrueImageableRect() {
		return new Rectangle2D.Double(getImageableX(), getImageableY(),
				getImageableWidth(), getImageableHeight()).getBounds();
	}

	public boolean isAssemblyPage() {
		return assemblyPage;
	}

	public void setAssemblyPage(boolean val) {
		this.assemblyPage = val;
	}

	public Rectangle getBoundsOnMap() {

		Rectangle rect = new Rectangle();

		// position
		rect.x = positionOnMap.x;
		rect.y = positionOnMap.y;

		rect.setSize(format.getPixelDimensions(JAVA_DISPLAY_RES));

		return rect;
	}

	public void refreshSheet() {
		// TODO Auto-generated method stub

	}

	@Override
	public MementoManager<PropertiesContainer> getMementoManager() {
		return canceler;
	}

	public PageFormat getPageFormat() {
		PageFormat pf = new PageFormat();
		pf.setPaper(this);
		return pf;
	}

	@Override
	public int print(Graphics paramGraphics, PageFormat paramPageFormat,
			int paramInt) throws PrinterException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		LayoutProperties pp = (LayoutProperties) properties;

		this.assemblyPage = pp.assemblyPage;
		this.displayPageNumbers = pp.displayPageNumbers;
		this.displayScale = pp.displayScale;
		this.displayTitle = pp.displayTitle;

		this.margins = new LayoutMargins();
		margins.setProperties(pp.margins);

		this.format = LayoutFormat.getFormat(format.getMillimeterDimensions());
		if (format == null) {
			format = new LayoutFormat(LayoutFormat.CUSTOM_FORMAT_NAME,
					format.getMillimeterDimensions());
		}

		this.positionOnMap = new Point(pp.positionOnMap);
		this.mapScale = pp.mapScale;

	}

	@Override
	public PropertiesContainer getProperties() {

		LayoutProperties pp = new LayoutProperties();
		pp.assemblyPage = assemblyPage;
		pp.displayPageNumbers = displayPageNumbers;
		pp.displayScale = displayScale;
		pp.displayTitle = displayTitle;
		pp.margins = new LayoutMargins(margins).getProperties();
		pp.mmDimensions = new Dimension(format.getMillimeterDimensions());
		pp.positionOnMap = new Point(positionOnMap);
		pp.mapScale = mapScale;

		return pp;
	}

	/**
	 * Si vrai, la feuille doit être concernée par les modifications apportée
	 * par l'utilisateur ou le programme.
	 * 
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Si vrai, la feuille doit être concernée par les modifications apportée
	 * par l'utilisateur ou le programme.
	 * 
	 * @return
	 */
	public void setActive(boolean b) {
		this.active = b;
	}

	@Override
	public String toString() {

		Object[] values = new Object[] { this.active, this.assemblyPage,
				this.displayPageNumbers, this.displayScale, this.displayTitle,
				this.format, this.mapScale, this.positionOnMap,
				this.printResolution, };

		Object[] keys = new Object[] { "active", "assemblyPage",
				"displayPageNumbers", "displayScale", "displayTitle", "format",
				"mapScale", "positionOnMap", "printResolution", };

		return Utils.toString(this, keys, values);

	}

	public float getMapScale() {
		return mapScale;
	}

}
