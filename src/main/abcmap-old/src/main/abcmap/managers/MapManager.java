package abcmap.managers;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.events.MapEvent;
import abcmap.events.ProjectEvent;
import abcmap.exceptions.MapManagerException;
import abcmap.geo.Coordinate;
import abcmap.geo.GeoConstants;
import abcmap.geo.GeoInfoMode;
import abcmap.geo.GeoSystemsContainer;
import abcmap.gui.comps.geo.HasGeoInformations;
import abcmap.gui.comps.geo.MapPanel;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.utils.Utils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.threads.ThreadManager;

/**
 * Gestion de la carte, en tant que composant et pour toutes manipulations
 * géographiques.
 * 
 * @author remipassmoilesel
 *
 */
public class MapManager implements HasNotificationManager {

	/** Nombre max de references géographiques */
	private static final int MAX_REFERENCES_LIST_SIZE = 2;

	/** Le panneau d'affichage de la carte */
	private MapPanel mapPanel;

	/** Référence de géoéférencement active pour saisie de coordonnées */
	private Coordinate activeReference;

	/** Calcul de coordonnées */
	private MapViewport mapViewport;

	/** Calcul de coordonnées */
	private AffineTransform screenToWorld;

	/** Calcul de coordonnées */
	private AffineTransform worldToScreen;

	/** Calcul de coordonnées */
	private Double mapViewportOrigin;

	/** Calcul de coordonnées */
	private GeodeticCalculator geoCalculator;

	/** Système de référence courant de la carte */
	private CoordinateReferenceSystem mapCRS;

	/** Conteneur de systèmes de référence */
	private GeoSystemsContainer geosystems;

	/** Mise à jourdes CRS et objets géo */
	private MapManagerUpdater notifm;

	private ProjectManager projectm;

	/**
	 * Met à jour la carte et le systeme de coordonnée en fonction des
	 * changements du projet.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class MapManagerUpdater extends NotificationManager {

		public MapManagerUpdater() {
			super(MapManager.this);
		}

		@Override
		public void notificationReceived(Notification arg) {

			if (arg instanceof ProjectEvent) {

				/*
				 * Recalcul des dimensions de la carte si dimensions non fixes
				 */
				if (Utils.safeEquals(ProjectEvent.DIMENSIONS_CHANGED,
						arg.getName()) == false) {
					if (projectm.isInitialized()
							&& projectm.isDimensionsFixed() == false) {

						// ne pas redimensionner si les dimensions trouvées sont
						// plus petites
						projectm.computeAndFitDimensions(false);

					}
				}

				/*
				 * Changement de système de coordonnées en cas de chargement
				 * projet
				 */
				if (ProjectEvent.isNewProjectLoadedEvent(arg)) {

					// reset du système de coordonnées
					resetComputingGeosystem();

					// récuperer le code et l'etat du georeferencement du projet
					String code = projectm.getMetadatas().GEOSYSTEM_EPSG_CODE;
					boolean enabled = projectm.getMetadatas().GEOREFMODE_ENABLED;

					// le géoreferencement est activé
					if (enabled) {

						// le projet n'a pas de code EPSG valide
						if (GeoSystemsContainer.EMPTY_CRS.equals(code)) {
							setGeorefencementEnabled(false);
						}

						// le projet à un code EPSG valide
						else {
							setGeorefencementEnabled(true);
						}
					}

					// le georeferencement est désactivé
					else {
						setGeorefencementEnabled(false);
					}

				}

