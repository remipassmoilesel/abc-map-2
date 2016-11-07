package abcmap.draw.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.Arrays;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Drawable;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.symbols.SymbolImage;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;
import abcmap.project.properties.ShapeProperties;
import abcmap.utils.gui.GuiUtils;

public class Symbol extends LayerElement {

	private int symbolCode;
	private int size;
	private String setName;
	private SymbolImage syImage;

	private int margins;

	public Symbol() {

		// poignée de déplacement
		this.handles.add(new Handle(Handle.FOR_MOVING));

		// taille du symbole
		this.size = 100;

		// couleur et forme
		this.stroke = drawm.getNewStroke();

		// la representation du symbole
		this.syImage = null;

		// marges autour de l'image
		this.margins = 3;

		// symbol par defaut
		this.setName = drawm.getAvailableSymbolSets().get(0);
		this.symbolCode = drawm.getAvailableSymbolCodesFor(setName).get(0);

		refreshShape();
	}

	public Symbol(Symbol symb) {
		this();
		setProperties(symb.getProperties());

		refreshShape();
	}

	@Override
	public boolean equals(Object o) {

		if (o instanceof Symbol == false)
			return false;

		Symbol shp = (Symbol) o;

		Object[] toCompare1 = new Object[] { this.size, this.symbolCode, this.stroke, this.selected,
				this.setName, this.maximumBounds, };

		Object[] toCompare2 = new Object[] { shp.size, shp.symbolCode, shp.stroke, shp.selected,
				shp.setName, shp.maximumBounds, };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	@Override
	public void refreshShape() {

		// image nulle, recuperer l'image
		if (syImage == null) {
			syImage = drawm.getSymbolImage(setName, symbolCode, size, stroke.getFgColor());
		}

		// adapter les dimensions
		maximumBounds.x = position.x;
		maximumBounds.y = position.y;

		maximumBounds.width = syImage.getWidth() + margins * 2;
		maximumBounds.height = syImage.getHeight() + margins * 2;

		// zone d'interaction
		int iam = drawm.getInteractionAreaMargin();
		Rectangle interBnds = new Rectangle(maximumBounds);
		interBnds.x -= iam;
		interBnds.y -= iam;
		interBnds.width += iam * 2;
		interBnds.height += iam * 2;
		interactionArea = new Area(interBnds);

		// positionner la poigne
		handles.get(0).setPosition(position);
		refreshHandles();

	}

	@Override
	public void draw(Graphics2D g, String mode) {

		if (syImage == null)
			return;

		// peindre l'image du symbole
		g.drawImage(syImage.getImage(), position.x + margins, position.y + margins,
				syImage.getWidth(), syImage.getHeight(), null);

		// dessin de la zone d'interaction
		if (drawInteractionArea) {
			GuiUtils.fillArea(g, interactionArea);
		}

		// peindre les attributs de selection
		if (Drawable.RENDER_FOR_DISPLAYING.equals(mode) && isSelected()) {

			// cadre
			g.setColor(drawm.getSelectionColor());
			g.setStroke(drawm.getSelectionStroke());
			g.draw(maximumBounds);

			// poignée
			drawHandles(g);

		}

		// peindre le symbole de sélection
		drawLinkMark(g, isSelected());

	}

	/**
	 * Casse la reference vers l'image. Lors du prochain refreshShape, une
	 * nouvelle image sera demandée.
	 */
	private void resetImage() {
		syImage = null;
	}

	public void setSymbolCode(int code) {
		this.symbolCode = code;
		resetImage();
	}

	@Override
	public LayerElement duplicate() {
		return new Symbol(this);
	}

	public int getSymbolCode() {
		return symbolCode;
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		ShapeProperties pp = (ShapeProperties) properties;

		this.size = Integer.valueOf(pp.size);
		this.setName = pp.symbolSetName;
		this.symbolCode = pp.symbolCode;

		// position du symbole
		setPosition(pp.position);

		// lien
		setLinkRessource(pp.linkRessource);

		resetImage();
	}

	@Override
	public PropertiesContainer getProperties() {

		ShapeProperties pp = new ShapeProperties();

		pp.size = this.size;
		pp.symbolSetName = new String(setName);
		pp.symbolCode = symbolCode;
		pp.position = getPosition();
		pp.stroke = (DrawPropertiesContainer) stroke.getProperties();

		// lien
		pp.linkRessource = linkRessource;

		return pp;

	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		resetImage();
	}

	public String getSymbolSetName() {
		return setName;
	}

	public void setSymbolSetName(String setName) {
		this.setName = setName;
		resetImage();
	}

	@Override
	public void setStroke(DrawProperties stroke) {
		super.setStroke(stroke);
		resetImage();
	}

	@Override
	public LayerElement getSample(int maxWidth, int maxHeight) {

		// creer l'echantillon
		Symbol sample = (Symbol) this.duplicate();

		// dimensionner
		sample.setSize((int) (maxWidth * 0.8f));

		// valider leschangements
		sample.refreshShape();

		return sample;
	}

}
