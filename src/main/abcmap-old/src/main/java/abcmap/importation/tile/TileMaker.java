package abcmap.importation.tile;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import abcmap.clipboard.ClipboardListener;
import abcmap.events.ClipboardEvent;
import abcmap.events.ImportEvent;
import abcmap.managers.CancelManager;
import abcmap.managers.ImportManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.utils.Utils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;

/**
 * Création de tuiles à partir d'images ou de fichiers puis ajout au calque
 * actif.
 * <p>
 * Les processus d'analyse sont traités dans un thread différent.
 * <p>
 * Les images sont écrites dans le dossier temporaire avant d'être taitées.
 * 
 */
public class TileMaker implements HasListenerHandler<ImportEventListener>, ClipboardListener {

	protected ProjectManager projectm;
	protected ImportManager importm;
	protected CancelManager cancelm;

	/** Liste d'attente d'images */
	protected ArrayList<File> waitingList;

	/** Activer le recadrage des images */
	protected Boolean activateCropping;

	/** Le rectangle de recadrage */
	protected Rectangle cropRectangle;

	/** Analyse de la file d'attente */
	private TileMakerListAnalyser waitingListAnalyser;

	/** Compter les instances pour distinguer les fichiers temporaires */
	private static int instances = 0;
	private int instanceNumber;
	private String tempFilePrefix;

	private ListenerHandler<ImportEventListener> listenerHandler;

	protected int totalToImport;
	protected int ioErrors;
	protected int imported;
	protected int refused;

	public TileMaker() {

		instances++;
		this.instanceNumber = instances;
		this.tempFilePrefix = "waitingForTileMaker_n" + instanceNumber + "_";

		projectm = MainManager.getProjectManager();
		importm = MainManager.getImportManager();
		cancelm = MainManager.getCancelManager();
		listenerHandler = new ListenerHandler<ImportEventListener>();

		waitingList = new ArrayList<File>();
		waitingListAnalyser = new TileMakerListAnalyser(this);
		activateCropping = false;
		cropRectangle = null;

		resetIndicators();
	}

	protected void resetIndicators() {
		totalToImport = 0;
		ioErrors = 0;
		imported = 0;
		refused = 0;
	}

	public boolean isWorking() {
		return waitingList.size() >= 0 || waitingListAnalyser.isWorking();
	}

	protected void fireEvent(String name) {

		// recuperation des stats
		ImportEvent ev = new ImportEvent(name, null);
		ev.setImported(imported);
		ev.setTotalToImport(totalToImport);
		ev.setRefused(refused);
		ev.setIoErrors(ioErrors);

		listenerHandler.fireEvent(ev);
	}

	/**
	 * Ajoute une image a la liste d'attente pour analyse.
	 * 
	 * @param img
	 */
	public void add(BufferedImage img) {

		// ajouter l'image
		if (addAndSaveBufferedImage(img) == false) {
			ioErrors++;
			return;
		}

		// stats
		totalToImport++;

		// demarrer l'analyse si necessaire
		startAnalyseIfStopped();

		fireEvent(ImportEvent.WAITING_LIST_CHANGED);

	}

	/**
	 * Enregistrer une BufferedImage dans les fichiers temporaires et l'ajouter
	 * à la liste d'attente.
	 * <p>
	 * Ne comptabilise pas les erreurs ou les succés, n'envoie pas d'evenement.
	 * 
	 * @param img
	 * @return
	 */
	private boolean addAndSaveBufferedImage(BufferedImage img) {

		// creer un fichier temporaire
		File f = projectm.createTemporaryFile(tempFilePrefix, ".jpg");
		if (f == null) {
			Log.error(new IOException("Unable to write temp files"));
			return false;
		}

		// ecrire l'image
		try {
			Utils.writeImage(img, f);
		} catch (IOException e) {
			Log.error(new IOException("Unable to write temp files"));
			return false;
		}

		// ajout à la liste d'attente
		waitingList.add(f);

		return true;

	}

	protected synchronized void startAnalyseIfStopped() {
		// démarrer l'analyse si stopée
		if (waitingListAnalyser.isWorking() == false) {
			waitingListAnalyser.start();
		}
	}

	public void add(ArrayList<BufferedImage> images) {

		// iterer la liste des images
		for (BufferedImage img : images) {

			// ajout à la liste d'attente
			if (addAndSaveBufferedImage(img) == true) {
				totalToImport++;
			}

			else {
				ioErrors++;
			}
		}

		// demarrer l'analyse si necessaire
		startAnalyseIfStopped();

		// notification
		fireEvent(ImportEvent.WAITING_LIST_CHANGED);

	}

	public void addFiles(ArrayList<File> files) {

		// iterer les fichiers
		for (File toCopy : files) {

			FileOutputStream stream = null;
			try {

				// creer un fichier temporaire
				File temp = projectm.createTemporaryFile(tempFilePrefix, ".jpg");
				stream = new FileOutputStream(temp);

				// copier le fichier source
				Files.copy(toCopy.toPath(), stream);

				// ajout si valide
				waitingList.add(temp);

				totalToImport++;
			}

			// erreur lors de la lecture
			catch (IOException e) {
				Log.error(e);
				ioErrors++;
			}

			finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						Log.error(e);
					}
				}
			}
		}

		// demarrer l'analyse si necessaire
		startAnalyseIfStopped();

		// notification
		fireEvent(ImportEvent.WAITING_LIST_CHANGED);

	}

	protected ArrayList<File> getWaitingList() {
		return waitingList;
	}

	public void enableCropping(Boolean val) {
		this.activateCropping = val;
	}

	/**
	 * Stopper l'analyse dès que possible
	 */
	public void stopImportLater() {
		if (waitingListAnalyser != null && waitingListAnalyser.isWorking() == false) {
			waitingListAnalyser.stop();
		}
	}

	public void setCropRectangle(Rectangle cropRectangle) {
		this.cropRectangle = cropRectangle;
	}

	@Override
	public ListenerHandler<ImportEventListener> getListenerHandler() {
		return listenerHandler;
	}

	/**
	 * Supprime tous les fichiers du dosseir temporaire contenant dans leur nom
	 * le préfixe de cet objet.
	 */
	protected void cleanTempFiles() {

		File[] tempDir = projectm.getTempFiles();
		for (File tempFile : tempDir) {
			if (tempFile.getName().contains(tempFilePrefix)) {
				try {
					Files.delete(tempFile.toPath());
				} catch (IOException e) {
					Log.error(e);
				}
			}
		}

	}

	/**
	 * Reception d'une nouvelle image en provenance du presse papier
	 */
	@Override
	public void clipboardChanged(ClipboardEvent event) {
		if (ClipboardEvent.NEW_IMAGE.equals(event.getName())) {
			add((BufferedImage) event.getValue());
		}
	}

}
