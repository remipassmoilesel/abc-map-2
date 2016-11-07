package abcmap.draw.tools;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.shapes.Label;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;

/**
 * Outil de création d'étiquettes de texte
 * 
 * @author remipassmoilesel
 *
 */
public class LabelTool extends MapTool {

	private ShapeMover mover;
	private LabelModificator modificator;

	public LabelTool() {
		super();
		this.mover = new ShapeMover();
		this.modificator = new LabelModificator();
	}

	@Override
	public void stopWorking() {
		if (modificator.isWorking() == true) {
			modificator.mouseReleased(null);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		// ne repondre qu'au bouton gauche de la souris
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé: arret
		if (projectm.isInitialized() == false) {
			return;
		}

		// recuperer le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// position de la souris à l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// un deplacement est en cours: transmission de l'information
		if (mover.isWorking() == true) {
			mover.mouseDragged(arg0);
		}

		// pas de deplacement en cours, recherche d'un element a deplacer
		else {
			for (LayerElement e : layer.getDrawShapesReversed()) {

				// analyse des elements concernes seulement
				if (e instanceof Label == false || e.isSelected() == false)
					continue;

				// la souris est sur l'element
				if (e.getInteractionArea().contains(pS)) {
					mover.mouseDragged(arg0);
					break;
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		// ne repondre qu'au bouton gauche de la souris
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé: arret
		if (projectm.isInitialized() == false)
			return;

		// recuperer le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// deplacement en cours: fin du deplacment
		if (mover.isWorking() == true) {
			mover.mouseReleased(arg0);
			return;
		}

		// position de la souris a l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// compter le nombre de clics
		int cc = arg0.getClickCount();

		// pas de modificiation en cours et double clic: modification ou
		// creation
		if (cc == 2 && modificator.isWorking() == false) {

			// recherche d'un element à modifier
			boolean modification = false;
			for (LayerElement e : layer.getDrawShapesReversed()) {

				// analyse des elements concernes seulement
				if (e instanceof Label == false || e.isSelected() == false)
					continue;

				// l'element est sous la souris
				if (e.getInteractionArea().contains(pS)) {
					modificator.setShape((Label) e);
					modificator.mouseDoubleClicked(arg0);
					modification = true;
					break;
				}
			}

			// pas d'objet a modifier: creation
			if (modification == false) {

				// creer un nouvel element
				Label label = drawm.getWitnessLabel();
				label.setSelected(true);
				label.setPosition(pS);

				// ajout de l'element
				layer.addElement(label);

				// modification
				modificator.setShape((Label) label);
				modificator.mouseDoubleClicked(arg0);

				// rafraichir l'element
				label.refreshShape();

			}

		}

		// modification en cours et clic hors de la zone de saisie: fin de la
		// modif
		else if (modificator.isWorking() == true) {
			modificator.mouseReleased(arg0);
		}

		// aucun des cas ci dessus: selection
		else {

			// touche control non enfoncée: deselectionner tout
			if (arg0.isControlDown() == false)
				projectm.setAllElementsSelected(false);

			for (LayerElement e : layer.getDrawShapesReversed()) {

				// analyser uniquement les etiquettes de texte
				if (e instanceof Label == false)
					continue;

				// la souris est sur l'etiquette
				if (e.getInteractionArea().contains(pS)) {
					e.setSelected(true);
					e.refreshShape();
					break;
				}
			}

			// notfications
			projectm.fireSelectionChanged();
			mapm.refreshMapComponent();
		}
	}

	/**
	 * Modificateur d'etiquette de texte
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class LabelModificator {

		private boolean working;
		private Label label;
		private LabelEditorPanel modifArea;

		public LabelModificator() {
			setWorking(false);
		}

		/**
		 * Double clic: début des modifications
		 * 
		 * @param arg0
		 */
		public void mouseDoubleClicked(MouseEvent arg0) {

			if (isWorking())
				return;

			// commencer les modifications
			modifArea = new LabelEditorPanel();
			modifArea.startModification(label);

			mapm.refreshMapComponent();

			setWorking(true);
		}

		/**
		 * Simple clic hors de la zone: arret de la modification
		 * 
		 * @param arg0
		 */
		public void mouseReleased(MouseEvent arg0) {

			// si arg est null: reinitialisation pour arret du travail
			if (arg0 == null) {
				setWorking(false);
				// pas de return ici
			}

			// ne régair qu'au bouton gauche de la souris
			else if (SwingUtilities.isLeftMouseButton(arg0) == false)
				return;

			// projet non intialisé: retour
			if (projectm.isInitialized() == false) {
				setWorking(false);
				return;
			}

			// position de la souris a l'echelle
			Point mS = arg0 != null ? mapm.getScaledPoint(arg0.getPoint()) : null;

			// arret uniquement si demande ou si clic hors de la zone de saisie
			if (arg0 == null || modifArea.getBounds().contains(mS) == false) {

				// enlever la zone de modification
				modifArea.stopModification();

				// l'element est vide: retrait de l'element
				if (modifArea.getTrimText().matches("^\\s*$") == true) {
					try {
						projectm.getActiveLayer().removeElement(label);
					} catch (MapLayerException e) {
						Log.debug(e);
						setWorking(false);
						return;
					}
				}

				// l'element contient du texte: affichage de l'element
				else {
					label.setText(modifArea.getTrimText());
					label.refreshShape();
				}

				projectm.fireSelectionChanged();
				mapm.refreshMapComponent();

				setWorking(false);
			}

		}

		public void setShape(Label l) {
			this.label = l;
		}

		public boolean isWorking() {
			return working;
		}

		public void setWorking(boolean working) {
			this.working = working;

			if (working == false) {
				this.label = null;
				this.modifArea = null;
			}
		}

	}

}
