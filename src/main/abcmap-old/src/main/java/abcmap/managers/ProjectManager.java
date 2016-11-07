package abcmap.managers;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import abcmap.cancel.memento.HasMementoManager;
import abcmap.cancel.memento.MementoManager;
import abcmap.configuration.ConfigurationConstants;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Tile;
import abcmap.events.ProjectEvent;
import abcmap.exceptions.LayoutPaperException;
import abcmap.exceptions.MapLayerException;
import abcmap.exceptions.ProjectException;
import abcmap.geo.Coordinate;
import abcmap.managers.stub.MainManager;
import abcmap.project.Project;
import abcmap.project.ProjectMetadatas;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.project.loaders.abm.AbmProjectLoader;
import abcmap.project.utils.MapDimensionsHandler;
import abcmap.project.writers.AbmProjectWriter;
import abcmap.project.writers.ProjectWriter;
import abcmap.utils.Utils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;

public class ProjectManager implements HasNotificationManager,
		HasMementoManager<Object> {

	private Project project;
	private NotificationManager om;
	private ArrayList<Exception> lastLoaderMinorExceptions;
	private ArrayList<Exception> lastSaverMinorExceptions;

	public ProjectManager() throws IOException {

		// reference vers le projet actif
		this.project = null;

		// A la reception d'un evenement, retransmission vers les ecouteurs
		this.om = new NotificationManager(this);
		om.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {
				// retransmission des events vers écouteurs
				fireEvent(arg);
			}
		});

		// verifier et/ou creer le repertoire temporaire
		verifyTempDirectory();

	}

	public void openProject(File path) throws IOException {

		// verifier projet fermé
		if (project != null)
			throw new ProjectException(ProjectException.PROJECT_ALREADY_OPENED);

		// verification chemin extension etc...
		AbmProjectLoader pi = new AbmProjectLoader();
		pi.verify(path);
		lastLoaderMinorExceptions = pi.getMinorExceptions();

		// creation
		project = new Project();
		project.setTempPath(createNewProjectTempDirectory());

		// chargement
		pi.load(path, project);

		// notification
		om.fireEvent(new ProjectEvent(ProjectEvent.NEW_PROJECT_LOADED, project));

	}

	/**
	 * Creer un fichier temporaire de nom "prefix" + System.nanotime() +
	 * "suffix".
	 * <p>
	 * "suffix" peut être null.
	 * 
	 * @param prefix
	 * @param suffix
	 * @return File ou null si l'opération à échoué.
	 */
	public File createTemporaryFile(String prefix, String suffix) {

		if (prefix == null) {
			throw new NullPointerException();
		}

		if (suffix == null) {
			suffix = "";
		}

		// le fichier à renvoyer
		File rslt = null;

		// vrai si operation réussie
		boolean actionPerformed = false;

		// 3 essais max
		int trys = 3;

		for (int i = 0; i < trys && actionPerformed == false; i++) {
			try {
				String name = prefix + System.nanoTime() + suffix;
				rslt = Paths.get(getTempDirectoryPath(), name).toFile();
				actionPerformed = rslt.createNewFile();
			} catch (Exception e) {
				Log.error(e);
			}
		}

		return rslt;
	}

	public ArrayList<Exception> getLastLoaderMinorExceptions() {
		return lastLoaderMinorExceptions;
	}

	public boolean isInitialized() {
		return project != null;
	}

	public Project getProject() {
		return project;
	}

	public File getRealPath() {
		return project.getRealPath();
	}

	public void setRealPath(File file) {
		project.setRealPath(file);
	}

	public File getTempDirectoryFile() {
		return project.getTempDirectoryFile();
	}

	public String getTempDirectoryPath() {
		return project.getTempDirectoryPath();
	}

	public File[] getTempFiles() {
		return project.getTempFiles();
	}

	public void addLayer(MapLayer layer) {
		project.addLayer(layer);
	}

	public MapLayer addNewLayer() {
		return project.addNewLayer();
	}

	public ArrayList<MapLayer> getLayers() {
		return project.getLayers();
	}

	public void addLayer(MapLayer layer, Integer index)
			throws MapLayerException {
		project.addLayer(layer, index);
	}

	public MapLayer getLayer(Integer index) throws MapLayerException {
		return project.getLayer(index);
	}

	public LayoutPaper getLayout(Integer index) throws LayoutPaperException {
		return project.getLayout(index);
	}

	public void removeLayer(MapLayer layer) {
		project.removeLayer(layer);
	}

	public void addLayout(LayoutPaper layout) {
		project.addLayout(layout);
	}

	public void addLayout(LayoutPaper s, Integer index)
			throws LayoutPaperException {
		project.addLayout(s, index);
	}

	public LayoutPaper addNewLayout() {
		return project.addNewLayout();
	}

	public ArrayList<LayoutPaper> getLayouts() {
		return project.getLayouts();
	}

	public void removeLayout(LayoutPaper layout) {
		project.removeLayout(layout);
	}

	/**
	 * Active ou désactive toutes les feuilles du projet
	 * 
	 * @param val
	 */
	public void setAllLayoutsActive(boolean val) {
		for (LayoutPaper p : project.getLayouts()) {
			p.setActive(val);
		}
	}

	public ProjectMetadatas getMetadatas() {
		return project.getMetadatas();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return om;
	}

	public Dimension getMapDimensions() {
		return project.getMapDimensions();
	}

	public MapLayer getActiveLayer() throws MapLayerException {
		return project.getActiveLayer();
	}

	public boolean isDimensionsFixed() {
		return project.isDimensionsFixed();
	}

	public void setDimensionsFixed(boolean v) {
		project.setDimensionsFixed(v);
	}

	/**
	 * Change le calque actif. Avec notifications.
	 * 
	 * @param layer
	 * @throws MapLayerException
	 */
	public void setActiveLayer(MapLayer layer) throws MapLayerException {
		project.setActiveLayer(layer);

		project.fireLayersChanged();
	}

	/**
	 * Change le calque actif. Avec notifications.
	 * 
	 * @param layer
	 * @throws MapLayerException
	 */
	public void setActiveLayer(Integer index) throws MapLayerException {
		project.setActiveLayer(index);

		project.fireLayersChanged();
	}

	/**
	 * Creer un dossier temporaire de projet unique ou sont dcomprsss les lments
	 * d'un projet pour travailler dessus.
	 * 
	 * @return
	 * @throws IOException
	 */
	private File createNewProjectTempDirectory() throws IOException {

		File rslt;

		int i = 0;
		String name;
		String complement = "";

		do {
			if (i != 0) {
				complement = "_" + i;
			} else {
				complement = "";
			}
			name = new SimpleDateFormat("yyyy-M-d-HH-mm").format(new Date());
			rslt = new File(
					ConfigurationConstants.TEMP_PGRM_DIRECTORY
							.getAbsolutePath()
							+ File.separator
							+ name
							+ complement);
			i++;
		} while (rslt.exists());

		// creation du dossier de projet
		rslt.mkdir();

		return rslt;
	}

	/**
	 * Vérifie les dossiers temporaires
	 * 
	 * @throws IOException
	 */
	private File verifyTempDirectory() throws IOException {

		File temp = ConfigurationConstants.TEMP_PGRM_DIRECTORY;
		if (temp.isDirectory() == false) {
			try {
				temp.mkdirs();
			} catch (Exception e) {
				throw new IOException();
			}
		}

		return temp;
	}

	public Color getBackgroundColor() {
		return Utils.stringToColor(project.getMetadatas().BACKGROUND_COLOR);
	}

	public void save() throws ProjectException, IOException {
		save(null);
	}

	public void save(ProjectWriter saver) throws ProjectException, IOException {

		if (saver == null) {
			saver = new AbmProjectWriter();
			saver.setOverwriting(true);
		}

		saver.write(project);

		lastSaverMinorExceptions = saver.getMinorExceptions();

		// avertir les observateurs
		project.fireProjectChange(ProjectEvent.PROJECT_SAVED);

	}

	public void saveDescriptorOnly() throws ProjectException, IOException {
		AbmProjectWriter saver = new AbmProjectWriter();
		saver.saveOnlyDescriptor(true);
		saver.write(project);
	}

	public void saveAs(File path, ProjectWriter saver) throws ProjectException,
			IOException {
		if (saver == null) {
			saver = new AbmProjectWriter();
			saver.setOverwriting(true);
		}

		project.setRealPath(path);
		saver.write(project);

		// avertir les observateurs
		project.fireProjectChange(ProjectEvent.PROJECT_SAVED);

	}

	public void newProject() throws IOException {

		// reinitialiser le compteur de calque
		MapLayer.setInstancesCounter(0);

		// Creer le projet
		project = new Project();
		project.setTempPath(createNewProjectTempDirectory());

		// créer un premier calque
		project.addNewLayer();

		// dimensions du projet
		project.setDimensionsFixed(false);
		project.setMapDimensions(ConfigurationConstants.PROJECT_DEFAULT_DIMENSIONS);

		// avertir les observateurs
		project.fireProjectChange(ProjectEvent.NEW_PROJECT_LOADED);

	}

	/**
	 * Cette operation peut prendre plus d'une seconde
	 * 
	 * @throws IOException
	 */
	public void closeProject() throws IOException {

		if (project == null)
			return;

		cleanNonUsedPictures();

		// preparer les chemins a supprimer
		String root = project.getTempDirectoryFile().getAbsolutePath();

		// suppression du projet
		project = null;

		// supprimer les objets java genants (ou pas) et attendre un peu
		System.gc();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			Log.error(e);
		}

		// suppression des fichiers du dossier temp de projet
		Utils.deleteRecursively(new File(root));

		// avertir les observateurs
		om.fireEvent(new ProjectEvent(ProjectEvent.PROJECT_CLOSED, null));

	}

	public static boolean checkProjectExtension(String name) {
		int pt = name.lastIndexOf('.') + 1;
		String ext = name.substring(pt, name.length());
		if (ext.equalsIgnoreCase(ConfigurationConstants.PROJECT_EXTENSION))
			return true;
		else
			return false;
	}

	/**
	 * Supprimer les fichiers non utilisés
	 */
	public void cleanNonUsedPictures() {

		// lister toutes les tuiles et les images des calques
		ArrayList<File> files = new ArrayList<File>();
		for (MapLayer lay : project.getLayers()) {
			for (LayerElement elmt : lay.getAllElements()) {

				if (elmt instanceof Tile) {
					File f = ((Tile) elmt).getSourceFile();
					if (f != null)
						files.add(new File(f.getAbsolutePath()));
				}

				else if (elmt instanceof Image) {
					File f = ((Image) elmt).getSourceFile();
					if (f != null)
						files.add(new File(f.getAbsolutePath()));
				}
			}
		}

		// lister ceux des operations d'annulation enregistrées
		files.addAll(MainManager.getCancelManager().getTileAndImageFiles());

		// vérifier les fichiers du projet
		for (File f : project.getTempDirectoryFile().listFiles()) {

			if (f.isFile() == false)
				continue;

			if (Utils.getExtension(f.getPath()).equalsIgnoreCase("jpg") == false)
				continue;

			if (files.contains(f) == false) {
				f.delete();
			}
		}

	}

	public void fireSelectionChanged() {
		project.fireSelectionChanged();
	}

	public void fireElementsChanged() {
		project.fireElementsChanged();
	}

	public void fireLayerListChanged() {
		project.fireLayersChanged();
	}

	public void fireLayoutListChanged() {
		project.fireLayoutListChanged();
	}

	public void fireMetadatasChanged() {
		project.fireMetadatasChanged();
	}

	public void setAllElementsSelected(boolean b) {
		project.setAllElementsSelected(b);
	}

	public void fireEvent(Notification event) {
		project.getNotificationManager().fireEvent(event);
	}

	/**
	 * Retourne le memento du projet
	 */
	@Override
	public MementoManager getMementoManager() {
		return project.getMementoManager();
	}

	/**
	 * Retourne la liste de références géographiques du projet. Destiné au map
	 * manager. Pour les opérations courantes accéder plutôt aux références via
	 * le map manager.
	 * 
	 * @return
	 */
	@Deprecated
	public ArrayList<Coordinate> getGeoReferences() {
		return project.getGeoReferences();
	}

	public ArrayList<Tile> getAllTiles() {

		if (isInitialized() == false)
			return null;

		return project.getAllTiles();
	}

	public Dimension computeMaxMapDimensions() {

		if (isInitialized() == false)
			return null;

		return MapDimensionsHandler.computeMaxDimensions(project);
	}

	public boolean isLayerBelongToProject(MapLayer lay) {
		if (isInitialized() == false)
			return false;

		return project.getLayers().contains(lay);
	}

	/**
	 * Propose des dimensions à la carte.
	 * <p>
	 * Si forceIfSmaller = true, alors la carte sera redimensionnée même si les
	 * dimensions calculées sont plus petites que les dimensions de la carte.
	 * 
	 * @param forceIfSmaller
	 */
	public void proposeMapDimensions(Dimension values, boolean forceIfSmaller) {
		MapDimensionsHandler.proposeDimensions(project, values, forceIfSmaller);
	}

	/**
	 * Calcule la taille de la carte et l'applique.
	 * <p>
	 * Si forceIfSmaller = true, alors la carte sera redimensionnée même si les
	 * dimensions calculées sont plus petites que les dimensions de la carte.
	 * 
	 * @param forceIfSmaller
	 */
	public void computeAndFitDimensions(boolean forceIfSmaller) {
		MapDimensionsHandler.computeAndFitDimensionsLater(project,
				forceIfSmaller);
	}

	/**
	 * Méthode de debuggage, affiche toutes les layouts du projet.
	 */
	public void printAllLayouts() {
		for (LayoutPaper paper : project.getLayouts()) {
			System.out.println(paper);
		}
	}

	public void setNotificationsEnabled(boolean val) {
		project.setNotificationsEnabled(val);
	}

}
