package abcmap.draw.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JPanel;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.project.layers.MapLayer;

/**
 * Objet permettant le deplacement d'elements de calques. N'est pas vraiment un
 * outil, juste une partie d'outil.
 * 
 * @author remipassmoilesel
 *
 */
public class ShapeMover extends MapTool {

	private boolean working;
	private boolean excludeTiles;
	private Point prevPos;
	private Class shapeFilter;
	private ArrayList<LayerElement> toMove;

	public ShapeMover() {

		this.working = false;
		this.prevPos = null;
		this.toMove = new ArrayList<LayerElement>();
		this.shapeFilter = null;

		// inclure les tuiles dans le deplacement par defaut
		this.excludeTiles = false;

	}

	public void setShapeFilter(Class filter) {
		this.shapeFilter = filter;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// recuperer le calque actif
		MapLayer layer = checkProjetAndReturnActiveLayer();
		if (layer == null) {
			setWorking(false);
			return;
		}

		// point a l'echelle
		Point mouseS = mapm.getScaledPoint(arg0.getPoint());

		// premier mouvement
		if (working == false) {

			// curseur move
			JPanel map = (JPanel) arg0.getSource();
			map.setCursor(guim.getMoveCursor());

			toMove = new ArrayList<LayerElement>(20);

			// conserver les element selectionnes dans une liste
			// puis sauvegarder leur etat

			ArrayList<LayerElement> list;
			if (excludeTiles) {
				list = new ArrayList<LayerElement>(layer.getDrawShapesReversed());
			}

			else {
				list = new ArrayList<LayerElement>(layer.getAllElementsReversed());
			}

			searching: for (LayerElement elmt : list) {

				// filtre de type
				if (shapeFilter != null) {

					// les elements ne sont pas du bon type: deselection
					if (shapeFilter.isInstance(elmt) == false) {
						elmt.setSelected(false);
						continue searching;
					}
				}
				
				if (elmt.isSelected() == true) {
					toMove.add(elmt);
					elmt.getMementoManager().saveStateToRestore();
				}

			}

			// enregistrer l'operation pour annulation
			cancelm.addDrawOperation(layer, new ArrayList<LayerElement>(toMove));

			// preparation du deplacement
			setWorking(true);
		}

		// autres mouvements
		else {

			// calcul du deplacement relatif
			int mx = mouseS.x - prevPos.x;
			int my = mouseS.y - prevPos.y;

			// deplacer les objets
			for (LayerElement elmt : toMove) {
				
				Point newPos = new Point(elmt.getPosition());
				newPos.x += mx;
				newPos.y += my;
				
				elmt.setPosition(newPos);
				elmt.refreshShape();
			}

		}

		// conserver la dernière position pour calculs de mouvements relatifs
		prevPos = new Point(mouseS);

		// rafraichir la carte
		mapm.refreshMapComponent();

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// seulement clic gauche
		if (checkProjectAndLeftClick(arg0) == false) {
			return;
		}

		if (working == false)
			return;

		// sauver les objets
		for (LayerElement elmt : toMove) {
			elmt.getMementoManager().saveStateToRedo();
		}

		// fin du deplacement
		setWorking(false);

		// curseur normal
		JPanel map = (JPanel) arg0.getSource();
		map.setCursor(guim.getDrawingCursor());

		projectm.fireSelectionChanged();
	}

	public boolean isWorking() {
		return working;
	}

	public void setWorking(boolean working) {
		this.working = working;

		if (working == false) {
			toMove = null;
			prevPos = null;
		}
	}

	public void excludeTiles(boolean val) {
		this.excludeTiles = val;
	}

}