				/*
				 * Dans tous les cas, rafraichir la carte lorsque le projet est
				 * modifié
				 */
				refreshMapComponent();
			}

		}

	}

	public MapManager() {

		this.notifm = new MapManagerUpdater();

		// ecouter le projet
		this.projectm = MainManager.getProjectManager();
		projectm.getNotificationManager().addObserver(this);

		// creer le conteneur de systemes de coordonnées
		geosystems = new GeoSystemsContainer();

		// charger les systemes basiques
		geosystems.loadDefaultCRS();

	}

	/**
	 * Redessiner le composant graphique affichant la carte
	 */
	public void refreshMapComponent() {

		// si la carte est nulle essayer de la recuperer
		if (getMapComponent() == null) {
			Log.debug(new NullPointerException("Map is null"));
			return;
		}

		getMapComponent().refresh();

	}

	/**
	 * Retourne vrai si le geo referencement est active
	 * 
	 * @return
	 */
	public boolean isGeoreferencementEnabled() {

		if (projectm.isInitialized() == false) {
			return false;
		}

		else {
			return projectm.getMetadatas().GEOREFMODE_ENABLED;
		}
	}

	/**
	 * Active ou desactive le georefencement. Notifie à partir du gestionnaire
	 * de projet et du gestionnaire de carte.
	 * 
	 * @param val
	 */
	public void setGeorefencementEnabled(boolean val) {

		// enregistrement de la nouvelle valeur
		projectm.getMetadatas().GEOREFMODE_ENABLED = val;

		// activer le géoréférencement
		if (val) {

			try {
				setMapCRS(projectm.getMetadatas().GEOSYSTEM_EPSG_CODE);
			}
			// code invalide, désactiver le georeferencement
			catch (MapManagerException e) {
				Log.error(e);
				projectm.getMetadatas().GEOSYSTEM_EPSG_CODE = GeoSystemsContainer.EMPTY_CRS;
				resetComputingGeosystem();
			}

			// mettre a jour les objets geographiques
			try {
				updateGeosystem();
			} catch (MapManagerException e) {
				Log.error(e);
			}

		}

		// désactiver le géoréferencement
		else {
			projectm.getMetadatas().GEOSYSTEM_EPSG_CODE = GeoSystemsContainer.EMPTY_CRS;
			resetComputingGeosystem();
		}

		// notifications
		projectm.fireMetadatasChanged();
		notifyGeosystemChanged();
	}

	/**
	 * Ajoute une référence de georeferencement à l'index indiqué
	 * 
	 * @param coord
	 */
	public void addGeoreference(Coordinate coord, int index) {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return;
		}

		// verifier l'index
		if (index < 0 || index >= MAX_REFERENCES_LIST_SIZE) {
			throw new IllegalArgumentException("Invalid index for reference: "
					+ index);
		}

		// recuperer les references geographiques
		ArrayList<Coordinate> references = getGeoReferences();

		// ajouter une reference
		references.add(index, coord);

		// enlever les references inutiles
		while (references.size() > MAX_REFERENCES_LIST_SIZE) {
			references.remove(references.size() - 1);
		}

		// mettre à jour le systeme de coordonnée
		try {
			updateGeosystem();
		} catch (MapManagerException e) {
			Log.error(e);
		}

		// notifier
		notifyGeosystemChanged();

	}

	/**
	 * Retourne la liste des reference geographiques ou null si le projet n'est
	 * pas initialisé. Il devrait toujours y avoir au moins deux références dans
	 * la liste, même initialisées à zéro.
	 * 
	 * @return
	 */
	public ArrayList<Coordinate> getGeoReferences() {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return null;
		}

		// retourner les references
		return projectm.getGeoReferences();
	}

	/**
	 * Retourne la référence active, c'est à dire celle qui est modifié par les
	 * actions de l'utilisateur ou null si le projet n'est pas initialisé.
	 * 
	 * @return
	 */
	public Coordinate getActiveReference() {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return null;
		}

		// recuperer les references
		ArrayList<Coordinate> references = getGeoReferences();

		// si la reference est nulle, prendre la premiere
		if (activeReference == null && references.size() > 0) {
			activeReference = references.get(0);
		}

		return activeReference;
	}

	/**
	 * Retourne l'index de la référence ou -1 si le projet n'est pas initialisé.
	 * 
	 * @return
	 */
	public int getActiveReferenceIndex() {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return -1;
		}

		// recuperer les references
		return getGeoReferences().indexOf(activeReference);

	}

	/**
	 * Affecte une reference comme reference active, qui sera affectée par les
	 * modifications de l'utilisateurs.
	 * 
	 * @param index
	 * @throws MapManagerException
	 */
	public void setActiveReferenceIndex(int comboIndex)
			throws MapManagerException {
		setActiveReference(getGeoReferences().get(comboIndex));
	}

	/**
	 * Affecte une reference comme reference active, qui sera affectée par les
	 * modifications de l'utilisateurs.
	 * 
	 * @param coord
	 * @throws MapManagerException
	 */
	public void setActiveReference(Coordinate coord) throws MapManagerException {

		// la reference ne peut pas etre nulle
		if (coord == null) {
			throw new NullPointerException("Cannot add null reference");
		}

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return;
		}

		// recuperer les references
		ArrayList<Coordinate> references = getGeoReferences();

		// verifier si la reference appartient à la liste
		if (references.contains(coord) == false) {
			throw new MapManagerException(MapManagerException.INVALID_REFERENCE);
		}

		// garder la reference de la georeference
		activeReference = coord;

		// mise à jour du systeme de coordonnées
		updateGeosystem();

		notifyGeosystemChanged();
	}

	/**
	 * Notifier un changement dans les références
	 */
	public void notifyGeosystemChanged() {
		notifm.fireEvent(new MapEvent(MapEvent.GEOSYSTEM_CHANGED, null));
	}

	private void resetComputingGeosystem() {
		mapViewport = null;
		screenToWorld = null;
		worldToScreen = null;
	}

	/**
	 * Met à jour les objets de calcul en fonction du système de coordonnées et
	 * des références choisies. Met à jour également ensuite les objets
	 * géoréférencés.
	 * 
	 * @throws MapManagerException
	 */
	private void updateGeosystem() throws MapManagerException {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			resetComputingGeosystem();
			return;
		}

		// recuperer les references
		ArrayList<Coordinate> references = getGeoReferences();

		// vérifier les références
		if (references.size() < 2) {
			resetComputingGeosystem();
			throw new MapManagerException(
					MapManagerException.NOT_ENOUGHT_REFERENCES);
		}

		// supprimer les references inutiles
		while (references.size() > 2) {
			references.remove(0);
		}

		Coordinate ref1 = references.get(0);
		Coordinate ref2 = references.get(1);

		// s'assurer que les points soient bien distincts
		if (Coordinate.testIfDifferent(ref1, ref2) == false) {
			resetComputingGeosystem();
			throw new MapManagerException(
					MapManagerException.REFERENCES_ARE_EQUALS);
		}

		// tester le systeme de coordonnées
		if (mapCRS == null) {
			resetComputingGeosystem();
			throw new MapManagerException(MapManagerException.INVALID_CRS);
		}

		// determiner les coordonnées minimales et maximales pour créer
		// l'enveloppe de calcul
		double xDegMin = (ref1.longitudeSec < ref2.longitudeSec) ? ref1.longitudeSec
				: ref2.longitudeSec;
		double xDegMax = (ref1.longitudeSec > ref2.longitudeSec) ? ref1.longitudeSec
				: ref2.longitudeSec;
		double yDegMin = (ref1.latitudeSec < ref2.latitudeSec) ? ref1.latitudeSec
				: ref2.latitudeSec;
		double yDegMax = (ref1.latitudeSec > ref2.latitudeSec) ? ref1.latitudeSec
				: ref2.latitudeSec;

		// creer l'enveloppe de calcul
		ReferencedEnvelope re;
		try {
			re = new ReferencedEnvelope(xDegMin, xDegMax, yDegMin, yDegMax,
					mapCRS);
		} catch (MismatchedDimensionException e) {
			resetComputingGeosystem();
			throw new MapManagerException("Invalid references");
		}

		// création des calculateurs
		mapViewport = new MapViewport(re);
		double w = Math.abs(ref1.longitudePx - ref2.longitudePx);
		double h = Math.abs(ref1.latitudePx - ref2.latitudePx);

		mapViewport.setScreenArea(new Rectangle2D.Double(0, 0, w, h)
				.getBounds());
		screenToWorld = mapViewport.getScreenToWorld();
		worldToScreen = mapViewport.getWorldToScreen();

		// position du rectangle screen pour calculs ultérieurs
		double xPx = (ref1.longitudePx < ref2.longitudePx) ? ref1.longitudePx
				: ref2.longitudePx;
		double yPx = (ref1.latitudePx < ref2.latitudePx) ? ref1.latitudePx
				: ref2.latitudePx;

		// conserver le point d'origine pour calculs
		mapViewportOrigin = new Point2D.Double(xPx, yPx);

		// objet de calcul de distances
		geoCalculator = new GeodeticCalculator(mapCRS);

		// mettre à jour les objets georeferencés
		updateGeorefShapes();

	}

	/**
	 * Azimut/distance entre deux points à partir de leurs coordonnées
	 * géographiques.<br>
	 * [0] azimuth: de - 180 à + 180,<br>
	 * [1] distance en mètres.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 * @throws MapManagerException
	 */
	public java.lang.Double[] azimuthDistance(Coordinate p1, Coordinate p2)
			throws MapManagerException {
		return azimuthDistance(p1.getDegreesPoint(), p2.getDegreesPoint());
	}

	/**
	 * Azimut/distance entre deux points à partir de leurs coordonnées
	 * géographiques.<br>
	 * [0] azimuth: de - 180 à + 180,<br>
	 * [1] distance en mètres.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 * @throws MapManagerException
	 */
	public java.lang.Double[] azimuthDistance(Point2D p1, Point2D p2)
			throws MapManagerException {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return null;
		}

		// verifier si les objets de calcul sont présent
		if (geoCalculator == null) {
			updateGeosystem();
		}

		// tenter de calculer l'azimuth
		try {
			geoCalculator.setStartingGeographicPoint(p1);
			geoCalculator.setDestinationGeographicPoint(p2);

			return new java.lang.Double[] { geoCalculator.getAzimuth(),
					geoCalculator.getOrthodromicDistance() };
		}

		// erreur lors du calcul
		catch (IllegalArgumentException | IllegalStateException e) {
			throw new MapManagerException(e.getClass().getName() + " : "
					+ e.getMessage());
		}

	}

	public Coordinate transformCoords(GeoConstants mode, Coordinate co)
			throws MapManagerException {

		// verifier le projet
		if (projectm.isInitialized() == false) {
			return null;
		}

		// mettre a jour le systeme si necessaire
		if (mapViewport == null) {
			updateGeosystem();
		}

		// traduire des coordonnées en pixels > degrés
		if (GeoConstants.SCREEN_TO_WORLD.equals(mode)) {
			Point2D p = co.getPixelPoint();
			p.setLocation(p.getX() - mapViewportOrigin.getX(), p.getY()
					- mapViewportOrigin.getY());
			co.setDegreesPoint(screenToWorld.transform(p, null));
		}

		// traduire des coordonnées en degrés > pixels
		else if (GeoConstants.WORLD_TO_SCREEN.equals(mode)) {
			co.setPixelPoint(worldToScreen.transform(co.getDegreesPoint(), null));
			Point2D p = co.getPixelPoint();
			co.setPixelPoint(new Point2D.Double(p.getX()
					+ mapViewportOrigin.getX(), p.getY()
					+ mapViewportOrigin.getY()));
		}

		else {
			throw new IllegalArgumentException("Unknown mode: " + mode);
		}

		return co;
	}

	/**
	 * Changer le systeme de coordonnée de la carte à partir d'un code EPSG.
	 * Lève une exception si le code est incorrect.
	 * 
	 * @param epsgCode
	 * @throws MapManagerException
	 */
	public void setMapCRS(String epsgCode) throws MapManagerException {

		// recuperer le systemes
		CoordinateReferenceSystem system = geosystems.get(epsgCode);

		// verifier le CRS
		if (system == null) {
			resetComputingGeosystem();
			throw new MapManagerException(MapManagerException.INVALID_CRS);
		}

		setMapCRS(system);
	}

	/**
	 * Changer le systeme de coordonnée de la carte à partir d'un code EPSG.
	 * N'envoie pas d'exception en cas de mauvaises références: le changement de
	 * CRS doit pouvoir se faire et se propager même si les références ne sont
	 * pas au point.
	 * 
	 * @param system
	 */
	public void setMapCRS(CoordinateReferenceSystem system) {

		mapCRS = system;
		projectm.getMetadatas().GEOSYSTEM_EPSG_CODE = getEpsgCode(system);

		// mettre à jour le systeme de calcul
		try {
			updateGeosystem();
		} catch (Exception e) {
			Log.error(e);
		}

		// notifications
		notifyGeosystemChanged();
	}

	/**
	 * Retourne le systeme de coordonnées courant de la carte
	 * 
	 * @return
	 */
	public CoordinateReferenceSystem getMapCRS() {
		return mapCRS;
	}

	/**
	 * Retourne le code epsg du systeme de coordonnée de la carte
	 * 
	 * @return
	 */
	public String getMapCRScode() {

		if (projectm.isInitialized() == false) {
			return GeoSystemsContainer.EMPTY_CRS;
		}

		else {
			return projectm.getMetadatas().GEOSYSTEM_EPSG_CODE;
		}
	}

	/**
	 * Mettre à jour les formes sur un thread séparé
	 */
	public void updateGeorefShapesLater() {
		ThreadManager.runLater(new Runnable() {
			@Override
			public void run() {
				updateGeorefShapes();
			}
		});
	}

	/**
	 * Mettre à jour les forme géo-référencées du projet sur le thread appelant.
	 * 
	 */
	public void updateGeorefShapes() {

		// projet non initialisé: retour
		if (projectm.isInitialized() == false) {
			return;
		}

		// iterer les calques à la recherche d'objet georeférencés
		for (MapLayer lay : projectm.getLayers()) {
			for (LayerElement elmt : lay.getAllElementsReversed()) {
				if (elmt instanceof HasGeoInformations) {

					HasGeoInformations shp = (HasGeoInformations) elmt;

					if (GeoInfoMode.isInformationModeNotEmpty(shp
							.getGeoInfoMode())) {
						elmt.refreshShape();
					}
				}
			}
		}

		// mettre à jour la carte
		refreshMapComponent();

	}

	/**
	 * Reclamer le focus sur la carte. Permet de mieux prendre en compte les
	 * raccourcis clavier, notamment si l'utilisateur à cliqué dans un champs de
	 * texte.
	 */
	public void requestFocusOnMap() {

		// si la carte est nulle essayer de la recuperer
		if (getMapComponent() == null) {
			Log.debug(new NullPointerException("Map is null"));
			return;
		}

		getMapComponent().requestFocusInWindow();

	}

	/**
	 * Enregistrer un oservateur de souris sur la carte.
	 * 
	 * @param o
	 */
	public void registerListenerOnMap(Object o) {

		if (o instanceof MouseListener)
			getMapComponent().addMouseListener((MouseListener) o);

		if (o instanceof MouseMotionListener)
			getMapComponent().addMouseMotionListener((MouseMotionListener) o);

		if (o instanceof MouseWheelListener)
			getMapComponent().addMouseWheelListener((MouseWheelListener) o);
	}

	public void unregisterListenerOnMap(Object o) {

		if (o instanceof MouseListener)
			getMapComponent().removeMouseListener((MouseListener) o);

		if (o instanceof MouseMotionListener)
			getMapComponent()
					.removeMouseMotionListener((MouseMotionListener) o);

		if (o instanceof MouseWheelListener)
			getMapComponent().removeMouseWheelListener((MouseWheelListener) o);

	}

	/**
	 * Retourne la référence du composant graphique de représentation de la
	 * carte.
	 * 
	 * @return
	 */
	public MapPanel getMapComponent() {

		// recuperer la reference de la carte
		if (mapPanel == null) {
			mapPanel = (MapPanel) MainManager.getGuiManager().getMap();
		}

		return mapPanel;
	}

	/**
	 * Changer l'echelle de représentation de la carte.
	 * 
	 * @param s
	 */
	public void setDisplayScale(float s) {
		getMapComponent().setDisplayScale(s);
		notifm.fireEvent(new MapEvent(MapEvent.DISPLAY_SCALE_CHANGED, s));
	}

	/**
	 * Retourne l'echelle de représentation de la carte.
	 * 
	 * @return
	 */
	public float getDisplayScale() {
		return getMapComponent().getScale();
	}

	/**
	 * Converti un point de l'espace du composant graphique carte vers l'espace
	 * de coordonnées du projet. En pixels.
	 * 
	 * @param p
	 * @return
	 */
	public Point getScaledPoint(Point p) {
		return getScaledPoint(p.x, p.y);
	}

	/**
	 * Converti un point de l'espace du composant graphique carte vers l'espace
	 * de coordonnées du projet. En pixels.
	 * 
	 * @param p
	 * @return
	 */
	public Point getScaledPoint(int x, int y) {
		return getMapComponent().getPointFromComponentToViewSpace(
				new Point(x, y));
	}

	public void addToDisplayScale(float value) {

		getMapComponent().addToScale(value);
		notifyDisplayScaleChanged();
	}

	public void resetDisplay(float scale) {
		getMapComponent().resetDisplay(scale);
		notifyDisplayScaleChanged();
	}

	public void notifyDisplayScaleChanged() {
		notifm.fireEvent(new MapEvent(MapEvent.DISPLAY_SCALE_CHANGED,
				getMapComponent().getScale()));
	}

	public void setMapCursor(Cursor cursor) {
		getMapComponent().setCursor(cursor);
	}

	@Override
	public NotificationManager getNotificationManager() {
		return notifm;
	}

	/**
	 * Retourne le CRS associé au code EPSG demandé ou null
	 * 
	 * @param epsgCode
	 * @return
	 */
	public CoordinateReferenceSystem getCRS(String epsgCode) {
		return geosystems.getCRS(epsgCode);
	}

	/**
	 * Retourne le code epsg du systeme de coordonnée passé en paramètre si il
	 * en a un, ou null sinon.
	 * 
	 * @param sys
	 * @return
	 */
	public static String getEpsgCode(CoordinateReferenceSystem sys) {

		if (sys == null) {
			throw new NullPointerException("System cannot be null");
		}

		// retrouver l'identifiant
		String id = null;
		try {
			id = CRS.lookupIdentifier(sys, true);
		} catch (FactoryException e) {
			Log.error(e);
			return null;
		}

		// verifier l'identifiant
		if (id == null || id.isEmpty()) {
			return null;
		}

		// decouper et retourner
		String[] tab = id.split(":");
		if (tab.length != 2) {
			return null;
		}

		return tab[1];
	}

}
