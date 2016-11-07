package abcmap.draw.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import abcmap.managers.DrawManager;
import abcmap.managers.MapManager;
import abcmap.managers.ProjectManager;
import abcmap.managers.stub.MainManager;

/**
 * Classe de traçage d'un rectangle simple
 * 
 * @author remipassmoilesel
 *
 */
public class SimpleRectangleTracer extends MouseAdapter {

	/** Le rectangle à tracer */
	protected Rectangle rectangle;

	/** Le point d'origine du rectangle, ou a eu lieu le clic de souris */
	private Point rectangleOrigin;

	/** Indicateur de travail */
	private boolean working;

	/** Effacer le rectangle lors du relachement de la souris */
	private boolean deleteRectangleOnMouseReleased;

	private Color rectangleColor;
	private Stroke rectangleStroke;

	private DrawManager drawm;
	private ProjectManager projectm;
	private MapManager mapm;

	public SimpleRectangleTracer() {

		drawm = MainManager.getDrawManager();
		projectm = MainManager.getProjectManager();
		mapm = MainManager.getMapManager();

		rectangleColor = Color.black;
		rectangleStroke = new BasicStroke(2);

		deleteRectangleOnMouseReleased = false;

		this.rectangle = null;
		this.rectangleOrigin = null;

		this.working = false;
	}

	private boolean checkProjectAndLeftClick(MouseEvent arg0) {

		// verifier le bouton du click
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return false;

		// verifier l'initilisation du projet
		if (projectm.isInitialized() == false)
			return false;

		return true;
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {

		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// obtenir le point de la souris à l'echelle
		Point sp = mapm.getScaledPoint(arg0.getPoint());

		// tout premier mouvement, créer le rectangle
		if (isWorking() == false) {

			unselectAllIfNecessary(arg0);

			// creer la forme puis
			rectangle = new Rectangle();
			rectangle.x = sp.x;
			rectangle.y = sp.y;

			// point de depart du rectangle pour calculs
			rectangleOrigin = new Point(sp);

			setWorking(true);

		}

		// mouvements suivants, après le premier
		else {

			Point ms = mapm.getScaledPoint(arg0.getPoint());

			// copie du point d'origine
			Point originCopy = new Point(rectangleOrigin);

			// calcul des dimensions du rectangle
			int w = ms.x - originCopy.x;
			int h = ms.y - originCopy.y;
			Dimension dim = new Dimension(w, h);

			// obtenir des valeurs toujours positives
			if (dim.width < 0) {
				int x = originCopy.x + dim.width;
				originCopy.setLocation(x, originCopy.y);
				dim.width = -dim.width;
			}

			if (dim.height < 0) {
				int y = originCopy.y + dim.height;
				originCopy.setLocation(originCopy.x, y);
				dim.height = -dim.height;
			}

			// touche control enfoncée, forme proportionnelle
			if (arg0.isControlDown() == true) {
				dim.width = dim.height;
			}

			// modifier la forme
			rectangle.x = originCopy.x;
			rectangle.y = originCopy.y;
			rectangle.width = dim.width;
			rectangle.height = dim.height;

			// rafraichir la carte
			mapm.refreshMapComponent();

		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		if (checkProjectAndLeftClick(arg0) == false)
			return;

		// tout deselectionner
		unselectAllIfNecessary(arg0);

		if (deleteRectangleOnMouseReleased == true) {
			resetRectangle();
		}

		setWorking(false);

		// appliquer les modifs
		mapm.refreshMapComponent();

	}

	/**
	 * Dessin du rectangle
	 * 
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		if (rectangle != null) {
			g2d.setColor(rectangleColor);
			g2d.setStroke(rectangleStroke);
			g2d.draw(rectangle);
		}
	}

	protected void resetRectangle() {
		rectangle = null;
		rectangleOrigin = null;
	}

	/**
	 * Déselectionne tous les elements du projet si le flag correspondant est
	 * activé.
	 * 
	 * @param arg0
	 */
	private void unselectAllIfNecessary(MouseEvent arg0) {

		// deselectionner tout si la touche control est levée, et si demandé
		if (arg0.isControlDown() == false) {
			projectm.setAllElementsSelected(false);
		}

	}

	public void setWorking(boolean working) {
		this.working = working;
	}

	public boolean isWorking() {
		return working;
	}

	public void setRectangleColor(Color color) {
		this.rectangleColor = color;
	}

	public void setDeleteRectangleOnMouseReleased(boolean val) {
		this.deleteRectangleOnMouseReleased = val;
	}

	public void setRectangleStroke(Stroke stroke) {
		this.rectangleStroke = stroke;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

}
