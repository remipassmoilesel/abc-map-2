package abcmap.importation.documents;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import abcmap.managers.Log;

/**
 * Insérer une page de PDF. Si aucune page n'est spécifiée, la première page est
 * insérée.
 * 
 * @author remipassmoilesel
 *
 */
public class PdfRenderer extends AbstractDocumentRenderer {

	@Override
	public String[] getSupportedExtensions() {
		return new String[] { "pdf" };
	}

	@Override
	public Dimension[] getDocumentDimensions(File file) throws IOException {

		// ouvrir le document
		PDDocument document;
		try {
			document = PDDocument.loadNonSeq(file, null);
		} catch (IOException e) {
			Log.error(e);
			throw new IOException("Unable to load file: " + file);
		}

		// lister les pages à importer.
		ArrayList<PDPage> pagesToImport;
		try {
			pagesToImport = getPagesToImport(document);
		} finally {
			document.close();
		}

		// le résultat à retourner
		Dimension[] dimensions = new Dimension[pagesToImport.size()];

		int i = 0;
		for (PDPage page : pagesToImport) {
			dimensions[i] = getDimensionsFrom(page);
			i++;
		}

		// fermer le document
		document.close();

		return dimensions;
	}

	@Override
	public BufferedImage[] render(File file) throws IOException {

		// ouvrir le document
		PDDocument document;
		try {
			document = PDDocument.loadNonSeq(file, null);
		} catch (IOException e) {
			Log.error(e);
			throw new IOException("Unable to load file: " + file);
		}

		// lister les pages à importer.
		ArrayList<PDPage> pages;
		try {
			pages = getPagesToImport(document);
		} finally {
			document.close();
		}

		// le résultat à retourner
		BufferedImage[] images = new BufferedImage[pages.size()];

		// calculer la résolution en fonction du facteur demandé
		int dpi = Math.round(72 * factor);

		// iterer les pages et convertir en image
		int i = 0;
		for (PDPage page : pages) {

			// convertir l'image
			images[i] = page.convertToImage(BufferedImage.TYPE_INT_RGB, dpi);

			i++;
		}

		// fermer le document
		document.close();

		return images;
	}

	/**
	 * Retourne les dimensions d'une page en pixels, 72dpi.
	 * 
	 * @param page
	 * @return
	 */
	private Dimension getDimensionsFrom(PDPage page) {

		// recherche la taille du média auxquel est destiné la page
		// ne peut pas retourner null.
		PDRectangle rect = page.findMediaBox();

		return new Dimension(Math.round(rect.getWidth()), Math.round(rect.getHeight()));

	}

	/**
	 * Retourne la liste des pages à importer en fonction de des numéros de
	 * pages passés en paramètres.
	 * 
	 * @param document
	 * @return
	 * @throws IOException
	 */
	private ArrayList<PDPage> getPagesToImport(PDDocument document) throws IOException {

		// lister toutes les pages disponibles
		List<PDPage> allPages = document.getDocumentCatalog().getAllPages();
		if (allPages.size() < 1) {
			throw new IOException("Empty document");
		}

		// lister les pages à importer.
		ArrayList<PDPage> pagesToImport = new ArrayList<>();

		// importer toutes les pages
		if (Arrays.asList(pageNumbersToImport).contains(0)) {
			pagesToImport.addAll(allPages);
		}

		// importer seulement les numéros spécifiés
		else {
			for (Integer i : pageNumbersToImport) {
				try {
					// recuperer la page concernée, sachant que le compte
					// commence à zéro
					pagesToImport.add(allPages.get(i - 1));
				} catch (Exception e) {
					Log.error(e);
				}
			}
		}

		if (pagesToImport.size() < 1) {
			throw new IOException("Pages not found");
		}

		return pagesToImport;
	}

}
