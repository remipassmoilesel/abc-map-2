package abcmap.project.utils;

import java.awt.Color;
import java.util.ArrayList;

import abcmap.draw.DrawConstants;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Symbol;
import abcmap.draw.shapes.Tile;
import abcmap.draw.styles.LineStyle;
import abcmap.draw.styles.Texture;
import abcmap.geo.GeoInfoMode;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;

/**
 * Classe utilitaire permettant de modifier des elements de calque à la chaine
 * 
 * @author remipassmoilesel
 *
 */
public class ShapeUpdater extends LayerSequentialPerformer {

	protected boolean modificationsHappened;
	protected ArrayList<LayerElement> toCancel;
	protected PropertiesContainer properties;
	protected DrawConstants mode;

	public ShapeUpdater() {

		// par defaut ne concerne que les elements selectionnes
		setOnlySelectedElements(true);

		this.properties = null;
		this.mode = null;
	}

	public ShapeUpdater(DrawConstants mode, ShapeProperties pp) {
		this.properties = pp;
		this.mode = mode;
	}

	/**
	 * An cas de surcharge laisser obligatoirement
	 * super.beforeBeginModifications();
	 */
	@Override
	protected void beforeBeginUpdate() {
		super.beforeBeginUpdate();

		// preparation de l'operation d'annulation
		toCancel = new ArrayList<LayerElement>();

		// verifier qu'une modification ai bien eu lieu
		modificationsHappened = false;
	}

	@Override
	protected void updateLayerElement(LayerElement elmt) {

		if (elmt == null)
			throw new NullPointerException();

		// verifier qu'une modification ai bien eu lieu
		if (modificationsHappened == false)
			modificationsHappened = true;

		// sauvegarde pour annuler / refaire
		elmt.getMementoManager().saveStateToRestore();

		// l'element a transferer concerne les couleurs
		if (properties instanceof DrawPropertiesContainer) {
			updateDrawProperties(mode, (DrawPropertiesContainer) properties, elmt);
		}

		// modifier le lien
		else if (DrawConstants.MODIFY_LINK.equals(mode)) {
			updateGeneralProperties(mode, (ShapeProperties) properties, elmt);
		}

		// sinon concerne des formes
		else if (elmt instanceof Label) {
			updateLabel(mode, (ShapeProperties) properties, (Label) elmt);
		}

		else if (elmt instanceof PolypointShape) {
			updatePolypointShape(mode, (ShapeProperties) properties, (PolypointShape) elmt);
		}

		else if (elmt instanceof RectangleShape) {
			updateRectangleShape(mode, (ShapeProperties) properties, (RectangleShape) elmt);
		}

		else if (elmt instanceof Symbol) {
			updateSymbol(mode, (ShapeProperties) properties, (Symbol) elmt);
		}

		else if (elmt instanceof Tile) {
			updateTile(mode, (ShapeProperties) properties, (Tile) elmt);
		}

		// rafraichir la forme
		elmt.refreshShape();

		// sauvegarde pour annuler / refaire
		elmt.getMementoManager().saveStateToRedo();

		// verifier si la liste est nulle
		if (toCancel == null) {
			// la liste est nulle: il manque surement
			// super.beforeBeginModifications(); dans l'override
			throw new NullPointerException(
					"List 'toCancel' have not been initialized. See : super.beforeBeginModifications();");

		}
		toCancel.add(elmt);
	}

	@Override
	protected void updatesAreDone() {

		// ne continuer que si une modification a eu lieu
		if (modificationsHappened) {

			// enregistrement de l'operation d'annulation
			cancelm.addDrawOperation(activeLayer, toCancel);

			// mise à jour
			projectm.fireElementsChanged();
			mapm.refreshMapComponent();
		}
	}

	public void setProperties(PropertiesContainer properties) {
		this.properties = properties;
	}

	public void setMode(DrawConstants mode) {
		this.mode = mode;
	}

	public static void updateDrawProperties(DrawConstants mode, DrawPropertiesContainer pp,
			LayerElement elmt) {

		// recuperer les proprietes de l'element
		DrawProperties baseStroke = elmt.getStroke();

		if (DrawConstants.MODIFY_FG_COLOR.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			baseStroke.setFgColor(pp.fgColor);
		}
		if (DrawConstants.MODIFY_BG_COLOR.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			baseStroke.setBgColor(pp.bgColor);
		}
		if (DrawConstants.MODIFY_LINESTYLE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			baseStroke.setLineStyle(LineStyle.safeValueOf(pp.linestyle));
		}
		if (DrawConstants.MODIFY_LINESTYLE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			baseStroke.setThickness(pp.thickness);
		}
		if (DrawConstants.MODIFY_TEXTURE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			baseStroke.setTexture(Texture.safeValueOf(pp.texture));
		}

		// appliquer les changements
		elmt.setStroke(baseStroke);

	}

