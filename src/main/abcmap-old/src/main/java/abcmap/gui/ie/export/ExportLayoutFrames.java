package abcmap.gui.ie.export;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.metadata.IIOMetadataNode;

import abcmap.configuration.ConfigurationConstants;
import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.utils.Utils;
import abcmap.utils.gui.GuiUtils;

public class ExportLayoutFrames extends InteractionElement {

	public ExportLayoutFrames() {
		this.label = "Exporter les cadres de mise en page.";
		this.help = "Cliquez ici pour exporter les cadres de mise en page dans un dossier. "
				+ "Pour exporter vous devrez d'abord mettre en page votre projet.";
	}

	@Override
	public void run() {

		// pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// Verifier le projet et obtenir le calque actif, ou afficher un
		// message d'erreur
		MapLayer layer = checkProjectAndGetActiveLayer();
		if (layer == null) {
			return;
		}

		// eviter les appels intempestifs
		if (threadAccess.askAccess() == false) {
			return;
		}

		// demander le dossier de destination
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog
				.browseDirectoryAndWait(parent);

		// quitter si l'utilisateur annule
		if (result.isActionCanceled()) {
			threadAccess.releaseAccess();
			return;
		}

		// verifier le dossier
		File directoryWhereExport = result.getFile();
		if (directoryWhereExport.isDirectory() == false) {
			guim.showErrorInBox("Répertoire d'export non valide.");
			threadAccess.releaseAccess();
			return;
		}

		// list ede cadre a exporter
		ArrayList<LayoutPaper> sheets = projectm.getLayouts();

		// pas de cadres a exporter, message d'erreur
		if (sheets.size() < 1) {
			guim.showProjectWithoutLayoutError();
			threadAccess.releaseAccess();
		}

		// résolution par défaut pour l'export
		configm.setPrintResolution(ConfigurationConstants.DEFAULT_PRINT_RESOLUTION);

		// iteration des feuilles
		int i = 1;
		Integer errors = 0;
		for (LayoutPaper lay : sheets) {

			// creer une image a partir de la feuille
			Dimension dim = lay.getPixelDimensions();
			BufferedImage bi = new BufferedImage(dim.width, dim.height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bi.createGraphics();

			// dessin de la carte
			try {
				lay.print(g2d, null, i);
			} catch (PrinterException e) {
				Log.error(e);
			}

			// enregistrement de l'image
			File destination = new File(directoryWhereExport.getAbsolutePath()
					+ File.separator + i + ".png");

			// métadonnees de l'image
			IIOMetadataNode metadatas = Utils.getMetadatasForResolution(configm
					.getPrintResolution());

			// ecriture de l'image dans le dossier
			try {
				Utils.writePngImage(destination, bi, metadatas);

				// verification du fichier resultat
				if (destination.isFile() == false)
					errors++;

			} catch (IOException e) {
				// comptabilisation des erreurs
				errors++;
			}

			// message de rapport d'erreur
			if (errors > 0) {
				guim.showErrorInBox(errors
						+ " erreur(s) ont eu lieu pendant l'export.");
			}

			i++;
		}

		threadAccess.releaseAccess();
	}

}
