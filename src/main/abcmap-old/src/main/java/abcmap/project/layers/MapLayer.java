package abcmap.project.layers;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

import abcmap.cancel.memento.HasMementoManager;
import abcmap.cancel.memento.MementoManager;
import abcmap.cancel.memento.PropertiesContainerCanceler;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Tile;
import abcmap.events.ProjectEvent;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.LayerProperties;
import abcmap.project.properties.PropertiesContainer;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.lists.ListenableContainer;
import abcmap.utils.notifications.NotificationManager;

/**
 * Conteneur d'objet représentant un calque.
 * <p>
 * L'objet le plus bas est d'index 0.
 * <p>
 * Les objets plus hauts ont des index > 0.
 * 
 * @author remipassmoilesel
 *
 */
public class MapLayer implements AcceptPropertiesContainer,
		HasMementoManager<PropertiesContainer> {

	private static final int MAX_NAME_LENGTH = 200;

	private static int instancesCounter = 0;
	private boolean visible;
	private String name;
	private float opacity;

	private ListenableContainer<LayerElement> shapesList;
	private ListenableContainer<LayerElement> tilesList;

	private ProjectManager project;

	private NotificationManager om;

	private boolean notificationsEnabled;

	private PropertiesContainerCanceler canceler;

	public MapLayer() {

		// se servir de l'ecouteur du projet
		project = MainManager.getProjectManager();
		if (project.isInitialized() == false)
			throw new IllegalStateException("Project non initialized");

		this.om = project.getNotificationManager();

		this.shapesList = new ListenableContainer<LayerElement>();
		shapesList.setEventClass(ProjectEvent.class);
		shapesList.setEventName(ProjectEvent.LAYERS_LIST_CHANGED);
		shapesList.setNotificationManager(project.getNotificationManager());

		this.tilesList = new ListenableContainer<LayerElement>();
		tilesList.setEventClass(ProjectEvent.class);
		tilesList.setEventName(ProjectEvent.LAYERS_LIST_CHANGED);
		tilesList.setNotificationManager(project.getNotificationManager());

		// proprietes
		setNotificationsEnabled(true);

		instancesCounter++;
		this.name = "Calque " + instancesCounter;
		this.visible = true;
		this.opacity = 1.0f;

		this.canceler = new PropertiesContainerCanceler(this);

	}

	public MapLayer(MapLayer lay) {
		this();

		shapesList.setNotificationsEnabled(false);
		copyContainer(lay.shapesList, this.shapesList);
		shapesList.setNotificationsEnabled(true);

		tilesList.setNotificationsEnabled(false);
		copyContainer(lay.tilesList, this.tilesList);
		tilesList.setNotificationsEnabled(true);

		// proprietes
		this.name = new String(lay.name);
		this.visible = lay.visible;
		this.opacity = lay.opacity;
	}

	public static void copyContainer(ListenableContainer source,
			ListenableContainer dest) {
		for (Object elmt : source) {
			dest.add(((LayerElement) elmt).duplicate());
		}
	}

	public ListenableContainer<LayerElement> getDrawShapes() {
		return shapesList;
	}

	public ListenableContainer<LayerElement> getTiles() {
		return tilesList;
	}

	public ArrayList<LayerElement> getDrawShapesReversed() {
		ArrayList<LayerElement> list = shapesList.getCopy();
		Collections.reverse(list);
		return list;
	}

	public ArrayList<LayerElement> getTilesReversed() {
		ArrayList<LayerElement> list = tilesList.getCopy();
		Collections.reverse(list);
		return list;
	}

	public ArrayList<LayerElement> getAllElements() {
		ArrayList<LayerElement> list = tilesList.getCopy();
		list.addAll(shapesList.getCopy());
		return list;
	}

	public ArrayList<LayerElement> getAllElementsReversed() {
		ArrayList<LayerElement> list = getAllElements();
		Collections.reverse(list);
		return list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {

		if (name.length() > MAX_NAME_LENGTH)
			name = name.substring(0, MAX_NAME_LENGTH);
		this.name = name;

		if (notificationsEnabled) {

		}
		fireEvent(ProjectEvent.NAME_CHANGED);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean v) {
		visible = v;
		if (notificationsEnabled)
			fireEvent(ProjectEvent.VISIBILITY_CHANGED);
	}

	public void setAllElementsSelected(boolean v) {

		for (LayerElement e : shapesList) {
			e.setSelected(v);
		}

		for (LayerElement e : tilesList) {
			e.setSelected(v);
		}

	}

	/**
	 * Ajouter un element au calque. Ne pas utiliser pour une tuile, voir
	 * ajouterTuile();
	 * 
	 * @param elmt
	 */
	public void addElement(LayerElement elmt) {
		addElement(elmt, true);
	}

	/**
	 * Ajouter un element avec ou sans notification
	 * 
	 * @param elmt
	 * @param notify
	 */
	public void addElement(LayerElement elmt, boolean notify) {

		// determiner la liste ou ajouter
		ListenableContainer<LayerElement> list = elmt instanceof Tile ? tilesList
				: shapesList;

		if (notify == false)
			list.setNotificationsEnabled(false);

		list.add(elmt);

		if (notify == false)
			list.setNotificationsEnabled(true);
	}

	/**
	 * Enlever un élément
	 * 
	 * @param elmt
	 */
	public void removeElement(LayerElement elmt) {

		// determiner la liste ou ajouter
		ListenableContainer<LayerElement> list = elmt instanceof Tile ? tilesList
				: shapesList;

		list.remove(elmt);
	}

	public void setNotificationsEnabled(boolean b) {
		this.notificationsEnabled = b;
		tilesList.setNotificationsEnabled(b);
		shapesList.setNotificationsEnabled(b);
	}

	/**
	 * Dessiner un calque
	 * 
	 * @param g
	 */
	public void draw(Graphics2D g, String mode) {

		// creer de nouveaux graphics pour la transparence
		Graphics2D g2 = (Graphics2D) g.create();

		// ameliorer la qualité de rendu
		GuiUtils.applyQualityRenderingHints(g2);

		// ajout de la transparence
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				opacity));

		// dessiner les tuiles
		for (LayerElement e : tilesList) {
			e.draw(g2, mode);
		}

		// puis dessiner les formes
		for (LayerElement e : shapesList) {
			e.draw(g2, mode);
		}

	}

	/**
	 * Fixer l'opacité en float 0.1 < ... < 1
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity) {
		this.opacity = opacity;
		fireEvent(ProjectEvent.OPACITY_CHANGED);

	}

	public float getOpacity() {
		return opacity;
	}

	public MapLayer duplicate() {
		return new MapLayer(this);
	}

	/**
	 * Affecter les proprietes de l'objet au calque. Si les proprietes sont
	 * nulles, pas de modifications.
	 */
	@Override
	public void setProperties(PropertiesContainer properties) {

		if (properties == null)
			return;

		LayerProperties pp = (LayerProperties) properties;

		if (pp.name != null)
			this.name = pp.name;

		if (pp.visible != null)
			this.visible = pp.visible;

		if (pp.opacity != null)
			this.opacity = pp.opacity;

		if (notificationsEnabled)
			fireEvent(ProjectEvent.LAYER_LOADED);

	}

	@Override
	public PropertiesContainer getProperties() {

		LayerProperties pp = new LayerProperties();

		pp.name = this.name;
		pp.visible = this.visible;
		pp.opacity = this.opacity;

		return pp;
	}

	public static void setInstancesCounter(int i) {
		instancesCounter = 0;
	}

	@Override
	public MementoManager<PropertiesContainer> getMementoManager() {
		return canceler;
	}

	private void fireEvent(String name) {
		om.fireEvent(new ProjectEvent(name, null));
	}

}
