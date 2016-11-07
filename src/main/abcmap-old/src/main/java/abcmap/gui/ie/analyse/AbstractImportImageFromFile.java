package abcmap.gui.ie.analyse;

import java.awt.Window;
import java.io.IOException;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Tile;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.exceptions.MapLayerException;
import abcmap.gui.dialogs.simple.BrowseDialogResult;
import abcmap.gui.dialogs.simple.BrowseFileFilter;
import abcmap.gui.dialogs.simple.SimpleBrowseDialog;
import abcmap.gui.ie.InteractionElement;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;
import abcmap.utils.gui.GuiUtils;

public abstract class AbstractImportImageFromFile extends InteractionElement {

	public enum Mode {
		TILE_MODE, IMAGE_MODE,
	}

	private Mode mode;

	public AbstractImportImageFromFile(Mode mode) {

		this.mode = mode;

		if (Mode.TILE_MODE.equals(mode)) {
			this.label = "Insérer une tuile à partir d'un fichier...";
			this.help = "Cliquez ici pour choisir un fichier puis "
					+ "l'importer dans votre carte en tant que tuile.";
		}

		else if (Mode.IMAGE_MODE.equals(mode)) {
			this.label = "Insérer une image à partir d'un fichier...";
			this.help = "Cliquez ici pour choisir un fichier puis "
					+ "l'importer dans votre carte en tant que tuile.";
		}

	}

	@Override
	public void run() {

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
			if (Mode.TILE_MODE.equals(mode)) {
				Tile tl = new Tile();
				tl.loadAndSaveImage(result.getFile(), null);
				elmt = tl;
			}

			else if (Mode.IMAGE_MODE.equals(mode)) {
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
		if (Mode.TILE_MODE.equals(mode)) {
			drawm.setCurrentTool(ToolLibrary.TILE_TOOL);
		}

		else if (Mode.IMAGE_MODE.equals(mode)) {
			drawm.setCurrentTool(ToolLibrary.IMAGE_TOOL);
		}

		// enregistrement de l'opération pour annulation
		ElementsCancelOp op = cancelm.addDrawOperation(layer, elmt);
		op.elementsHaveBeenAdded(true);

	}

}
