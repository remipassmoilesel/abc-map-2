package abcmap.managers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import abcmap.draw.DrawConstants;
import abcmap.draw.basicshapes.DrawProperties;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.draw.shapes.Ellipse;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Label;
import abcmap.draw.shapes.Polygon;
import abcmap.draw.shapes.Polyline;
import abcmap.draw.shapes.Rectangle;
import abcmap.draw.shapes.Symbol;
import abcmap.draw.shapes.Tile;
import abcmap.draw.symbols.SymbolImage;
import abcmap.draw.symbols.SymbolImageLibrary;
import abcmap.draw.tools.MapTool;
import abcmap.draw.tools.containers.ToolContainer;
import abcmap.draw.tools.containers.ToolLibrary;
import abcmap.events.DrawManagerEvent;
import abcmap.events.ProjectEvent;
import abcmap.exceptions.DrawManagerException;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;
import abcmap.project.properties.ShapeProperties;
import abcmap.project.utils.ShapeUpdater;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.notifications.Notification;
import abcmap.utils.notifications.HasNotificationManager;
import abcmap.utils.notifications.NotificationManager;
import abcmap.utils.notifications.UpdatableByNotificationManager;

/**
 * 
 * Gerer les outils de dessin
 * 
 */
public class DrawManager implements HasNotificationManager {

	/** Liste de toutes les formes disponibles */
	private static final Class[] availablesShapes = new Class[] { Tile.class,
			Rectangle.class, Label.class, Ellipse.class, Image.class,
			Polygon.class, Polyline.class, Symbol.class, Image.class };

	/**
	 * Liste des noms des formes. Initialisé plus tard pour supporter
	 * l'internationalisation
	 */
	private static String[] availablesShapeNames;

	private ProjectManager projectm;

	// marge supplementaire pour zone d'interaction des formes avec la souris
	private static final int INTERACTION_AREA_MARGIN = 10;

	private ToolContainer currentTC = null;

	private NotificationManager observer;
	private DrawProperties stroke;

	// objets pour sauvegardes de caractristiques
	private Label witnessLabel;
	private Symbol witnessSymbol;
	private Polyline witnessPolyline;
	private Polygon witnessPolygon;

	public DrawManager() {

		projectm = MainManager.getProjectManager();

		// ecouter le projet pour reinitilisation des parametres
		this.observer = new NotificationManager(this);
		observer.setDefaultUpdatableObject(new UpdatableByNotificationManager() {
			@Override
			public void notificationReceived(Notification arg) {
				if (ProjectEvent.isNewProjectLoadedEvent(arg)) {
					// raz des formes témoin
					witnessLabel = null;
					witnessSymbol = null;
					witnessPolyline = null;
					witnessPolygon = null;
				}
			}
		});

		MainManager.getProjectManager().getNotificationManager().addObserver(this);

		// options de dessin par defaut
		this.stroke = new DrawProperties();
		this.stroke.setFgColor(Color.blue);

	}

	synchronized public void setCurrentTool(ToolContainer tc) {

		GuiManager gui = MainManager.getGuiManager();
		MapManager mapm = MainManager.getMapManager();

		// changement de curseur
		if (gui == null)
			return;

		// arreter le travail du precedent outil
		if (currentTC != null && currentTC.getCurrentInstance() != null) {

			// signifier l'arret
			currentTC.getCurrentInstance().stopWorking();

			// desenregistrement des observateurs
			MainManager.getMapManager().unregisterListenerOnMap(
					currentTC.getCurrentInstance());
			observer.removeObserver(currentTC.getCurrentInstance());
		}

		currentTC = tc;

		// création d'un nouvel outils à chaque changement
		MapTool tool = currentTC.getNewInstance();
		mapm.registerListenerOnMap(tool);

		// notifier les observateurs
		observer.fireEvent(new DrawManagerEvent(DrawManagerEvent.TOOL_CHANGED,
				currentTC));

	}

	/**
	 * Changer l'outil courant à partir de l'identifiant de l'outil
	 * 
	 * @param toolId
	 */
	public void setCurrentTool(String toolId) {

		for (ToolContainer tc : ToolLibrary.getAvailablesTools()) {
			if (tc.getId().equals(toolId)) {
				setCurrentTool(tc);
				return;
			}
		}

	}

	/**
	 * Retourne le premier element sélectionné ou null
	 * 
	 * @param filter
	 * @return
	 */
	public LayerElement getFirstSelectedElement() {
		return getFirstSelectedElement(new ArrayList());
	}

	/**
	 * Retourne le premier element sélectionné ou null
	 * 
	 * @param filter
	 * @return
	 */
	public LayerElement getFirstSelectedElement(
			Class<? extends LayerElement> filter) {
		ArrayList<Class<? extends LayerElement>> list = new ArrayList<>();
		list.add(filter);
		return getFirstSelectedElement(list);
	}

