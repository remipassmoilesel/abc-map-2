package abcmap.draw.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Symbol;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;

public class SymbolTool extends MapTool {

	private ShapeMover mover;

	public SymbolTool() {
		super();
		this.mover = new ShapeMover();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// ne reagir qu'au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// vrifier le projet
		if (projectm.isInitialized() == false)
			return;

		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// Creer un element en double cliquant
		if (arg0.getClickCount() > 1 && mover.isWorking() == false) {

			// tout deselectionner
			projectm.setAllElementsSelected(false);

			// creer un symbole avec les parametres choisis
			Symbol sbl = drawm.getWitnessSymbol();
			sbl.setSelected(true);
			sbl.setPosition(pS);
			sbl.refreshShape();

			// ajout au calque actif
			layer.addElement(sbl);

			// enregistrement de l'operation pour annulation
			ElementsCancelOp op = cancelm.addDrawOperation(layer, sbl);
			op.elementsHaveBeenAdded(true);

			// notification
			projectm.fireSelectionChanged();
			mapm.refreshMapComponent();
		}

		else {

			// fin de dplacment
			if (mover.isWorking() == true) {
				mover.mouseReleased(arg0);
				return;
			}

			else {

				boolean selectionChanged = false;

				// Selectionner un lment
				if (arg0.isControlDown() == false) {
					projectm.setAllElementsSelected(false);
					selectionChanged = true;
				}

				selection: for (LayerElement e : layer.getDrawShapesReversed()) {
					if (e instanceof Symbol) {
						if (e.getInteractionArea().contains(pS)) {
							Symbol sbl = (Symbol) e;
							sbl.setSelected(true);
							sbl.refreshShape();
							selectionChanged = true;
							break selection;
						}
					}
				}

				if (selectionChanged) {
					projectm.fireSelectionChanged();
				}

				mapm.refreshMapComponent();

			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		// ne reagir qu'au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// verifier le projet
		if (projectm.isInitialized() == false)
			return;

		// err.pr("mouseReleased");

		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// position de la souris à l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		if (mover.isWorking() == false) {

			for (LayerElement e : layer.getDrawShapesReversed()) {

				if (e instanceof Symbol == false || e.isSelected() == false)
					continue;

				if (e.getInteractionArea().contains(pS)) {
					mover.mouseDragged(arg0);
					break;
				}
			}
		}

		else {
			mover.mouseDragged(arg0);
		}

	}

}
