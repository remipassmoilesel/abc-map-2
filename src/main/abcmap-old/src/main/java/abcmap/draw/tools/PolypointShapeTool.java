package abcmap.draw.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import abcmap.cancel.ElementsCancelOp;
import abcmap.draw.basicshapes.Handle;
import abcmap.draw.basicshapes.LayerElement;
import abcmap.draw.basicshapes.PolypointShape;
import abcmap.draw.shapes.Polygon;
import abcmap.draw.shapes.Polyline;
import abcmap.exceptions.MapLayerException;
import abcmap.managers.Log;
import abcmap.managers.stub.MainManager;
import abcmap.project.layers.MapLayer;

public class PolypointShapeTool extends MapTool {

	public static final String POLYPOINT_NORMAL = "POLYPOINT_NORMAL";
	public static final String POLYPOINT_ADD_NODES = "POLYPOINT_ADD_NODES";
	public static final String POLYPOINT_REMOVE_NODES = "POLYPOINT_REMOVE_NODES";

	// options du createur de polyforme
	private static String CONSTRUCT_AT_FIRST_POINT = "CONSTRUCT_AT_FIRST_POINT";
	private static String CONSTRUCT_AT_LAST_POINT = "CONSTRUCT_AT_LAST_POINT";

	// trait de tracage
	private static final Color TRACING_COLOR = Color.magenta;
	private static final Stroke TRACING_STROKE = new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_MITER, 5.0f, new float[] { 5.0f }, 0.0f);

	private PolyPointCreator creator;
	private PolyPointMover mover;
	private Class shapeClass;

	public PolypointShapeTool(Class shape) {
		super();

		// actions et mode par dfaut
		this.creator = new PolyPointCreator();
		this.mover = new PolyPointMover();

		// mode de fonctionnement par défaut
		this.mode = POLYPOINT_NORMAL;

		this.shapeClass = shape;

	}

	public Class getShapeClass() {
		return shapeClass;
	}

	@Override
	public void stopWorking() {
		if (creator.isWorking())
			creator.setWorking(false);
		if (mover.isWorking())
			mover.setWorking(false);
	}

	/**
	 * Distribution des actions
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		super.mouseReleased(arg0);

		// ne reagir qu'au bouton gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// verifier le projet
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

		// point a l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// forme a modifier
		PolypointShape shapeSelected = null;

		// un objet est en cours de deplacement
		if (mover.isWorking()) {
			mover.mouseReleased(arg0);
		}

		// un objet est en cours de creation
		else if (creator.isWorking()) {
			creator.mouseReleased(arg0);
		}

		else {

			// recherche d'un element sous la souris
			searching: for (LayerElement e : layer.getDrawShapesReversed()) {

				// ignorer les elements non selectionnes et de classes
				// differentes
				if (shapeClass.isInstance(e) == false || e.isSelected() == false)
					continue searching;

				// recherche sur le corps de l'objet
				if (e.getInteractionArea().contains(pS)) {
					shapeSelected = (PolypointShape) e;
					break searching;
				}

				// recherche sur les poignées
				else {
					for (Handle h : e.getHandles()) {
						if (h.getInteractionArea().contains(pS)) {
							shapeSelected = (PolypointShape) e;
							break searching;
						}
					}

				}
			}

			// aucun element n'est selectionne sous la souris
			if (shapeSelected == null) {

				// selection
				// chercher un element sous la souris
				PolypointShape shapeToSelect = null;
				boolean oneElementIsSelected = false;
				searching: for (LayerElement e : layer.getDrawShapesReversed()) {

					if (shapeClass.isInstance(e) == false)
						continue searching;

					// tester la selection d'un objet pour selection/deselection
					// d'objets
					if (e.isSelected() == true)
						oneElementIsSelected = true;

					if (e.getInteractionArea().contains(pS)) {
						shapeToSelect = (PolypointShape) e;
						break searching;
					}

					// prendre en compte les poignées
					else {
						for (Handle h : e.getHandles()) {
							if (h.getInteractionArea().contains(pS)) {
								shapeToSelect = (PolypointShape) e;
								break searching;
							}
						}
					}
				}

				// touche control non maintenu: deselection
				if (arg0.isControlDown() == false) {
					projectm.setAllElementsSelected(false);
				}

				// une forme doit etre selectionnee
				if (shapeToSelect != null) {
					shapeToSelect.setSelected(true);
					projectm.fireSelectionChanged();
				}

				// aucune forme ne doit etre selectionnee et des forme ont pu
				// etre deselectionnee
				else if (oneElementIsSelected) {
					// notifier de la deselection precedente
					if (arg0.isControlDown() == false) {
						projectm.fireSelectionChanged();
					}
				}

				// creation d'une nouvelle forme si aucune selection/deselection
				else if (shapeToSelect == null && oneElementIsSelected == false) {

					if (POLYPOINT_NORMAL.equals(mode)) {
						creator.setShape(CONSTRUCT_AT_LAST_POINT, null);
						creator.mouseReleased(arg0);
					}
				}

				// rafraichir la carte
				mapm.refreshMapComponent();

			}

			// un element est selectionne sous la souris
			else {

				// touche control non enfoncée: deselectionner tout
				if (arg0.isControlDown() == false) {
					projectm.setAllElementsSelected(false);

					// selectionner a nouveau la forme
					shapeSelected.setSelected(true);
				}

				// mode ajout de noeuds
				if (POLYPOINT_ADD_NODES.equals(mode)) {

					// un click sur la poigne de debut/fin: ajout de segments
					ArrayList<Handle> handles = shapeSelected.getHandles();
					Handle first = handles.get(0);
					Handle last = handles.get(handles.size() - 1);

					// premiere poigne, ajout de noeuds
					if (first.getInteractionArea().contains(pS)) {
						creator.setShape(CONSTRUCT_AT_FIRST_POINT, shapeSelected);
						creator.mouseReleased(arg0);
					}

					// dernire poigne, ajout
					else if (last.getInteractionArea().contains(pS)) {
						creator.setShape(CONSTRUCT_AT_LAST_POINT, shapeSelected);
						creator.mouseReleased(arg0);
					}

					// click sur un des segments, ajout
					else {

						// rechercher si on est dans la zone d'interaction d'une
						// poignee
						boolean onHandle = false;
						for (Handle h : shapeSelected.getHandles()) {
							if (h.getInteractionArea().contains(pS)) {
								onHandle = true;
								break;
							}
						}

						// si on est PAS dans zi d'une poignee, ajout d'une
						// poignee
						if (onHandle == false) {
							ArrayList<Area> areas = new ArrayList<Area>(
									shapeSelected.getSegmentsArea());

							// enregistrement pour annulation
							shapeSelected.getMementoManager().saveStateToRestore();

							adding: for (Area a : areas) {
								if (a.contains(pS)) {
									shapeSelected.addPointAtPosition(areas.indexOf(a) + 1, pS);
									break adding;
								}
							}

							// rafraichir la forme
							shapeSelected.refreshShape();

							// enregistrement pour annulation
							shapeSelected.getMementoManager().saveStateToRedo();
							cancelm.addDrawOperation(layer, shapeSelected);

						}
					}
				}

				// Mode retrait de points
				else if (POLYPOINT_REMOVE_NODES.equals(mode)) {

					// chercher la poignee a supprimer
					Handle handle = null;
					searching: for (Handle h : shapeSelected.getHandles()) {
						if (h.getInteractionArea().contains(pS)) {
							handle = h;
							break searching;
						}
					}

					// Enregistrement pour annulation
					shapeSelected.getMementoManager().saveStateToRestore();

					int index = shapeSelected.getHandles().indexOf(handle);
					if (index >= 0)
						shapeSelected.removePoint(index);

					// moins de deux poignes, suppression de l'element
					if (shapeSelected.getHandles().size() < 2) {
						layer.removeElement(shapeSelected);
					}

					// rafraichir la forme
					shapeSelected.refreshShape();

					// Enregistrement pour annulation
					shapeSelected.getMementoManager().saveStateToRedo();
					cancelm.addDrawOperation(layer, shapeSelected);

				}

				mapm.refreshMapComponent();

			}
		}

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		super.mouseDragged(arg0);

		// ne répondre qu'au clic gauche
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// verifier le projet
		if (projectm.isInitialized() == false)
			return;

		// une etape a ete sautee
		if (creator.isWorking() == true)
			return;

		// recuperer le calque actif
		MapLayer layer;
		try {
			layer = projectm.getActiveLayer();
		} catch (MapLayerException e1) {
			Log.debug(e1);
			return;
		}

		// point a l'echelle
		Point pS = mapm.getScaledPoint(arg0.getPoint());

		// tout premier mouvement
		if (mover.isWorking() == false) {

			// recherche si clic sur une poigne
			searchingForNodes: for (LayerElement e : layer.getDrawShapesReversed()) {

				// seulement les polylignes selectionnes
				if (shapeClass.isInstance(e) == false || e.isSelected() == false)
					continue searchingForNodes;

				for (Handle h : e.getHandles()) {
					if (h.getInteractionArea().contains(pS)) {
						mover.setShape((PolypointShape) e, h);
						mover.mouseDragged(arg0);
						break searchingForNodes;
					}
				}

			}

			// aucune poignee trouvée, recherche si clic sur un segment
			if (mover.isWorking() == false) {
				serchingForShape: for (LayerElement e : layer.getDrawShapesReversed()) {

					// seulement les polylignes slctionnes
					if (shapeClass.isInstance(e) == false || e.isSelected() == false)
						continue serchingForShape;

					if (e.getInteractionArea().contains(pS)) {
						mover.setShape((PolypointShape) e, null);
						mover.mouseDragged(arg0);
						break serchingForShape;
					}
				}
			}
		}

		// suite des mouvements
		else if (mover.isWorking() == true) {
			mover.mouseDragged(arg0);
		}

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		super.mouseMoved(arg0);

		if (creator.isWorking()) {
			creator.mouseMoved(arg0);
		}

	}

	@Override
	public void drawOnCanvas(Graphics2D g2d) {
		if (creator.isWorking())
			creator.draw(g2d);

	}

	@Override
	public void setToolMode(String mode) {
		super.setToolMode(mode);
		this.mode = mode;
	}

	/**
	 * Objet de deplacement de ligne
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class PolyPointMover {

		private boolean working;
		private PolypointShape poly;
		private Handle handle;
		private Point prevPos;
		private int handleIndex;

		public PolyPointMover() {
			setWorking(false);
		}

		public void setWorking(boolean working) {
			this.working = working;

			if (working == false) {
				this.poly = null;
				this.handle = null;
				this.handleIndex = -1;
				prevPos = null;
			}
		}

		/**
		 * Affecter une forme pour deplacement
		 * 
		 * @param p
		 * @param h
		 */
		public void setShape(PolypointShape p, Handle h) {
			poly = p;
			prevPos = null;
			handle = h;
			handleIndex = poly.getHandles().indexOf(handle);
		}

		public void mouseDragged(MouseEvent arg0) {

			// une etape a ete sautée: arret
			if (projectm.isInitialized() == false || poly == null) {
				setWorking(false);
				return;
			}

			// continuer à travailler
			if (SwingUtilities.isLeftMouseButton(arg0) == false)
				return;

			// premier deplacement
			if (isWorking() == false) {
				prevPos = mapm.getScaledPoint(arg0.getPoint());
				working = true;

				poly.getMementoManager().saveStateToRestore();
				return;
			}

			// position de la souris a l'echelle
			Point mouseS = mapm.getScaledPoint(arg0.getPoint());

			// calcul du deplacement relatif
			int x = mouseS.x - prevPos.x;
			int y = mouseS.y - prevPos.y;

			// deplacement de la forme entiere
			if (arg0.isShiftDown() == true || handle == null) {
				Point newPos = new Point(poly.getPosition());
				newPos.translate(x, y);
				poly.setPosition(newPos);
			}

			// deplacement du noeud seulement
			else {
				try {
					Point polyPoint = poly.getPoints().get(handleIndex);
					polyPoint.translate(x, y);
				} catch (IndexOutOfBoundsException e) {
					Log.error(e);
				}

			}

			// rafraichir la forme
			poly.refreshShape();

			// enreigstrement de la position précédente
			prevPos = mapm.getScaledPoint(arg0.getPoint());

			// rafraichir la carte
			mapm.refreshMapComponent();

		}

		public void mouseReleased(MouseEvent arg0) {

			// une etape a ete sautée: arret
			if (projectm.isInitialized() == false || poly == null) {
				setWorking(false);
				return;
			}

			// seulement le clic gauche
			if (SwingUtilities.isLeftMouseButton(arg0) == false)
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

			// rafraichir la forme
			poly.refreshShape();

			// enregistrer l'operation pour annulation
			poly.getMementoManager().saveStateToRedo();
			cancelm.addDrawOperation(layer, poly);

			// rafraichir la carte
			mapm.refreshMapComponent();

			// arret
			setWorking(false);
		}

		public boolean isWorking() {
			return working;
		}

	}

	/**
	 * Object createur de ligne
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class PolyPointCreator {

		private boolean working;
		private Double line;
		private String constructionMode;
		private PolypointShape poly;
		private boolean polyHasJustBeenAffected;

		public PolyPointCreator() {
			this.polyHasJustBeenAffected = false;
			setWorking(false);
		}

		/**
		 * Affecter une forme pour creation/modification
		 * 
		 * @param constructionMode
		 * @param p
		 */
		public void setShape(String constructionMode, PolypointShape p) {

			// reinitialisation !! avant affectation de la forme
			setWorking(false);

			// affectation de la forme
			this.poly = p;

			if (CONSTRUCT_AT_FIRST_POINT.equals(constructionMode)) {
				this.constructionMode = constructionMode;
			}

			else if (CONSTRUCT_AT_LAST_POINT.equals(constructionMode)) {
				this.constructionMode = constructionMode;
			}

			else
				throw new IllegalArgumentException("Unknown mode: " + constructionMode);

			polyHasJustBeenAffected = true;
		}

		public void mouseReleased(MouseEvent arg0) {

			// continuer à travailler
			if (SwingUtilities.isLeftMouseButton(arg0) == false)
				return;

			// projet non initialisé, retour
			if (projectm.isInitialized() == false) {
				setWorking(false);
				return;
			}

			// recuperer le calque actif
			MapLayer layer;
			try {
				layer = projectm.getActiveLayer();
			} catch (MapLayerException e1) {
				Log.debug(e1);
				setWorking(false);
				return;
			}

			// point a l'echelle
			Point pS = mapm.getScaledPoint(arg0.getPoint());

			// tout premier clic pour créer la forme
			if (isWorking() == false && arg0.getClickCount() == 1) {

				// creation et ajout de la forme si nulle
				if (poly == null) {

					if (shapeClass.equals(Polygon.class)) {
						poly = drawm.getWitnessPolygon();
					}

					else if (shapeClass.equals(Polyline.class)) {
						poly = drawm.getWitnessPolyline();
					}

					else {
						throw new IllegalArgumentException("Unknown class: " + shapeClass);
					}

					poly.addPoint(pS);

					// ajout sans notification
					layer.addElement(poly, false);

					ElementsCancelOp op = MainManager.getCancelManager().addDrawOperation(layer, poly);
					op.elementsHaveBeenAdded(true);

					setWorking(true);

				}

				// la forme existe deja
				else {

					// ne pas ajouter le premier point si nouvelle afectation de
					// poly
					if (polyHasJustBeenAffected) {
						polyHasJustBeenAffected = false;
					}

					else {

						poly.getMementoManager().saveStateToRestore();

						if (CONSTRUCT_AT_FIRST_POINT.equals(constructionMode)) {
							poly.addPointAtPosition(0, pS);
						}

						else if (CONSTRUCT_AT_LAST_POINT.equals(constructionMode)) {
							poly.addPoint(pS);
						}

						MapLayer activeLayer;
						try {
							activeLayer = projectm.getActiveLayer();
						} catch (MapLayerException e) {
							setWorking(false);
							return;
						}

						poly.getMementoManager().saveStateToRedo();
						MainManager.getCancelManager().addDrawOperation(activeLayer, poly);
					}
				}

				// selectionner seulement la forme
				projectm.setAllElementsSelected(false);
				poly.setSelected(true);

				projectm.fireElementsChanged();
			}

			// clics suivants pour ajouter des points
			else if (isWorking() && arg0.getClickCount() == 1) {

				poly.getMementoManager().saveStateToRestore();

				if (CONSTRUCT_AT_LAST_POINT.equals(constructionMode)) {
					poly.addPoint(pS);
				}

				else if (CONSTRUCT_AT_FIRST_POINT.equals(constructionMode)) {
					poly.addPointAtPosition(0, pS);
				}

				MapLayer activeLayer;
				try {
					activeLayer = projectm.getActiveLayer();
				} catch (MapLayerException e) {
					setWorking(false);
					return;
				}

				poly.getMementoManager().saveStateToRedo();
				MainManager.getCancelManager().addDrawOperation(activeLayer, poly);

			}

			// double clic pour terminer la forme
			else if (isWorking() && arg0.getClickCount() >= 2) {
				poly.closeShape();
				poly.refreshShape();
				setWorking(false);
			}

			mapm.refreshMapComponent();

		}

		public void mouseMoved(MouseEvent arg0) {

			// dessiner une ligne pour visualiser le tracé à venir
			ArrayList<Point> points = poly.getPoints();

			// choisir le point de départ
			Point p1 = new Point();
			if (CONSTRUCT_AT_FIRST_POINT.equals(constructionMode)) {
				p1 = points.get(0);
			} else if (CONSTRUCT_AT_LAST_POINT.equals(constructionMode)) {
				p1 = points.get(points.size() - 1);
			}

			// point d'arrivé
			Point p2 = mapm.getScaledPoint(arg0.getPoint());

			// la ligne
			line = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);

			mapm.refreshMapComponent();
		}

		public boolean isWorking() {
			return working;
		}

		private void setWorking(boolean v) {

			working = v;

			// reinitialisation si arret du travail
			if (working == false) {
				this.poly = null;
				this.line = null;
			}
		}

		public void draw(Graphics2D g2d) {

			// tracer la ligne pointillé lors de la creation
			if (line != null) {
				g2d.setStroke(TRACING_STROKE);
				g2d.setColor(TRACING_COLOR);
				g2d.draw(line);
			}

		}

	}

}
