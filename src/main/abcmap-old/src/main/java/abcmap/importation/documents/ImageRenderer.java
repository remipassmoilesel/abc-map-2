package abcmap.importation.documents;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class ImageRenderer extends AbstractDocumentRenderer {

	/** Formats supportés, mis à jour au premier accès */
	private static String[] supportedFormats = null;

	@Override
	public String[] getSupportedExtensions() {

		// lister les formats si nécéssaire
		if (supportedFormats == null) {
			supportedFormats = Utils.getAllImageSupportedFormats();
		}

		return supportedFormats;
	}

	@Override
	public BufferedImage[] render(File file) throws IOException {

		// Dimensions originales du fichiers
		Dimension originDims = getDocumentDimensions(file)[0];

		// dimensions finales
		Dimension finalDims = new Dimension();
		finalDims.width = Math.round(originDims.width * factor);
		finalDims.height = Math.round(originDims.height * factor);

		// lire l'image originale
		BufferedImage originalImage = ImageIO.read(file);

		// creer l'image finale
		BufferedImage resizedImage = new BufferedImage(finalDims.width, finalDims.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();

		// améliorer la qualité du rendu
		g.setComposite(AlphaComposite.Src);
		GuiUtils.applyQualityRenderingHints(g);

		// dessiner l'image
		g.drawImage(originalImage, 0, 0, finalDims.width, finalDims.width, null);
		g.dispose();

		return new BufferedImage[] { resizedImage };

	}

	@Override
	public Dimension[] getDocumentDimensions(File file) throws IOException {
		return new Dimension[] { Utils.getImageDimensions(file) };
	}

}
