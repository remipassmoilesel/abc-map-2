package trys;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import abcmap.draw.styles.BackgroundRenderer;
import abcmap.draw.styles.Texture;
import abcmap.utils.sandbox.DrawingTestFrame;
import abcmap.utils.sandbox.DrawingTestFrame.DrawingProcess;

public class AC_DrawingArea implements DrawingProcess {

	public static void launch() {
		DrawingTestFrame.show(new AC_DrawingArea());
	}

	@Override
	public void draw(Graphics2D g2d) {

		Polygon r = new Polygon(new int[] { 20, 200, 60 }, new int[] { 20, 50, 200 }, 3);

		// rendu du fond
		BackgroundRenderer.fill(g2d, r, Texture.HLINES_OBLIQUE_LEFT, Color.BLUE);

		// rendu du contour
		g2d.setColor(Color.red);
		g2d.draw(r);

	}

}
