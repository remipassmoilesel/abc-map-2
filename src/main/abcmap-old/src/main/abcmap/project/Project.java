package abcmap.project;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import abcmap.cancel.memento.HasMementoManager;
import abcmap.cancel.memento.MementoManager;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Tile;
import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.exceptions.LayoutPaperException;
import abcmap.exceptions.MapLayerException;
import abcmap.geo.Coordinate;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.project.layouts.LayoutPaper;
import abcmap.utils.lists.ListenableContainer;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;

/**
 * Projet de carte. Un projet est composé de métadonnées, de calques, et de
 * feuilles de mise en page.
 * <p>
 * Certaines opérations sur les calques et les feuilles de mise en page envoient
 * des exceptions pour forcer la prise en comptes d'erreurs possibles: absence
 * de calque ou de feuille de mise en page, problèmes d'index, ...
 * 
 * @author remipassmoilesel
 *
 */
public class Project implements HasNotificationManager,
		HasMementoManager<ArrayList<Object>> {

	/** Les métadonnées du projet */
	private ProjectMetadatas metadatas;

	/** Le chemin final du projet */
	private File realPath;

	/** Le chemin temporaire du projet */
	private File tempPath;

	/**
	 * La référence vers le gestionnaire de notifications, celui du gestionnaire
	 * de projet
	 */
	private NotificationManager notifm;

	/** Les calques du projet */
	private ListenableContainer<MapLayer> layers;

	/** Les feuilles de mise en page du projet */
	private ListenableContainer<LayoutPaper> layouts;

	/**
	 * Les références géographiques du projet. Il devrait toujours y avoir au
	 * moins deux références dans la liste, même initialisées à zéro.
	 */
	private ListenableContainer<Coordinate> georeferences;

	/** Le calque actif */
	private MapLayer activeLayer;

	/**
	 * Si vrai, les listes du projet envoient des notifications lorsqu'elles
	 * sont modifiées
	 */
	private boolean notificationsEnabled;

	/** Le gestionnaire d'annulation */
	private Canceler canceler;

	public Project() {

		this.metadatas = new ProjectMetadatas();
		this.realPath = null;
		this.tempPath = null;
		this.notificationsEnabled = true;

		// le projet et les listes se servent de l'observermanager du
		// projectcontrol
		this.notifm = MainManager.getProjectManager().getNotificationManager();

		this.layers = new ListenableContainer<MapLayer>();
		layers.setEventClass(ProjectEvent.class);
		layers.setEventName(ProjectEvent.LAYERS_LIST_CHANGED);
		layers.setNotificationManager(notifm);
		
		this.layouts = new ListenableContainer<LayoutPaper>();
		layouts.setEventClass(ProjectEvent.class);
		layouts.setEventName(ProjectEvent.LAYOUTS_LIST_CHANGED);
		layouts.setNotificationManager(notifm);

		this.georeferences = new ListenableContainer<Coordinate>();
		georeferences.add(new Coordinate());
		georeferences.add(new Coordinate());
		georeferences.setEventClass(MapEvent.class);
		georeferences.setEventName(MapEvent.GEOSYSTEM_CHANGED);
		georeferences.setNotificationManager(notifm);

		// annualation
		this.canceler = new Canceler();

	}

	/**
	 * Activer ou desactiver le lancement d'evenement lors d'une modification de
	 * calque ou de mise en page.
	 * 
	 * @param val
	 */
	public void setNotificationsEnabled(boolean val) {
		layers.setNotificationsEnabled(val);
		layouts.setNotificationsEnabled(val);
		this.notificationsEnabled = val;
	}

	/**
	 * Retourne le chemin réel du projet, par exemple /home/projet.abm
	 * 
	 * @return
	 */
	public File getRealPath() {
		return realPath;
	}

	/**
	 * Affecter le chemin réel du projet
	 * 
	 * @param file
	 */
	public void setRealPath(File file) {
		this.realPath = file;
	}

	/**
	 * Retourne le dossier temporaire du projet sur le disque
	 * 
	 * @return
	 */
	public File getTempDirectoryFile() {
		return tempPath;
	}

	/**
	 * Retourne le chemin du dossier temporaire du projet sur le disque
	 * 
	 * @return
	 */
	public String getTempDirectoryPath() {
		return tempPath.getAbsolutePath();
	}

	/**
	 * Affecter le chemin temporaire du projet sur le disque
	 * 
	 * @param tempPath
	 */
	public void setTempPath(File tempPath) {
		this.tempPath = tempPath;
	}

	/**
	 * Retourne la liste de tous les fichiers temporaires du projet
	 * 
	 * @return
	 */
	public File[] getTempFiles() {
		return tempPath.listFiles();
	}

	/**
	 * Retourne le calque actif du projet. Si aucun calque n'est présent envoi
	 * une exception.
	 * 
	 * @return
	 * @throws MapLayerException
	 */
	public MapLayer getActiveLayer() throws MapLayerException {

		// test si le calque actif est null
		if (activeLayer == null) {

			// essayer d'affecter le premier calque
			if (layers.size() > 0) {
				activeLayer = layers.get(0);
			}

			// ou lancer une erreur
			else {
				throw new MapLayerException();
			}
		}

		return activeLayer;
	}

	/**
	 * Affecte le calque actif du projet. Envoi une exception si le calque
	 * n'appartient pas au projet.
	 * 
	 * @param layer
	 * @throws MapLayerException
	 */
	public void setActiveLayer(MapLayer layer) throws MapLayerException {
		if (layers.contains(layer) == false) {
			throw new MapLayerException();
		}

		activeLayer = layer;
	}

	/**
	 * Affecte le calque actif du projet. Envoi une exception si le calque
	 * n'appartient pas au projet.
	 * 
	 * @param layer
	 * @throws MapLayerException
	 */
	public void setActiveLayer(Integer index) throws MapLayerException {
		try {
			activeLayer = layers.get(index);
		} catch (IndexOutOfBoundsException e) {
			throw new MapLayerException();
		}
	}

	/**
	 * Ajoute un calque au projet.
	 * 
	 * @param layer
	 */
	public void addLayer(MapLayer layer) {
		layers.add(layer);
	}

	/**
	 * Ajoute un calque au projet, à la position désirée. Envoie une exception
	 * si la position est invalide.
	 * 
	 * @param layer
	 * @param index
	 * @throws MapLayerException
	 */
	public void addLayer(MapLayer layer, Integer index)
			throws MapLayerException {
		try {
			layers.add(layer, index);
		} catch (IndexOutOfBoundsException e) {
			throw new MapLayerException();
		}
	}

	/**
	 * Crée, ajoute et retourne un nouveau calque.
	 * 
	 * @return
	 */
	public MapLayer addNewLayer() {
		MapLayer layer = new MapLayer();
		layers.add(layer);
		return layer;
	}

	/**
	 * Retourne une copie de la liste de tous les calques.
	 * 
	 * @return
	 */
	public ArrayList<MapLayer> getLayers() {
		return layers.getCopy();
	}

	/**
	 * Enleve un calque de la liste des calques.
	 * 
	 * @param layer
	 */
	public void removeLayer(MapLayer layer) {
		layers.remove(layer);
	}

	/**
	 * Retourne le calque d'index spécifié ou envoi une exception.
	 * 
	 * @param index
	 * @return
	 * @throws MapLayerException
	 */
	public MapLayer getLayer(Integer index) throws MapLayerException {
		try {
			return layers.get(index);
		} catch (IndexOutOfBoundsException e) {
			throw new MapLayerException();
		}
	}

	/**
	 * Ajoute une feuille de mise en page au projet
	 * 
	 * @param layout
	 */
	public void addLayout(LayoutPaper layout) {
		layouts.add(layout);
	}

	/**
	 * Ajoute une feuille de mise en page au projet à l'index spécifié ou envoi
	 * une exception si l'index est invalide.
	 * 
	 * @param layout
	 */
	public void addLayout(LayoutPaper s, Integer index)
			throws LayoutPaperException {
		try {
			layouts.add(s, index);
		} catch (IndexOutOfBoundsException e) {
			throw new LayoutPaperException();
		}
	}

	/**
	 * Retourne la feuille d'index spécifié en argument.
	 * 
	 * @param index
	 * @return
	 */
	public LayoutPaper getLayout(Integer index) {
		return layouts.get(index);
	}

	/**
	 * Créer, ajouter et retourner une nouvelle feuille.
	 * 
	 * @return
	 */
	public LayoutPaper addNewLayout() {
		
		LayoutPaper layout = new LayoutPaper();
		layouts.add(layout);

		return layout;
	}

	/**
	 * Retourne une copie de la liste des feuilles
	 * 
	 * @return
	 */
	public ArrayList<LayoutPaper> getLayouts() {
		return layouts.getCopy();
	}

	/**
	 * Supprime la feuille du projet
	 * 
	 * @param sheet
	 */
	public void removeLayout(LayoutPaper sheet) {
		layouts.remove(sheet);
	}

	/**
	 * Retourne le conteneur de métadonnées du projet
	 * 
	 * @return
	 */
	public ProjectMetadatas getMetadatas() {
		return metadatas;
	}

	/**
	 * Affecter un conteneur de métadonnées au projet
	 * 
	 * @param metadatas
	 */
	public void setMetadatas(ProjectMetadatas metadatas) {
		this.metadatas = metadatas;
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	/**
	 * Ajouter une référence géographique. Il ne peut y avoir que deux
	 * références maximum. Lorsque qu'une référence est ajoutée elle l'est à la
	 * fin de la liste, et les références en trop sont supprimées en partant de
	 * l'index zéro.
	 * 
	 * @param m
	 */
	public void addGeoreference(Coordinate c) {

		// vérifier la référence
		if (c == null) {
			throw new NullPointerException("Reference cannot be null.");
		}

		// supprimer les références en trop sans notifications
		georeferences.setNotificationsEnabled(false);
		while (georeferences.size() > 1) {
			georeferences.remove(0);
		}
		georeferences.setNotificationsEnabled(true);

		// ajouter la référence avec notifications
		georeferences.add(c);

	}

	/**
	 * Retourne une copie de la lite de références géographiques.
	 * 
	 * @return
	 */
	public ArrayList<Coordinate> getGeoReferences() {
		return georeferences.getCopy();
	}

	/**
	 * Notifier les observateurs de changements sur le projet. Les notifications
	 * doivent être activées.
	 * 
	 * @param event
	 */
	public void fireProjectChange(String event) {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(event, this));
		}
	}

	/**
	 * Notifier les observateurs de changements de sélection sur le projet. Les
	 * notifications doivent être activées.
	 * 
	 * @param event
	 */
	public void fireSelectionChanged() {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(ProjectEvent.SELECTION_CHANGED, null));
		}
	}

	/**
	 * Notifier les observateurs de changements sur le projet. Les notifications
	 * doivent être activées.
	 * 
	 * @param event
	 */
	public void fireElementsChanged() {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(ProjectEvent.ELEMENTS_CHANGED, null));
		}
	}

	/**
	 * Notifier les observateurs de changements sur le projet. Les notifications
	 * doivent être activées.
	 * 
	 * @param event
	 */
	public void fireLayersChanged() {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(ProjectEvent.LAYERS_LIST_CHANGED,
					null));
		}
	}

	/**
	 * Notifier les observateurs de changements sur le projet. Les notifications
	 * doivent être activées.
	 * 
	 * @param event
	 */
	public void fireMetadatasChanged() {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(ProjectEvent.METADATAS_CHANGED, null));
		}
	}

	/**
	 * Notifier les observateurs de changements sur le projet. Les notifications
	 * doivent être activées.
	 * 
	 * @param event
	 */
	public void fireLayoutListChanged() {
		if (notificationsEnabled) {
			notifm.fireEvent(new ProjectEvent(ProjectEvent.LAYOUTS_LIST_CHANGED,
					null));
		}
	}

	/**
	 * Retourne les dimensions de la carte en pixel
	 * 
	 * @return
	 */
	public Dimension getMapDimensions() {
		return new Dimension(metadatas.MAP_DIMENSIONS);
	}

	/**
	 * Retourne vrai si les dimensions sont fixes et si elles ne peuvent pas
	 * être changées dynamiquement.
	 * 
	 * @return
	 */
	public boolean isDimensionsFixed() {
		return metadatas.MAP_DIMENSIONS_FIXED;
	}

	/**
	 * Fixer les dimensions du projet. Si les dimensions sont dynamiques,
	 * l'ajout d'un élement provoquera le recalcul des dimensions du projet.
	 * 
	 * @param v
	 */
	public void setDimensionsFixed(boolean v) {
		metadatas.MAP_DIMENSIONS_FIXED = v;
	}

	@Override
	public MementoManager<ArrayList<Object>> getMementoManager() {
		return canceler;
	}

	private class Canceler extends MementoManager<ArrayList<Object>> {

		@Override
		public ArrayList<Object> saveState() {

			// calques
			ArrayList<Object> st = new ArrayList<Object>();
			st.add(layers.getCopy());

			// calque actif
			MapLayer activeLayer = null;
			try {
				activeLayer = getActiveLayer();
			} catch (MapLayerException e) {
				Log.error(e);
			}
			st.add(activeLayer);

			// feuilles de mise en page
			st.add(layouts.getCopy());
			return st;
		}

		@Override
		protected void setState(ArrayList<Object> st) {

			// calques
			layers.clear();
			layers.setNotificationsEnabled(false);
			layers.addAll((List<MapLayer>) st.get(0));
			layers.setNotificationsEnabled(true);

			// calque actif
			if (layers.contains((MapLayer) st.get(1))) {
				try {
					setActiveLayer((MapLayer) st.get(1));
				} catch (MapLayerException e) {
					Log.error(e);
				}
			}

			// feuilles de mise en page
			layouts.clear();
			layouts.setNotificationsEnabled(false);
			layouts.addAll((List<LayoutPaper>) st.get(0));
			layers.setNotificationsEnabled(true);

		}

	}

	/**
	 * Parcours les calques du projet et retourne la liste de toutes les tuiles.
	 * 
	 * @return
	 */
	public ArrayList<Tile> getAllTiles() {

		ArrayList<Tile> result = new ArrayList<Tile>(100);
		for (MapLayer layer : layers) {
			for (LayerElement elmt : layer.getTiles()) {
				result.add((Tile) elmt);
			}
		}

		return result;
	}

	/**
	 * Parcours les calques et retourne la liste de tous les elements du projet.
	 * 
	 * @return
	 */
	public ArrayList<LayerElement> getAllElements() {
		ArrayList<LayerElement> result = new ArrayList<LayerElement>(500);
		for (MapLayer ml : layers) {
			result.addAll(ml.getAllElements());
		}
		return result;
	}

	/**
	 * Parcours de tous les calques et sélection de tous les élements
	 * 
	 * @param b
	 */
	public void setAllElementsSelected(boolean b) {
		for (MapLayer ml : layers) {
			ml.setAllElementsSelected(b);
		}
	}

	/**
	 * Pour les opérations courantes utiliser plutôt
	 * MapDimensionsHandler.propose()
	 * 
	 * @param values
	 */
	@Deprecated
	public void setMapDimensions(Dimension values) {
		metadatas.MAP_DIMENSIONS = new Dimension(values);
	}
}