	/**
	 * Retourne le premier element sélectionné ou null
	 * 
	 * @param filter
	 * @return
	 */
	public LayerElement getFirstSelectedElement(
			List<Class<? extends LayerElement>> filters) {

		// projet non initialisé, retour
		if (projectm.isInitialized() == false)
			return null;

		// recuperer le calque actif
		MapLayer lay;
		try {
			lay = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			Log.debug(e);
			return null;
		}

		for (LayerElement elmt : lay.getAllElements()) {

			// element non selectionné, continuer
			if (elmt.isSelected() == false) {
				continue;
			}

			// pas de filtre, retourner le premier element selectionne
			if (filters == null || filters.size() < 1) {
				return elmt;
			}

			// filtre actif, retourner le premier element correspondant
			else if (filters.contains(elmt.getClass())) {
				return elmt;
			}
		}

		return null;
	}

	/**
	 * Affecte le mode à l'outil courant
	 * 
	 * @param mode
	 */
	public void setToolMode(String mode) {

		if (currentTC == null || currentTC.getCurrentInstance() == null)
			return;

		currentTC.getCurrentInstance().setToolMode(mode);

		// notifier les observateurs
		notifyToolModeChanged();
	}

	/**
	 * Retourne le mode de l'outil courant ou null
	 * 
	 * @return
	 */
	public String getToolMode() {

		if (currentTC == null || currentTC.getCurrentInstance() == null)
			return null;

		return currentTC.getCurrentInstance().getToolMode();
	}

	public void notifyToolModeChanged() {
		observer.fireEvent(new DrawManagerEvent(
				DrawManagerEvent.TOOL_MODE_CHANGED, null));
	}

	public MapTool getCurrentTool() {
		if (currentTC == null)
			return null;
		return currentTC.getCurrentInstance();
	}

	public ToolContainer getCurrentToolContainer() {
		return currentTC;
	}

	public void setStroke(DrawProperties stroke) {
		this.stroke = new DrawProperties(stroke);

		// notifier les observateurs
		observer.fireEvent(new DrawManagerEvent(
				DrawManagerEvent.DRAW_STROKE_CHANGED, stroke));
	}

	/**
	 * Retourne un obet contenant les options de dessin actives.
	 * 
	 * @return
	 */
	public DrawProperties getNewStroke() {
		return new DrawProperties(stroke);
	}

	public Dimension getMaxDrawingDimensions() {
		return projectm.getMapDimensions();
	}

	@Override
	public NotificationManager getNotificationManager() {
		return observer;
	}

	/**
	 * Stroke de cadre de selection
	 * 
	 * @return
	 */
	public Stroke getSelectionStroke() {
		return new BasicStroke(3);
	}

	/**
	 * Couleur de cadre de selection
	 * 
	 * @return
	 */
	public Color getSelectionColor() {
		return new Color(0, 255, 0);
	}

