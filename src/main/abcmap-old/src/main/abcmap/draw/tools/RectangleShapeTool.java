package abcmap.draw.tools;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.RectangleShape;
import abcmap.draw.shapes.Ellipse;
import abcmap.draw.shapes.Image;
import abcmap.draw.shapes.Rectangle;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.project.layers.MapLayer;

public class RectangleShapeTool extends MapTool {

	private static final int MIN_SHAPE_WIDTH = 20;
	private static Class[] availablesShapes = new Class[] { Rectangle.class, Ellipse.class,
			Image.class };

	private Creator creator;
	private Modificator modificator;
	private Selector selector;
	private ShapeMover mover;

	private Class<? extends RectangleShape> shapeClass;

	private boolean allowCreation;

	/**
	 * Outils pour forme rectangulaire. Specifier allowCreation = false pour
	 * empecher la creation de formes.
	 * 
	 * @param shapeClass
	 * @param allowCreation
	 */
	public RectangleShapeTool(Class<? extends RectangleShape> shapeClass, boolean allowCreation) {
		super();

		if (Arrays.asList(availablesShapes).contains(shapeClass) == false) {
			throw new IllegalArgumentException("Classe non maitrise: " + shapeClass.getName());
		}

		// classe de la forme a manipuler
		this.shapeClass = shapeClass;

		// outil de creation de forme
		this.creator = new Creator();

		// outil de modification de forme
		this.modificator = new Modificator();

		// outil de selection de forme
		this.selector = new Selector();

		// outil de deplacement de forme
		this.mover = new ShapeMover();

		this.allowCreation = allowCreation;

	}

	public RectangleShapeTool(Class<? extends RectangleShape> shape) {
		this(shape, true);
	}

	@Override
	public void stopWorking() {

		// arret du createur de formes
		if (creator.isWorking())
			creator.setWorking(false);

		// arret du modificateur
		if (modificator.isWorking())
			modificator.setWorking(false);

	}

	/*
	 * Distribution des actions
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		super.mousePressed(arg0);

		// reagir uniquement au bouton gauche de la souris
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé: retour
		if (projectm.isInitialized() == false)
			return;

		// recuperer le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e) {
			Log.debug(e);
			return;
		}

		// position de la souris a l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// verifier toutes les poignees (meme celle du milieu !)
		search: for (LayerElement elmt : layer.getDrawShapesReversed()) {

			// analyser seulement les formes correspondantes et selectionnees
			if (shapeClass.isInstance(elmt) == false || elmt.isSelected() == false)
				continue search;

			// analyser toutes les poignees, meme celle hors de la zone
			// d'interaction
			for (Handle h : elmt.getHandles()) {

				if (h.getInteractionArea().contains(pS)) {

					if (h.getType().equals(Handle.FOR_MOVING)) {
						mover.mouseDragged(arg0);
					}

					else {
						modificator.startModification(arg0, (RectangleShape) elmt, h);
					}

					break search;
				}
			}
		}

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		// reagir uniquement au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé, retour
		if (projectm.isInitialized() == false)
			return;

		// repartition de l'information vers la tache active
		if (creator.isWorking()) {
			creator.mouseDragged(arg0);
		}

		else if (modificator.isWorking()) {
			modificator.mouseDragged(arg0);
		}

		else if (mover.isWorking()) {
			mover.mouseDragged(arg0);
		}

		// aucune tache active >> selection
		else {

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.debug(e);
				return;
			}

			// point de la souris à l'echelle
			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// si un element sous la souris et selectionne, deplacement
			boolean oneElementIsSelected = false;
			boolean pleaseMove = false;
			searching: for (LayerElement elmt : layer.getDrawShapesReversed()) {

				// analyser seulement les formes correspondantes
				if (shapeClass.isInstance(elmt) == false || elmt.isSelected() == false)
					continue searching;

				oneElementIsSelected = true;

				if (elmt.getInteractionArea().contains(mouseS)) {
					pleaseMove = true;
					mover.mouseDragged(arg0);
					break searching;
				}
			}

			// aucun element n'a ete modifie, creation si pas de selection
			if (pleaseMove == false) {
				if (oneElementIsSelected == false)
					creator.start(arg0);
			}

		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		super.mouseReleased(arg0);

		// reagir uniquement au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non initialisé, retour
		if (projectm.isInitialized() == false)
			return;

		// fin de modification
		if (modificator.isWorking()) {
			modificator.mouseReleased(arg0);
		}

		// fin de creation
		else if (creator.isWorking()) {
			creator.mouseReleased(arg0);
		}

		// fin de deplacement
		else if (mover.isWorking()) {
			mover.mouseReleased(arg0);
		}

		// selection par clic
		else if (creator.isWorking() == false && modificator.isWorking() == false) {
			selector.mouseReleased(arg0);
		}

	}

	/**
	 * Creation de formes
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class Creator {

		private boolean working;
		private Point origin;
		private RectangleShape shape;

		public Creator() {
			setWorking(false);
		}

		/**
		 * Premiere etape de la creation d'une forme: creer la forme puis
		 * l'ajouter au calque actif a la position de la souris.
		 * 
		 * @param arg0
		 */
		public void start(MouseEvent arg0) {

			// projet non initialise >> arret
			if (projectm.isInitialized() == false)
				return;

			// ampecher la creation de formes si demande
			if (allowCreation == false)
				return;

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.debug(e);
				return;
			}

