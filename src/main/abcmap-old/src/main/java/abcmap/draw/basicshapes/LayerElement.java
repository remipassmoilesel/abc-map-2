package abcmap.draw.basicshapes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;

import abcmap.cancel.memento.HasMementoManager;
import abcmap.cancel.memento.MementoManager;
import abcmap.cancel.memento.PropertiesContainerCanceler;
import abcmap.draw.links.LinkRessource;
import abcmap.managers.ConfigurationManager;
import abcmap.managers.DrawManager;
import abcmap.managers.ImportManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;

/**
 * Element de base d'un calque.
 * 
 * @author remipassmoilesel
 *
 */
public abstract class LayerElement implements
		HasMementoManager<PropertiesContainer>, AcceptPropertiesContainer,
		Drawable {

	/**
	 * Dimensions maximum de l'objet. getLocation() doit renvoyer la position de
	 * l'objet.
	 */
	protected java.awt.Rectangle maximumBounds;

	protected MapManager mapCtrl;
	protected ProjectManager projectm;
	protected DrawManager drawm;
	protected ConfigurationManager confm;
	protected ImportManager importm;

	protected boolean selected;
	protected ArrayList<Handle> handles;
	protected DrawProperties stroke;
	protected boolean drawInteractionArea;
	protected Area interactionArea;
	protected LinkRessource linkRessource;
	protected boolean drawLinkMark;
	protected Point position;

	private PropertiesContainerCanceler canceler;

	public LayerElement() {

		this.drawm = MainManager.getDrawManager();
		this.mapCtrl = MainManager.getMapManager();
		this.projectm = MainManager.getProjectManager();
		this.confm = MainManager.getConfigurationManager();
		this.importm = MainManager.getImportManager();

		this.maximumBounds = new Rectangle();
		this.stroke = drawm.getNewStroke();
		this.selected = false;
		this.handles = new ArrayList<Handle>();
		this.canceler = new PropertiesContainerCanceler(this);
		this.drawInteractionArea = false;
		this.interactionArea = new Area();
		this.linkRessource = null;
		this.drawLinkMark = false;
		this.position = new Point();

	}

	public abstract LayerElement duplicate();

	protected void drawLinkMark(Graphics2D g, boolean forceDraw) {
		if (linkRessource != null && (drawLinkMark | forceDraw)) {
			Point p = maximumBounds.getLocation();
			linkRessource.draw(g, LinkRessource.getDefaultPosition(p));
		}
	}

	public void drawLinkMark(boolean val) {
		drawLinkMark = val;
	}

	/**
	 * Rafraichir les poignées de la liste "handles"
	 */
	protected void refreshHandles() {

		if (handles == null)
			return;

		for (Handle h : handles) {
			h.refreshShape();
		}

	}

	/**
	 * Rafraichir les poignées de la liste "handles"
	 */
	protected void drawHandles(Graphics2D g) {

		if (handles == null)
			return;

		for (Handle h : handles) {
			h.draw(g);
		}

	}

	/**
	 * Retourne la position du point le plus en haut et à gauche
	 * 
	 * @return
	 */
	public Point getPosition() {
		return new Point(position);
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setPosition(int x, int y) {
		this.position.x = x;
		this.position.y = y;
	}

	public boolean isSelected() {
		return new Boolean(selected);
	}

	public void setSelected(boolean val) {
		this.selected = val;
	}

	public ArrayList<Handle> getHandles() {
		return handles;
	}

	/**
	 * Retourne les dimensions maximum de l'objet
	 */
	@Override
	public java.awt.Rectangle getMaximumBounds() {
		return new java.awt.Rectangle(maximumBounds);
	}

	public DrawProperties getStroke() {
		return new DrawProperties(stroke);
	}

	public void setStroke(DrawProperties stroke) {
		this.stroke = stroke;
	}

	public void drawInteractionArea(boolean v) {
		drawInteractionArea = v;
	}

	@Override
	public MementoManager getMementoManager() {
		return canceler;
	}

	public Area getInteractionArea() {
		return new Area(interactionArea);
	}

	public LinkRessource getLinkRessources() {
		return linkRessource;
	}

	public void setLinkRessource(LinkRessource linkRessources) {
		this.linkRessource = linkRessources;
	}

	/**
	 * Retourne un échantillon de cet objet c'est à dire un nouvel objet de
	 * dimensions maximales 'maxWidth' et 'maxHeight' comportant les mêmes
	 * caractéristiques.
	 * 
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public abstract LayerElement getSample(int maxWidth, int maxHeight);

}
