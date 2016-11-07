package abcmap.gui.windows.crop;

import java.awt.Color;
import java.awt.Graphics2D;

import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.shapes.Rectangle;

/**
 * Rectangle de selection de dimensions de recadrage
 * 
 * @author remipassmoilesel
 *
 */
public class CropSelectionRectangle extends Rectangle {

	public CropSelectionRectangle() {
		super();

		// couleur du rectangle
		this.stroke = new DrawProperties();
		stroke.setFgColor(Color.green);
		stroke.setThickness(5);

		setSelected(true);

	}

	public void setColor(Color color) {
		stroke.setFgColor(color);
	}

	/**
	 * Dessin du rectangle sans le cadre de selection
	 */
	@Override
	public void draw(Graphics2D g, String mode) {

		g.setColor(stroke.getFgColor());
		g.setStroke(stroke.getSwingStroke());
		g.draw(bounds);

		// dessin des poignées
		for (Handle h : handles) {
			h.draw(g);
		}
	}

}
