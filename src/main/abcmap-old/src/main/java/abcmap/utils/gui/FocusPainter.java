package abcmap.utils.gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import abcmap.gui.GuiColors;
import abcmap.managers.DrawManager;

/**
 * Peint un composant en fonction de son état, sous focus ou non.
 * 
 * @author remipassmoilesel
 *
 */

public class FocusPainter {

	/**
	 * La couleur du composant lorsqu'il n'est pas sous focus
	 */
	private Color nonFocusedColor;

	/**
	 * La couleur du composant sous focus
	 */
	private Color focusedColor;

	/**
	 * Marge en pixel entre le rectangle coloré de sélection et le bord du
	 * composant
	 */
	private int focusPaintMargins;

	public FocusPainter() {
		this.focusPaintMargins = 3;
	}

	public FocusPainter(Color nonFocus) {
		this.nonFocusedColor = nonFocus;
		this.focusedColor = GuiColors.FOCUS_COLOR_BACKGROUND;
	}

	public FocusPainter(Color nonFocus, Color focus) {
		this.nonFocusedColor = nonFocus;
		this.focusedColor = focus;
	}

	public void draw(Graphics g, Component comp, boolean focused) {

		Graphics2D g2d = (Graphics2D) g;

		// recuperer la zone à dessiner
		Rectangle r = new Rectangle(comp.getSize());

		r.x -= focusPaintMargins;
		r.y -= focusPaintMargins;
		r.width += focusPaintMargins * 2;
		r.height += focusPaintMargins * 2;

		// effacer le fond
		g2d.clearRect(r.x, r.y, r.width, r.height);

		// dessiner un element avec focus
		if (focused) {

			// nouveau graphics pour fond transparent
			Graphics2D g2dT = (Graphics2D) g.create();

			float alpha = 0.2f;
			int type = AlphaComposite.SRC_OVER;
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			g2dT.setComposite(composite);

			// fond bleu transparent
			Color co = GuiColors.FOCUS_COLOR_BACKGROUND;
			g2dT.setColor(co);
			g2dT.fillRect(r.x, r.y, r.width, r.height);

			// cadre bleu
			g2d.setColor(focusedColor);
			g2d.setStroke(GuiColors.FOCUS_STROKE);
			int t = GuiColors.FOCUS_STROKE_THICKNESS;
			g2d.drawRect(r.x, r.y, r.width - t, r.height - t);

		}

		// dessiner un element sans focus
		else {
			g2d.setColor(nonFocusedColor);
			g2d.fillRect(r.x, r.y, r.width, r.height);
		}

	}

	public void setNonFocusedColor(Color c) {
		this.nonFocusedColor = c;
	}

	public void setFocusedColor(Color c) {
		this.focusedColor = c;
	}

}