	public static void updateLabel(DrawConstants mode, ShapeProperties pp, Label shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		if (DrawConstants.MODIFY_FONT_FAMILY.equals(mode)
				|| DrawConstants.MODIFY_ALL.equals(mode)) {
			String ff = pp.font;
			shp.setFontFamily(ff);
		}

		if (DrawConstants.MODIFY_TEXT_SIZE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setFontSize(pp.size);
		}

		if (DrawConstants.MODIFY_TEXT_COLOR.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Color fc = pp.stroke.fgColor;
			DrawProperties st = shp.getTextStroke();
			st.setFgColor(fc);
			shp.setStroke(st);
		}

		if (DrawConstants.MODIFY_BOLD.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Boolean fb = pp.bold;
			shp.setBold(fb);
		}

		if (DrawConstants.MODIFY_ITALIC.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Boolean fi = pp.italic;
			shp.setItalic(fi);
		}

		if (DrawConstants.MODIFY_STRIKE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Boolean fs = pp.strikethrough;
			shp.setStrikethrough(fs);
		}

		if (DrawConstants.MODIFY_UNDERLINE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Boolean fu = pp.underlined;
			shp.setUnderlined(fu);
		}

		if (DrawConstants.MODIFY_TEXT_MODE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setGeoInfoMode(new GeoInfoMode(pp.geoInfoMode));
		}

		if (DrawConstants.MODIFY_BORDER.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setBorderActivated(pp.borderActivated);
		}

	}

	public static void updatePolypointShape(DrawConstants mode, ShapeProperties pp,
			PolypointShape shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		if (DrawConstants.MODIFY_GEOTEXTMODE.equals(mode)
				|| mode.equals(DrawConstants.MODIFY_ALL)) {
			shp.setGeoInfoMode(new GeoInfoMode(pp.geoInfoMode));
		}

		if (DrawConstants.MODIFY_TEXT_SIZE.equals(mode) || mode.equals(DrawConstants.MODIFY_ALL)) {
			shp.setGeoTextSize(pp.geoInfoSize);
		}

		if (DrawConstants.MODIFY_BEGIN_ARROW.equals(mode)
				|| mode.equals(DrawConstants.MODIFY_ALL)) {
			shp.setBeginWithArrow(pp.beginWithArrow);
		}

		if (DrawConstants.MODIFY_END_ARROW.equals(mode) || mode.equals(DrawConstants.MODIFY_ALL)) {
			shp.setEndWithArrow(pp.endWithArrow);
		}

		if (DrawConstants.MODIFY_ARROW_FORCE.equals(mode)
				|| mode.equals(DrawConstants.MODIFY_ALL)) {
			shp.setArrowForce(pp.arrowForce);
		}

	}

	public static void updateSymbol(DrawConstants mode, ShapeProperties pp, Symbol shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		if (DrawConstants.MODIFY_SYMBOL_CODE.equals(mode)
				|| DrawConstants.MODIFY_ALL.equals(mode)) {

			shp.setSymbolSetName(pp.symbolSetName);
			shp.setSymbolCode(pp.symbolCode);
		}

		if (DrawConstants.MODIFY_SIZE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			Integer size = pp.size;
			shp.setSize(size);
		}

	}

	public static void updateTile(DrawConstants mode, ShapeProperties pp, Tile shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		// TODO

	}

	private void updateRectangleShape(DrawConstants mode, ShapeProperties pp, RectangleShape shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		if (DrawConstants.MODIFY_GEOTEXTMODE.equals(mode)
				|| DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setGeoInfoMode(new GeoInfoMode(pp.geoInfoMode));
		}

		if (DrawConstants.MODIFY_SIZE.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setGeoTextSize(pp.geoInfoSize);
		}

	}

	private void updateGeneralProperties(DrawConstants mode, ShapeProperties pp, LayerElement shp) {

		if (mode == null || pp == null || shp == null)
			throw new NullPointerException();

		if (DrawConstants.MODIFY_LINK.equals(mode) || DrawConstants.MODIFY_ALL.equals(mode)) {
			shp.setLinkRessource(pp.linkRessource);
		}

	}

}
