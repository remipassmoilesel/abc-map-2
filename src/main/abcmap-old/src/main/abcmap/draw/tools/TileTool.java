package abcmap.draw.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Tile;
import abcmap.project.layers.MapLayer;

public class TileTool extends MapTool {

	private ShapeMover mover;

	public TileTool() {
		super();

		this.mover = new ShapeMover();
		mover.excludeTiles(false);
		mover.setShapeFilter(Tile.class);

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// deplacement
		if (mover.isWorking()) {
			mover.mouseDragged(arg0);
		}

		else {

			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// si un element est selectionne et sous la souris, deplacement
			MapLayer layer = this.checkProjetAndReturnActiveLayer();
			if (layer == null) {
				return;
			}

			for (LayerElement elmt : layer.getTilesReversed()) {
				if (elmt.isSelected() && elmt.getInteractionArea().contains(mouseS)) {
					mover.mouseDragged(arg0);
					break;
				}
			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// ne reagir qu'au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé: arret
		if (projectm.isInitialized() == false)
			return;

		// les elements bossent deje
		if (mover.isWorking()) {
			mover.mouseReleased(arg0);
		}

		else {

			// selection
			MapLayer layer = this.checkProjetAndReturnActiveLayer();
			if (layer == null) {
				return;
			}

			// point de la souris à l'echelle
			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// deselectionner tout
			unselectAllIfCtrlNotPressed(arg0);

			// clic sur une forme selection
			selection: for (LayerElement elmt : layer.getTilesReversed()) {
				if (elmt.getInteractionArea().contains(mouseS) == true) {
					elmt.setSelected(true);
					break selection;
				}
			}

			// notifications
			projectm.fireSelectionChanged();
			mapm.refreshMapComponent();
		}

	}

}
