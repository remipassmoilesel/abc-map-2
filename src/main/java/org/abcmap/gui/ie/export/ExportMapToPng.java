package org.abcmap.gui.ie.export;

import org.abcmap.gui.ie.InteractionElement;

public class ExportMapToPng extends InteractionElement {

    private boolean drawLayoutsFrames;

    public ExportMapToPng() {

        this.label = "Exporter la carte au format PNG.";
        this.help = "Cliquez ici pour exporter la carte dans son entier au format PNG.";

        // dessiner les cadres de mise en page
        // TODO: mettre une checkbox ?
        this.drawLayoutsFrames = false;

    }

    public void drawLayouts(boolean drawLayouts) {
        this.drawLayoutsFrames = drawLayouts;
    }

    @Override
    public void run() {

		/*
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

		// selection du dossier de destination
		final Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog.browseFileToSaveAndWait(
				parent, null);

		// annulation de l'utilisateur
		if (result.isActionCanceled()) {
			threadAccess.releaseAccess();
			return;
		}

		// creer l'image
		Dimension dim = projectm.getMapDimensions();
		BufferedImage bi = new BufferedImage(dim.width, dim.height,
				BufferedImage.TYPE_INT_ARGB);

		// recuperer l'objet de dessin
		Graphics2D g2d = bi.createGraphics();

		// peindre le fond de carte
		g2d.setPaint(projectm.getBackgroundColor());
		g2d.fill(new Rectangle(dim));

		// dessiner la carte
		ProjectRenderer pr = new ProjectRenderer(Drawable.RENDER_FOR_PRINTING);
		pr.drawLayoutFrames(drawLayoutsFrames);
		pr.render(g2d);

		try {
			// enregistrer temporairement l'image
			File tmp = new File(ConfigurationConstants.TEMP_PGRM_DIRECTORY
					+ File.separator + System.currentTimeMillis() + ".tmp");
			ImageIO.write(bi, "png", tmp);

			// deplacement
			Files.copy(tmp.toPath(), result.getFile().toPath(),
					StandardCopyOption.REPLACE_EXISTING);

			// suppression de l'ancien
			tmp.delete();

		} catch (IOException e) {
			guim.showErrorInBox("Erreur lors de l'export de l'image.");
			Log.error(e);
		}

		threadAccess.releaseAccess();

		*/
    }

}