			// creer la forme
			Constructor<? extends RectangleShape> ct;
			try {
				ct = shapeClass.getDeclaredConstructor();
				shape = ct.newInstance();
			} catch (Exception e) {
				return;
			}

			// ajout de la forme sans notification
			layer.addElement(shape, false);

			// positionner la forme
			Point sp = mapm.getScaledPoint(arg0.getPoint());
			shape.setPosition(sp);

			// enregistrement de l'operation pour annulation
			ElementsCancelOp op = cancelm.addDrawOperation(layer, shape);
			op.elementsHaveBeenAdded(true);

			// deselectionner tout
			projectm.setAllElementsSelected(false);
			shape.setSelected(true);

			// essentiel: point de depart pour tracer la forme
			origin = sp;

			setWorking(true);

			// rafraichir la carte et la forme
			shape.refreshShape();
			mapm.refreshMapComponent();

		}

		/**
		 * Deuxieme etape de la creation: tracer la forme et la redimensionner.
		 * 
		 * @param arg0
		 */
		public void mouseDragged(MouseEvent arg0) {

			// projet non initialise >> retour
			if (projectm.isInitialized() == false)
				return;

			// la premiere etape n'a pas ete effectuee >> retour
			if (working == false)
				return;

			// point non a l'echelle
			Point m = arg0.getPoint();

			// point a l'echelle
			Point ms = mapm.getScaledPoint(m);

			// Utiliser une copie du point d'origine
			Point originCopy = new Point(origin);

			// calcul des dimensions du rectangle
			int w = ms.x - originCopy.x;
			int h = ms.y - originCopy.y;
			Dimension dim = new Dimension(w, h);

			// compenser les dimensions si elles sont negatives
			// pour obtenir toujours des valeurs positives
			if (dim.width < 0) {
				int x = originCopy.x + dim.width;
				originCopy.setLocation(x, originCopy.y);
				dim.width = -dim.width;
			}
			// compenser les dimensions si elles sont negatives
			// pour obtenir toujours des valeurs positives
			if (dim.height < 0) {
				int y = originCopy.y + dim.height;
				originCopy.setLocation(originCopy.x, y);
				dim.height = -dim.height;
			}

			// Touche control maintenue: tracer un carre
			if (arg0.isControlDown() == true) {
				dim.width = dim.height;
			}

			// respecter la position minimale
			if (origin.x < 0)
				origin.setLocation(0, originCopy.y);

			if (origin.y < 0)
				origin.setLocation(originCopy.x, 0);

			// respecter les dimensions max
			// TODO A revoir
			Dimension max = drawm.getMaxDrawingDimensions();
			if (dim.width > max.getWidth())
				dim.width = max.width;

			if (dim.height > max.getHeight())
				dim.height = max.height;

			// modifier la forme
			shape.setPosition(originCopy);
			shape.setDimensions(dim);

			// rafraichir carte et forme
			shape.refreshShape();
			mapm.refreshMapComponent();

		}

		/**
		 * Troisieme etape de la creation: finalisation
		 * 
		 * @param arg0
		 */
		public void mouseReleased(MouseEvent arg0) {

			// projet non initialise >> retour
			if (projectm.isInitialized() == false)
				return;

			// les etapes precedentes n'ont pas ete remplies >> retour
			if (working == false)
				return;

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.debug(e);
				setWorking(false);
				return;
			}

			// forme inferieure a la taille minimale: retrait
			Dimension dim = shape.getRectangle().getSize();
			if (dim.width < MIN_SHAPE_WIDTH && dim.height < MIN_SHAPE_WIDTH) {
				layer.removeElement(shape);
			}

			// notifier et rafraichir la carte
			mapm.refreshMapComponent();
			projectm.fireElementsChanged();

			// reinitialisation
			setWorking(false);
		}

		public void setWorking(boolean working) {
			this.working = working;

			// reinitialisation si arret du travail
			if (working == false) {
				shape = null;
				origin = null;
			}
		}

		public boolean isWorking() {
			return working;
		}

	}

	/**
	 * Modificateur de forme
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class Modificator {

		private boolean working;
		private Handle handle;
		private ArrayList<Handle> handles;
		private double originalSideCoeff;
		private RectangleShape shape;

		public Modificator() {
			this.originalSideCoeff = 1d;
			setWorking(false);
		}

		/**
		 * Premiere etape de la modification: preparer la forme
		 * 
		 * @param arg0
		 * @param elmt
		 * @param h
		 */
		public void startModification(MouseEvent arg0, RectangleShape elmt, Handle h) {

			// projet non initialise: retour
			if (projectm.isInitialized() == false)
				return;

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.debug(e);
				return;
			}

			// forme et poignee a modifier
			shape = elmt;
			handle = h;
			handles = shape.getHandles();

			// deselectionner tout sauf la forme a modifier
			projectm.setAllElementsSelected(false);
			shape.setSelected(true);

			// enregistrement de l'operation pour annulation
			shape.getMementoManager().saveStateToRestore();
			cancelm.addDrawOperation(layer, shape);

			// coefficient de proportions pour conservation si la touche CTRL
			// est enfoncée
			Dimension2D dim = shape.getRectangle().getSize();
			this.originalSideCoeff = dim.getHeight() / dim.getWidth();

			// initialisation
			setWorking(true);
		}

		/**
		 * Deuxieme etape de la modification: tracage de la forme
		 * 
		 * @param arg0
		 */
		public void mouseDragged(MouseEvent arg0) {

			// les etapes anterieures n'ont pas ete correctement effectue
			if (isWorking() == false)
				return;

			// position de la souris à l'echelle
			Point scaledPoint = mapm.getScaledPoint(arg0.getPoint());

			// dimensions originale de la forme
			java.awt.Rectangle shapeRect = shape.getRectangle();

			// poignee du coin en haut a gauche
			// changer la position et adapter les dimensions
			if (RectangleShape.ULC_HANDLE_INDEX == handles.indexOf(handle)) {

				int ht = shape.getStroke().getHalfThickness();

				// nouvelle position du rectangle
				Point newPos = new Point(scaledPoint);

				// position du coin bas droit
				Point brc = new Point();
				brc.x = shapeRect.x + shapeRect.width;
				brc.y = shapeRect.y + shapeRect.height;

				// calculer les nouvelles dimensions
				Dimension newDims = new Dimension(shapeRect.getSize());

				// Appui sur CTRL: dimensions contraintes par coeff
				if (arg0.isControlDown() == true) {
					newDims.width = brc.x - scaledPoint.x - ht;
					newDims.height = (int) (newDims.width * originalSideCoeff);
				}

				// sinon dimensions libres
				else {
					newDims.width = brc.x - scaledPoint.x - ht;
					newDims.height = brc.y - scaledPoint.y - ht;
				}

				// verifier les dimensions min
				if (newDims.width < MIN_SHAPE_WIDTH) {
					newDims.width = MIN_SHAPE_WIDTH;
					newPos.x = brc.x - MIN_SHAPE_WIDTH - ht;
				}
				if (newDims.height < MIN_SHAPE_WIDTH) {
					newDims.height = MIN_SHAPE_WIDTH;
					newPos.y = brc.y - MIN_SHAPE_WIDTH - ht;
				}

				// repositionner le rectangle à l'emplacement de la souris
				shape.setPosition(newPos);
				shape.setDimensions(newDims);

			}

			// poignee du coin en bas droite
			// changer les dimensions sans changer la position
			else if (RectangleShape.BRC_HANDLE_INDEX == handles.indexOf(handle)) {

				// calcul des nouvelles dimensions
				Dimension newD = shape.getRectangle().getSize();

				// dimensions proportionnelles si CTRL enfonce
				if (arg0.isControlDown() == true) {
					newD.width = scaledPoint.x - shapeRect.x;
					newD.height = (int) (newD.width * originalSideCoeff);
				}

				// sinon dimensions libres
				else {
					newD.width = scaledPoint.x - shapeRect.x;
					newD.height = scaledPoint.y - shapeRect.y;
				}

				// verifier les dimensions minimales
				if (newD.width < MIN_SHAPE_WIDTH) {
					newD.width = MIN_SHAPE_WIDTH;
				}
				if (newD.height < MIN_SHAPE_WIDTH) {
					newD.height = MIN_SHAPE_WIDTH;
				}

				shape.setDimensions(newD);
			}

			// rafraichier la carte
			shape.refreshShape();
			mapm.refreshMapComponent();

		}

		/**
		 * Derniere etape de la modification: finalisation de la forme et
		 * norification
		 * 
		 * @param arg0
		 */
		public void mouseReleased(MouseEvent arg0) {

			// les precedentes etapes n'ont pas ete respectees
			if (isWorking() == false)
				return;

			// enregistrement pour annulation
			shape.getMementoManager().saveStateToRedo();

			// rafraichier la carte
			mapm.refreshMapComponent();

			// notifier les observateurs
			projectm.fireElementsChanged();

			// reinitialisation
			setWorking(false);
		}

		public void setWorking(boolean working) {
			this.working = working;

			// reinitialisation
			if (working == false) {
				handle = null;
				shape = null;
			}
		}

		public boolean isWorking() {
			return working;
		}

	}

	/**
	 * Outil de selection de forme
	 * 
	 * @author remipassmoilesel
	 *
	 */
	public class Selector {

		public void mouseReleased(MouseEvent arg0) {

			// position de la souris a l'echelle
			Point pS = mapm.getScaledPoint(arg0.getPoint());

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e) {
				Log.debug(e);
				return;
			}

			// si touche control non enfoncée, tout deslectionner
			if (arg0.isControlDown() == false) {
				projectm.setAllElementsSelected(false);
			}

			// iterer les elements du calque
			selection: for (LayerElement elmt : layer.getDrawShapesReversed()) {

				// analyser uniquement les formes concernees
				if (shapeClass.isInstance(elmt) == false)
					continue selection;

				// verifier si la souris est sur la forme
				if (elmt.getInteractionArea().contains(pS) == false)
					continue selection;

				// selectionner l'element
				elmt.setSelected(true);

				// arret de la recherche
				break selection;
			}

			// rafraichir la carte
			mapm.refreshMapComponent();

			// notifications
			projectm.fireSelectionChanged();
		}

	}

}
