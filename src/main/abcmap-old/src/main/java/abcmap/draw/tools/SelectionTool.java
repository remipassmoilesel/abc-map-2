package abcmap.draw.tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Tile;
import abcmap.project.layers.MapLayer;

/**
 * Outil de sélection d'objet sur la carte
 * 
 * @author Internet
 * 
 */
public class SelectionTool extends MapTool {

	private RectangleSelector multiSelector;
	private ShapeMover mover;
	private Class shapeFilter;
	private boolean excludeTiles;

	public SelectionTool() {
		this.mover = new ShapeMover();
		this.multiSelector = new RectangleSelector();

		excludeTiles(false);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		// seulement clic gauche
		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// les elements bossent dj
		if (multiSelector.isWorking()) {
			multiSelector.mouseDragged(arg0);
		}

		else if (mover.isWorking()) {
			mover.mouseDragged(arg0);
		}

		else {

			// position de la souris à l'echelle
			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// recuperer le calque actif
			MapLayer layer = checkProjetAndReturnActiveLayer();
			if (layer == null)
				return;

			boolean startMove = false;
			searching: for (LayerElement elmt : layer.getAllElementsReversed()) {

				// exclure les elements non selectionnés
				if (elmt.isSelected() == false)
					continue;

				// exclure les tuiles si demandé
				if (excludeTiles == true && elmt instanceof Tile)
					continue;

				boolean mouseOnElement = false;

				// verifier la zone d'interaction
				if (elmt.getInteractionArea().contains(mouseS)) {
					mouseOnElement = true;
				}

				// verifier les poignees
				if (mouseOnElement == false) {
					for (Handle h : elmt.getHandles()) {
						if (h.getInteractionArea().contains(mouseS)) {
							mouseOnElement = true;
							break;
						}
					}
				}

				if (mouseOnElement == true) {
					startMove = true;
					mover.mouseDragged(arg0);
					break searching;
				}
			}

			// sinon, tracage d'une forme de selection
			if (startMove == false) {
				multiSelector.mouseDragged(arg0);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// seulement clic gauche
		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// les elements bossent dj
		if (mover.isWorking()) {
			mover.mouseReleased(arg0);
		}

		else if (multiSelector.isWorking()) {
			multiSelector.mouseReleased(arg0);
		}

		else {

			// recuperer le calque actif
			MapLayer layer = checkProjetAndReturnActiveLayer();
			if (layer == null)
				return;

			// point à l'echelle
			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// verifier la touche ctrl
			unselectAllIfCtrlNotPressed(arg0);

			// clic sur une forme: selection
			searching: for (LayerElement elmt : layer.getAllElementsReversed()) {

				if (excludeTiles == true && elmt instanceof Tile)
					continue;

				if (shapeFilter != null && shapeFilter.isInstance(elmt) == false)
					continue;

				if (elmt.getInteractionArea().contains(mouseS) == true) {
					elmt.setSelected(true);
					break searching;
				}
			}

			mapm.refreshMapComponent();

			// chaque clic, notifier changement de selection
			projectm.fireSelectionChanged();
		}
	}

	public void excludeTiles(boolean val) {
		excludeTiles = val;
		mover.excludeTiles(val);
	}

	public void setShapeFilter(Class filter) {
		mover.setShapeFilter(filter);
		shapeFilter = filter;
	}

	public boolean isExcludingTiles() {
		return excludeTiles;
	}

	public Class getShapeFilter() {
		return shapeFilter;
	}

	@Override
	public void drawOnCanvas(Graphics2D g2d) {
		if (multiSelector != null && multiSelector.isWorking()) {
			multiSelector.draw(g2d);
		}
	}

	/**
	 * Objet de sélection multiple. Permet de tracer un rectangle et de
	 * sélectionner tous les objets compris dedans.
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class RectangleSelector extends SimpleRectangleTracer {

		public RectangleSelector() {
			setRectangleColor(drawm.getSelectionColor());
			setRectangleStroke(drawm.getSelectionStroke());
			setDeleteRectangleOnMouseReleased(false);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);

			// recuperer le calque actif
			MapLayer activeLayer = checkProjetAndReturnActiveLayer();
			if (activeLayer == null)
				return;

			// recuperer le point à l'echelle
			Point ms = mapm.getScaledPoint(arg0.getPoint());

			// itérer les elements du projet
			for (LayerElement elmt : activeLayer.getAllElementsReversed()) {

				if (excludeTiles == true && elmt instanceof Tile)
					continue;

				if (shapeFilter != null && shapeFilter.isInstance(elmt) == false)
					continue;

				// verifier le rectangle de selection
				if (rectangle != null && rectangle.contains(elmt.getMaximumBounds())) {
					elmt.setSelected(true);
				}

				// verifier le point de la souris
				if (elmt.getInteractionArea().contains(ms)) {
					elmt.setSelected(true);
				}

			}

			// notifier les observateurs
			projectm.fireElementsChanged();

		}

	}

}
