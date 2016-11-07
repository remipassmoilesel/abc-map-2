package abcmap.importation.tile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import abcmap.draw.shapes.Tile;
import abcmap.events.ImportEvent;
import abcmap.exceptions.MapLayerException;
import abcmap.exceptions.TileAnalyseException;
import abcmap.managers.CancelManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

/**
 * Classe complémentaire de TileMaker. Traite la file d'attente de TileMaker.
 * 
 * @author remipassmoilesel
 *
 */
class TileMakerListAnalyser implements Runnable {

	/** Si vrai arrête l'analyse */
	private boolean stopImport = false;

	private TileMaker parent;

	private ProjectManager projectm;
	private CancelManager cancelm;
	private TileAnalyser tileAnalyser;

	public TileMakerListAnalyser(TileMaker maker) {
		projectm = MainManager.getProjectManager();
		cancelm = MainManager.getCancelManager();

		this.parent = maker;
		this.tileAnalyser = new TileAnalyser();
	}

	public void stop() {
		stopImport = true;
	}

	public void start() {
		if (isWorking() == false) {
			stopImport = false;
			ThreadManager.runLater(this);
		}
	}

	@Override
	public void run() {

		// eviter les appels intempestifs
		if (ThreadAccessControl.get(0).askAccess() == false) {
			return;
		}

		// notifier les observateurs
		parent.fireEvent(ImportEvent.IMPORT_STARTED);

		// raz des flags
		stopImport = false;

		// récuperer la reference de la liste d'attente
		ArrayList<File> waitingList = parent.getWaitingList();

		// récupérer le calque actif au début de l'analyse
		MapLayer layer = null;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.error(e1);
			parent.fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			stopImport = true;
		}

		// itérer les images
		importing: while (waitingList.size() > 0 && stopImport == false) {

			// arret si projet non initialisé ou si suppression du calque
			if (projectm.isInitialized() == false
					|| projectm.isLayerBelongToProject(layer) == false) {

				parent.fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
				stopImport = true;
				break importing;
			}

			// récuperer l'image et l'enlever de la liste
			BufferedImage img;
			File tempFile = waitingList.remove(0);
			try {
				img = ImageIO.read(tempFile);
			} catch (IOException e1) {
				Log.error("Unable to read file: " + e1);
				parent.ioErrors++;
				parent.fireEvent(ImportEvent.EXCEPTION_HAPPENED);
				continue;
			}

			// construire une tuile
			Tile t = new Tile();
			try {
				t.loadAndSaveImage(img,
						parent.activateCropping ? parent.cropRectangle : null);
			}

			// erreur lors de la création
			catch (Exception e) {
				Log.error(e);
				parent.ioErrors++;
				parent.fireEvent(ImportEvent.EXCEPTION_HAPPENED);
				continue;
			}

			try {
				// determiner la position de la tuile et changer sa position
				tileAnalyser.analyseTileAndSetPosition(t);
				t.refreshShape();

				// ajout au calque actif
				layer.addElement(t);

				// enregistrement pour annulation
				cancelm.addDrawOperation(layer, t).elementsHaveBeenAdded(true);

				// statistiques
				parent.imported++;

				// notifications
				parent.fireEvent(ImportEvent.WAITING_LIST_CHANGED);
			}

			// impossible positionner la tuile
			catch (TileAnalyseException e) {
				Log.error(e);

				// statistiques
				parent.refused++;

				// notifications
				parent.fireEvent(ImportEvent.TILE_REFUSED);
			}

		}

		// l'import à été abandonné
		if (stopImport) {
			parent.fireEvent(ImportEvent.IMPORT_ABORTED);
		}

		// l'import s'est fini correctement
		else {
			parent.fireEvent(ImportEvent.IMPORT_FINISHED);
		}

		// supprimer les fichiers temporaires
		parent.cleanTempFiles();

		// raz des indicateurs de stats
		parent.resetIndicators();

		// arret du travail
		ThreadAccessControl.get(0).releaseAccess();

	}

	public boolean isWorking() {
		return ThreadAccessControl.get(0).isOngoingThread();
	}

}
