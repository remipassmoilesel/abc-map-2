package oldtrys.datas;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PdfBoxDimensions {

	public static void main(String[] args) {

		// Initialisation.init(args);
		// Initialisation.launchGui();

		sandBox(args);

		// AB_TimeComputingArea.launch();

		// AC_DrawingArea.launch();

		// AD_SwingTestArea.launch();

	}

	private static void sandBox(String[] args) {
		// ouvrir le document
		PDDocument document;
		try {
			document = PDDocument.loadNonSeq(new File("test_pdf.pdf"), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		List<PDPage> pages = document.getDocumentCatalog().getAllPages();
		int i = 0;
		for (PDPage page : pages) {

			i++;
			System.out.println();
			System.out.println("Page #" + i);

			System.out.println("getDimensionFrom");
			System.out.println(getDimensionFrom(page));

			System.out.println("page.getMediaBox()");
			System.out.println(page.getMediaBox());
			System.out.println("page.getCropBox()");
			System.out.println(page.getCropBox());
			System.out.println("page.getBleedBox()");
			System.out.println(page.getBleedBox());
			System.out.println("page.findCropBox()");
			System.out.println(page.findCropBox());
		}

		String layouts = document.getDocumentCatalog().getPageLayout();

		System.out.println(layouts);

		try {
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retourne les dimensions d'une page en pixels, 72dpi.
	 */
	private static Dimension getDimensionFrom(PDPage page) {

		// recherche la taille du média auxquel est destiné la page
		// ne peut pas retourner null.
		PDRectangle rect = page.findMediaBox();

		return new Dimension(Math.round(rect.getWidth()), Math.round(rect.getHeight()));

	}
}
