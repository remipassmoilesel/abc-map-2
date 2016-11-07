package abcmap.gui.comps.display;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Affiche une image dans une panneau DynamicDisplay.
 * <br>Créé pour debogage. 
 * @author remipassmoilesel
 *
 */
public class DrawableImage implements DrawablePanelElement {

	private BufferedImage image;

	public DrawableImage(String path) throws IOException {
		this.image = ImageIO.read(new File(path));
	}

	public DrawableImage(BufferedImage img) {
		this.image = img;
	}

	@Override
	public void render(Graphics2D g2d) {
		g2d.drawImage(image, 0, 0, null);
	}

	@Override
	public Dimension getDimensions() {
		return new Dimension(image.getWidth(), image.getHeight());
	}

}
