package abcmap.importation.documents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import abcmap.managers.Log;

public class SvgRenderer extends AbstractDocumentRenderer {

	@Override
	public String[] getSupportedExtensions() {
		return new String[] { "svg" };
	}

	@Override
	public Dimension[] getDocumentDimensions(File file) throws IOException {

		// charger le document
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		FileReader stream = new FileReader(file);
		SVGDocument doc = factory.createSVGDocument(file.getAbsolutePath(), stream);

		// taille par défaut du document
		// les valeurs svg sont décimales
		float width = 400;
		float height = 400;

		Throwable exception1 = null;
		Throwable exception2 = null;

		// tenter de récupérer les attributs width et height
		try {
			width = Float.valueOf(doc.getDocumentElement().getAttribute("width"));
			height = Float.valueOf(doc.getDocumentElement().getAttribute("height"));
		}

		catch (Exception e) {

			// conserver la référence de l'erreur
			exception1 = e;

			// ou tenter de récupérer l'attribut viewbox
			try {
				String[] viewBoxValues = doc.getDocumentElement().getAttribute("viewBox")
						.split(" +");

				width = Float.valueOf(viewBoxValues[2]);
				height = Float.valueOf(viewBoxValues[3]);
			}

			catch (Exception e2) {
				// conserver la référence de l'erreur
				exception2 = e;
			}
		}

		// journalisation des erreurs si besoin
		if (exception1 != null && exception2 != null) {
			Log.error(exception1);
			Log.error(exception2);
		}

		// ajouter le facteur d'agrandissement
		width = (int) (width * factor);
		height = (int) (height * factor);

		// fermer la ressource
		stream.close();

		return new Dimension[] { new Dimension(Math.round(width), Math.round(height)) };
	}

	@Override
	public BufferedImage[] render(File file) throws IOException {

		Dimension dim = getDocumentDimensions(file)[0];

		// charger le document
		FileReader reader = new FileReader(file);
		TranscoderInput svgImage = new TranscoderInput(reader);

		// l'objet de transformation d'image
		BufferedImageTranscoder transcoder = new BufferedImageTranscoder();

		// ajouter les informations de dimensions
		// les clefs de transcoders doivent être des Float
		transcoder.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(dim.width));
		transcoder.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(dim.height));
		transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1f));
		try {
			transcoder.transcode(svgImage, null);
		} catch (TranscoderException e) {
			Log.error(e);
			throw new IOException("Unable to load image.");
		}

		return new BufferedImage[] { transcoder.getImage() };

	}

	private class BufferedImageTranscoder extends JPEGTranscoder {

		private BufferedImage image;

		@Override
		public void writeImage(BufferedImage img, TranscoderOutput output)
				throws TranscoderException {
			this.image = img;
		}

		public BufferedImage getImage() {
			return image;
		}

	}

}