	public String[] getAvailableFonts() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();
	}

	public Class[] getAvailablesShapes() {
		return availablesShapes;
	}

	public String[] getAvailablesShapeNames() {
		if (availablesShapeNames == null) {
			availablesShapeNames = new String[] { "Tuile", "Rectangle",
					"Texte", "Ellipse", "Image", "Polygone", "Polyligne",
					"Symbol", "Image" };
		}

		return availablesShapeNames;
	}

	public String getReadableNameFor(Class<? extends LayerElement> class1) {

		// trouver l'index de la forme si elle existe
		int index = Arrays.asList(availablesShapes).indexOf(class1);

		if (index == -1) {
			throw new IllegalArgumentException("Unknown shape: "
					+ class1.getSimpleName());
		}

		return getAvailablesShapeNames()[index];

	}

	/**
	 * Obtenir un label tmoin avec les proprits en cours
	 * 
	 * @return
	 */
	public Label getWitnessLabel() {

		if (witnessLabel == null) {
			witnessLabel = new Label();
		}

		witnessLabel.setStroke(new DrawProperties(stroke));
		return new Label(witnessLabel);
	}

	/**
	 * Affecter un label tmoin avec les proprits en cours Efface le texte du
	 * label.
	 * 
	 * @return
	 */
	public void setWitnessLabel(Label lbl) {
		witnessLabel = lbl;
		witnessLabel.setText(null);
	}

	/**
	 * Retourne la liste des sets de symbole disponibles
	 * 
	 * @return
	 */
	public ArrayList<String> getAvailableSymbolSets() {
		return (ArrayList<String>) SymbolImageLibrary.getAvailablesSets();
	}

	public SymbolImage getSymbolImage(String set, int code, int size,
			Color color) {
		return SymbolImageLibrary.getImage(set, code, size, color);
	}

	/**
	 * Retourne la liste des codes disponibles
	 * 
	 * @param setName
	 * @return
	 */
	public ArrayList<Integer> getAvailableSymbolCodesFor(String setName) {
		return SymbolImageLibrary.getAvailablesCodesFor(setName);
	}

	public Font getSymbolSetFont(String name) throws DrawManagerException {
		return SymbolImageLibrary.getSetFont(name);
	}

	/**
	 * Obtenir un label tmoin avec les proprits en cours
	 * 
	 * @return
	 */
	public Symbol getWitnessSymbol() {

		if (witnessSymbol == null) {
			witnessSymbol = new Symbol();
		}

		witnessSymbol.setStroke(new DrawProperties(stroke));
		return new Symbol(witnessSymbol);
	}

	/**
	 * Affecter un label tmoin avec les proprits en cours Efface le texte du
	 * label.
	 * 
	 * @return
	 */
	public void setWitnessSymbol(Symbol sbl) {
		witnessSymbol = sbl;
	}

	public PolypointShape getWitnessPolyline() {

		if (witnessPolyline == null) {
			witnessPolyline = new Polyline();
		}

		witnessPolyline.setStroke(new DrawProperties(stroke));
		return new Polyline(witnessPolyline);
	}

	public void setWitnessPolyline(Polyline poly) {
		this.witnessPolyline = poly;
		witnessPolyline.clearPoints();
	}

	public PolypointShape getWitnessPolygon() {

		if (witnessPolygon == null) {
			witnessPolygon = new Polygon();
		}

		witnessPolygon.setStroke(new DrawProperties(stroke));
		return new Polygon(witnessPolygon);
	}

	public void setWitnessPolygon(Polygon poly) {
		this.witnessPolygon = poly;
		witnessPolygon.clearPoints();
	}

	public int getInteractionAreaMargin() {
		return INTERACTION_AREA_MARGIN;
	}

	public void updateWitness(Class<? extends LayerElement> shapeClass,
			DrawConstants mode, ShapeProperties pp) {

		// enregistrement des preferences pour future création de formes
		if (Label.class.equals(shapeClass)) {
			Label witness = getWitnessLabel();
			ShapeUpdater.updateLabel(mode, pp, witness);
			witness.refreshShape();
			setWitnessLabel(witness);
		}

		else if (Polygon.class.equals(shapeClass)) {
			Polygon witness = (Polygon) getWitnessPolygon();
			ShapeUpdater.updatePolypointShape(mode, pp, witness);
			witness.refreshShape();
			setWitnessPolygon(witness);
		}

		else if (Polyline.class.equals(shapeClass)) {
			Polyline witness = (Polyline) getWitnessPolyline();
			ShapeUpdater.updatePolypointShape(mode, pp, witness);
			witness.refreshShape();
			setWitnessPolyline(witness);
		}

		else if (Symbol.class.equals(shapeClass)) {
			Symbol witness = getWitnessSymbol();
			ShapeUpdater.updateSymbol(mode, pp, witness);
			witness.refreshShape();
			setWitnessSymbol(witness);
		}

		else {
			throw new IllegalArgumentException("Unknown class: " + shapeClass);
		}

		observer.fireEvent(new DrawManagerEvent(
				DrawManagerEvent.WITNESSES_CHANGED, null));

	}

	/**
	 * Actualise les formes selectionnées en fonction du mode choisis.
	 * 
	 * <p>
	 * Les modifications sont enregistrees dans la chaine d'annulation.
	 * 
	 * @param shapeClass
	 * @param mode
	 * @param pp
	 */
	public void updateSelectedShapes(Class<? extends LayerElement> shapeClass,
			DrawConstants mode, ShapeProperties pp) {

		ShapeUpdater su = new ShapeUpdater(mode, pp);
		su.addShapeFilter(shapeClass);
		su.setOnlySelectedElements(true);

		su.run();
	}

	/**
	 * Retourne tous les élements sélectionnés ou null
	 * 
	 * @return
	 */
	public ArrayList<LayerElement> getSelectedElements() {

		GuiUtils.throwIfOnEDT();

		// projet non initialisé, arret
		if (projectm.isInitialized() == false)
			return null;

		// recuperer le calque actif
		MapLayer lay;
		try {
			lay = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			Log.error(e);
			return null;
		}

		// lister tous les élements sélectionnés
		ArrayList<LayerElement> elmts = new ArrayList<LayerElement>(20);
		for (LayerElement elmt : lay.getAllElements()) {
			if (elmt.isSelected()) {
				elmts.add(elmt);
			}
		}

		return elmts;
	}

}
