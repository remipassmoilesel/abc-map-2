package org.abcmap.ielements.analyse;

import org.abcmap.ielements.InteractionElement;

public abstract class AbstractImportImageFromFile extends InteractionElement {

    private AnalyseMode mode;

    public AbstractImportImageFromFile(AnalyseMode mode) {

        this.mode = mode;

        if (AnalyseMode.TILE_MODE.equals(mode)) {
            this.label = "Insérer une tuile à partir d'un fichier...";
            this.help = "Cliquez ici pour choisir un fichier puis "
                    + "l'importer dans votre carte en tant que tuile.";
        } else if (AnalyseMode.IMAGE_MODE.equals(mode)) {
            this.label = "Insérer une image à partir d'un fichier...";
            this.help = "Cliquez ici pour choisir un fichier puis "
                    + "l'importer dans votre carte en tant que tuile.";
        }

    }

    @Override
    public void run() {

		/*
        // pas de lancement dans l'EDT
		GuiUtils.throwIfOnEDT();

		// projet non initialisé, retour
		if (checkProjectAndGetActiveLayer() == null) {
			return;
		}

		// boite parcourir de choix de fichier
		Window parent = guim.getMainWindow();
		BrowseDialogResult result = SimpleBrowseDialog.browseFileToOpenAndWait(
				parent, BrowseFileFilter.PICTURES_FILEFILTER);

		// l'utilisateur annule: arret
		if (BrowseDialogResult.CANCEL.equals(result.getReturnVal()))
			return;

		// ouverture
		LayerElement elmt = null;
		try {
			if (AnalyseMode.TILE_MODE.equals(mode)) {
				Tile tl = new Tile();
				tl.loadAndSaveImage(result.getFile(), null);
				elmt = tl;
			}

			else if (AnalyseMode.IMAGE_MODE.equals(mode)) {
				Image img = new Image();
				img.loadAndSaveImage(result.getFile());
				elmt = img;
			}

		} catch (IOException e) {
			Log.error(e);
			elmt = null;
		}

		// element null -> message d'erreur puis arret
		if (elmt == null) {
			guim.showErrorInBox("Erreur lors de l'import de l'image.");
			return;
		}

		// deselectionner tous les elements du calque actif
		projectm.setAllElementsSelected(false);

		// otenir le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			return;
		}

		// selectionner l'element qui vient d'être ajouté
		elmt.setSelected(true);

		// ajout de l'element
		layer.addElement(elmt);

		// selection de l'outil adapté
		if (AnalyseMode.TILE_MODE.equals(mode)) {
			drawm.setCurrentTool(ToolLibrary.TILE_TOOL);
		}

		else if (AnalyseMode.IMAGE_MODE.equals(mode)) {
			drawm.setCurrentTool(ToolLibrary.IMAGE_TOOL);
		}

		// enregistrement de l'opération pour annulation
		ElementsCancelOp op = cancelm.addDrawOperation(layer, elmt);
		op.elementsHaveBeenAdded(true);
		*/
    }

}
