package abcmap.importation.documents;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Tile;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.events.ImportEvent;
import abcmap.exceptions.MapImportException;
import abcmap.exceptions.MapLayerException;
import abcmap.importation.tile.ImportEventListener;
import abcmap.managers.CancelManager;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.DrawManager;
import abcmap.managers.Log;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.listeners.HasListenerHandler;
import abcmap.utils.listeners.ListenerHandler;
import abcmap.utils.threads.ThreadAccessControl;
import abcmap.utils.threads.ThreadManager;

public class DocumentImporter implements Runnable,
		HasListenerHandler<ImportEventListener> {

	public static final String ALL_PAGES = "0";

	public static final String IMPORT_AS_TILE = "IMPORT_AS_TILE";
	public static final String IMPORT_AS_IMAGE = "IMPORT_AS_IMAGE";

	private static final String TEMP_PREFIX = "waiting_for_document_import_";

	private ConfigurationManager configm;
	private AbstractDocumentRenderer renderer;
	private ProjectManager projectm;
	private DrawManager drawm;
	private CancelManager cancelm;
	private ListenerHandler<ImportEventListener> listenerHandler;

	private File fileToImport;

	public DocumentImporter() {

		this.configm = MainManager.getConfigurationManager();
		this.projectm = MainManager.getProjectManager();
		this.drawm = MainManager.getDrawManager();
		this.cancelm = MainManager.getCancelManager();

		this.listenerHandler = new ListenerHandler<>();
	}

	public void startLater() throws MapImportException {

		// les premieres verification sont effectuées ici pour pouvoir lancées
		// des exceptions adaptées et permettre l'affichage de messages
		// d'erreurs plus explicites

		// verifier le fichier a importer
		fileToImport = new File(configm.getDocumentImportPath());

		if (fileToImport.isFile() == false) {
			throw new MapImportException(MapImportException.INVALID_FILE);
		}

		// determiner le bon lecteur à utiliser
		String extension = Utils.getExtension(fileToImport.getName());
		renderer = AbstractDocumentRenderer.getRendererFor(extension);
		if (renderer == null) {
			throw new MapImportException(
					MapImportException.NO_RENDERER_AVAILABLE);
		}

		// lancer l'import
		ThreadManager.runLater(this);
	}

	@Override
	public void run() {

		// eviter les appels intempestifs
		if (ThreadAccessControl.get(1).askAccess() == false) {
			return;
		}

		// lecteur null, arret
		if (renderer == null) {
			fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			Log.error(new NullPointerException("Renderer is null"));
			return;
		}

		fireEvent(ImportEvent.IMPORT_STARTED);

		// copier le fichier original dans le repertoire temporaire
		try {
			copySourceFileInTempDirectory();
		} catch (IOException e1) {
			fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			Log.error(new IOException("Unable to write in temp directory"));
			return;
		}

		// appliquer les parametres d'import
		renderer.setFactor(configm.getDocumentImportFactor());
		renderer.setPageNumbersToImport(configm.getDocumentImportPages());

		// créer les images
		BufferedImage[] images = null;
		try {
			images = renderer.render(fileToImport);
		} catch (Throwable e) {
			Log.error(e);
			fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			return;
		}

		// obtenir le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			Log.error(e);
			fireEvent(ImportEvent.FATAL_EXCEPTION_HAPPEND);
			return;
		}

		// deselectionner tous les elements du calque actif
		projectm.setAllElementsSelected(false);

		// les transformer en objet
		String type = configm.getDocumentImportType();

		// le decalage de position entre les images, pour ne pas qu'elle soient
		// superposées
		int shift = 30;
		int currentShift = 0;

		for (BufferedImage image : images) {

			currentShift += shift;

			LayerElement elmt = null;

			// import en tant que tuile
			if (DocumentImporter.IMPORT_AS_TILE.equals(type)) {
				elmt = new Tile();
				try {
					((Tile) elmt).loadAndSaveImage(image);
				} catch (IOException e) {
					Log.error(e);
					fireEvent(ImportEvent.EXCEPTION_HAPPENED);
					continue;
				}
			}

			// import en tant qu'image
			else {
				elmt = new Image();
				try {
					((Image) elmt).loadAndSaveImage(image);
				} catch (IOException e) {
					Log.error(e);
					fireEvent(ImportEvent.EXCEPTION_HAPPENED);
					continue;
				}
			}

			// selectionner l'element qui vient d'être ajouté
			elmt.setPosition(new Point(currentShift, currentShift));
			elmt.setSelected(true);
			elmt.refreshShape();

			// ajout de l'element
			layer.addElement(elmt);

			// enregistrement de l'opération pour annulation
			ElementsCancelOp op = cancelm.addDrawOperation(layer, elmt);
			op.elementsHaveBeenAdded(true);

		}

		// selection de l'outil adapté
		if (DocumentImporter.IMPORT_AS_TILE.equals(type)) {
			drawm.setCurrentTool(ToolLibrary.TILE_TOOL);
		}

		else {
			drawm.setCurrentTool(ToolLibrary.IMAGE_TOOL);
		}

		fireEvent(ImportEvent.IMPORT_FINISHED);

		// eviter les appels intempestifs
		ThreadAccessControl.get(1).releaseAccess();

	}

	/**
	 * Copier le fichier à importer dans les fichiers temporaires.
	 * 
	 * @throws IOException
	 */
	protected void copySourceFileInTempDirectory() throws IOException {

		// creer un fichier temporaire
		File destination = projectm.createTemporaryFile(TEMP_PREFIX, null);

		if (destination == null) {
			throw new IOException("Unable to write temp files");
		}

		FileOutputStream stream = null;
		try {

			// copier le fichier à importer
			stream = new FileOutputStream(destination);
			Files.copy(fileToImport.toPath(), stream);

			// conserver la référence du fichier
			fileToImport = destination;
		}

		catch (IOException e) {
			throw new IOException(e);
		}

		finally {
			if (stream != null)
				stream.close();
		}

	}

	public void fireEvent(String name) {
		listenerHandler.fireEvent(new ImportEvent(name, null));
	}

	@Override
	public ListenerHandler<ImportEventListener> getListenerHandler() {
		return listenerHandler;
	}

	public boolean isWorking() {
		return ThreadAccessControl.get(1).isOngoingThread();
	}

	/**
	 * Retourne un tableau d'entiers contenant les numéros de page à importer.
	 * Si le tableau contient un 0, alors toutes les pages doivent être
	 * imprimées.
	 * 
	 * @param str
	 * @return
	 */
	public static Integer[] parsePagesString(String str) {

		// convertir la chaine en tableau
		Integer[] array = Utils.stringToIntArray(str);

		return array;

	}

}
