package abcmap.draw.basicshapes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Arrays;

import abcmap.draw.styles.LineStyle;
import abcmap.draw.styles.Texture;
import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.DrawPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;

public class DrawProperties implements AcceptPropertiesContainer {

	public static final String FOREGROUND = "FOREGROUND";
	public static final String BACKGROUND = "BACKGROUND";

	private int thickness;
	private Color color;
	private BasicStroke stroke;
	private Color fill;
	private LineStyle linestyle;
	private Texture texture;

	public DrawProperties() {
		this.thickness = 5;
		this.color = Color.black;
		this.fill = null;
		this.texture = Texture.PLAIN;
		setLineStyle(LineStyle.LINE_STROKE);
	}

	public DrawProperties(DrawProperties stroke2) {
		this.thickness = stroke2.thickness;
		this.color = stroke2.color;
		this.fill = stroke2.fill;
		this.texture = stroke2.texture;
		setLineStyle(stroke2.linestyle);
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof DrawProperties == false)
			return false;

		DrawProperties str = (DrawProperties) obj;

		Object[] toCompare1 = new Object[] { this.texture, this.color, this.fill, this.stroke,
				this.thickness, };
		Object[] toCompare2 = new Object[] { str.texture, str.color, str.fill, str.stroke,
				str.thickness, };

		return Arrays.deepEquals(toCompare1, toCompare2);

	}

	/**
	 * Créer un objet de proprietes de dessin à partir des proprietes fournies
	 * en parametre <br>
	 * Ou un objet vierge si les proprietes sont nulles.
	 * 
	 * @param pp
	 * @return
	 */
	public static DrawProperties createNewWith(DrawPropertiesContainer pp) {
		DrawProperties ds = new DrawProperties();
		if (pp != null)
			ds.setProperties(pp);
		return ds;
	}

	public void setLineStyle(LineStyle style) {

		if (style == null)
			throw new NullPointerException();

		this.linestyle = style;
		this.stroke = style.getSwingStroke(thickness);
	}

	public int getThickness() {
		return thickness;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
		setLineStyle(linestyle);
	}

	public Color getFgColor() {
		return color;
	}

	public void setFgColor(Color color) {
		this.color = color;
	}

	public BasicStroke getSwingStroke() {
		return stroke;
	}

	public Color getBgColor() {
		return fill;
	}

	public LineStyle getLinestyle() {
		return linestyle;
	}

	public void setBgColor(Color fill) {
		this.fill = fill;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	@Override
	public void setProperties(PropertiesContainer properties) {

		DrawPropertiesContainer pp = (DrawPropertiesContainer) properties;
		this.thickness = pp.thickness;
		this.color = pp.fgColor;
		this.fill = pp.bgColor;
		this.texture = Texture.safeValueOf(pp.texture);

		// action à appliquer en dernier
		LineStyle ls = LineStyle.safeValueOf(pp.linestyle);

		setLineStyle(ls);
	}

	@Override
	public PropertiesContainer getProperties() {

		DrawPropertiesContainer pp = new DrawPropertiesContainer();
		pp.thickness = this.thickness;
		pp.fgColor = this.color;
		pp.bgColor = this.fill;
		pp.linestyle = this.linestyle.toString();
		pp.texture = texture.toString();

		return pp;
	}

	public int getHalfThickness() {
		return thickness / 2;
	}

}
