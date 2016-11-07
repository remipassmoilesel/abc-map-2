package abcmap.gui.comps.display;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import abcmap.gui.GuiCursor;
import abcmap.utils.gui.GuiUtils;

/**
 * Panneau d'affichage dynamique. L'utilisateur peut zoomer dans le panneau avec
 * sa molette, et déplacer le panneau avec sa souris.
 * 
 * @author remipassmoilesel
 *
 */
public class DynamicDisplayPanel extends JPanel {

	public static final String RIGHT_BUTTON = "RIGHT_BUTTON";
	public static final String LEFT_BUTTON = "LEFT_BUTTON";

	/** L'element dessiné dans le panneau */
	private DrawablePanelElement drawableElement;

	/** Echelle d'affichage */
	private float scale;

	/** Valeur minimale d'affichage */
	private float minScaleValue;

	/** Valeur maximale d'affichage */
	private float maxScaleValue;

	/** Valeur d'incement/decrementation d'affichage */
	private float rotateScaleStep;

	/** Point d'origine de l'element a dessiner par rapport au ULC */
	private Point elementOrigin;

	/** Dernier type de curseur avant déplacement */
	private Cursor lastCursor;

	/** Le bouton utilisé pour déplacer le panneau */
	private String moveMouseButton;

	public DynamicDisplayPanel() {
		super();

		// pas de layout
		setLayout(null);

		// caractéristiques par defaut
		setPreferredSize(new Dimension(800, 600));

		// echelle par defaut
		this.scale = 1f;
		this.minScaleValue = 0.2f;
		this.maxScaleValue = 3f;

		this.rotateScaleStep = 0.2f;

		// point d'origine de l'element a afficher
		this.elementOrigin = new Point(0, 0);

		// ecouter la souris
		ZoomAndMoveMapManager mm = new ZoomAndMoveMapManager();
		addMouseListener(mm);
		addMouseMotionListener(mm);
		addMouseWheelListener(mm);

		// couleur de fond par défaut
		setBackground(Color.darkGray);

		moveMouseButton = RIGHT_BUTTON;

	}

	/**
	 * Déterminer quel bouton est utilisé pour déplacer l'element dessiné
	 * 
	 * @param moveMouseButton
	 */
	public void setMoveMouseButton(String moveMouseButton) {
		this.moveMouseButton = moveMouseButton;
	}

	/**
	 * Valeur minimale de zoom
	 * 
	 * @param minScaleValue
	 */
	public void setMinScaleValue(float minScaleValue) {
		this.minScaleValue = minScaleValue;
	}

	/**
	 * Valeur maximale de zoom
	 * 
	 * @param minScaleValue
	 */
	public void setMaxScaleValue(float maxScaleValue) {
		this.maxScaleValue = maxScaleValue;
	}

	/**
	 * Converti un point de l'espace du composant panneau en point dans l'espace
	 * de l'element affiché.
	 * <p>
	 * Manipule une copie du point passé en parametre.
	 * 
	 * @param p
	 */
	public Point getPointFromComponentToViewSpace(Point p) {

		Point p2 = new Point(0, 0);

		p2.x = (int) ((p.x - elementOrigin.x) / scale);
		p2.y = (int) ((p.y - elementOrigin.y) / scale);

		return p2;
	}

	/**
	 * Converti un point de l'espace de l'élément affiché en point dans l'espace
	 * du composant.
	 * <p>
	 * Manipule une copie du point passé en parametre.
	 * 
	 * @param p
	 */
	public Point getPointFromViewToComponentSpace(Point p) {

		Point p2 = new Point(0, 0);

		p2.x = (int) (p.x * scale + elementOrigin.x);
		p2.y = (int) (p.y * scale + elementOrigin.y);

		return p2;
	}

	/**
	 * Renvoi vrai si un point de l'espace de l'element affiché est dans ses
	 * limites.
	 * 
	 * @param p
	 */
	public boolean isPointOnDrawableElement(Point p) {
		Rectangle deBounds = new Rectangle(drawableElement.getDimensions());
		return deBounds.contains(p);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// graphiques à l'echelle
		Graphics2D g2dScaled = getTransformedGraphics(g);

		// graphiques non à l'echelle
		Graphics2D g2d = (Graphics2D) g.create();

		// nettoyer le composant
		g2d.setPaint(getBackground());
		g2d.fill(new Rectangle(this.getSize()));

		// rendu de l'element
		if (drawableElement != null) {
			drawableElement.render((Graphics2D) g2dScaled);
		}

	}

	/**
	 * Adapte un objet graphics du composant à l'élément à dessiner (echelle,
	 * options, ...), puis le retourne.
	 * 
	 * @return
	 */
	public Graphics2D getTransformedGraphics() {
		return getTransformedGraphics(getGraphics());
	}

	/**
	 * Adapte un objet graphics à l'élément à dessiner (echelle, options, ...),
	 * puis le retourne.
	 * 
	 * @return
	 */
	public Graphics2D getTransformedGraphics(Graphics g) {

		// graphiques à l'echelle d'affichage
		Graphics2D g2dScaled = (Graphics2D) g.create();

		// améliorer la qualité du dessin
		GuiUtils.applyQualityRenderingHints(g2dScaled);

		// point d'origine de la carte
		g2dScaled.translate(elementOrigin.x, elementOrigin.y);

		// echelle
		g2dScaled.scale(scale, scale);

		return g2dScaled;
	}

	/**
	 * Déplace l'element à dessiner de +p.x et +p.y
	 * 
	 * @param pixel
	 */
	public void move(Point pixel) {
		move(pixel.x, pixel.y);
	}

	/**
	 * Déplace l'element à dessiner de +x et +y
	 * 
	 * @param pixel
	 */
	public void move(int x, int y) {
		elementOrigin.setLocation(elementOrigin.x + x, elementOrigin.y + y);
	}

	/**
	 * Recentre l'affichage et remet à zéro l'echelle
	 * 
	 * @param sc
	 */
	public void resetDisplay(float sc) {

		// recuperer les dimensions de l'objet à rendre
		Dimension deDim = drawableElement.getDimensions();

		// si nulle retour
		if (deDim == null)
			return;

		setDisplayScale(sc);

		// recuperer les dimensions du composant
		Rectangle cpDim = this.getVisibleRect();

		// calculer une origine correspondant au centre
		// + mise à l'echelle
		double x = (cpDim.width - deDim.width * scale) / 2d;
		double y = (cpDim.height - deDim.height * scale) / 2d;

		elementOrigin.setLocation(x, y);

	}

	/**
	 * Incrémente l'echelle d'affichage
	 * 
	 * @param value
	 */
	public void addToScale(float value) {
		setDisplayScale(verifyScale(scale + value));
	}

	/**
	 * Affecte l'echelle d'affichage
	 * 
	 * @param value
	 */
	public void setDisplayScale(float value) {
		scale = verifyScale(value);
	}

	/**
	 * Retourne l'echelle d'affichage
	 * 
	 * @return
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * Retourne une valeur corrigée de l'echelle passée en paramètre
	 * 
	 * @param sc
	 * @return
	 */
	private float verifyScale(float sc) {

		if (sc < minScaleValue)
			sc = minScaleValue;

		if (sc > maxScaleValue)
			sc = maxScaleValue;

		return sc;
	}

	/**
	 * Affecte l'element a dessiner au panneau
	 * 
	 * @param e
	 */
	public void setDrawableElement(DrawablePanelElement e) {
		this.drawableElement = e;
	}

	/**
	 * Conserver l'état du curseur pour pourvoir le rétablir plus tard
	 */
	private void saveCursor() {
		lastCursor = getCursor();
	}

	/**
	 * Restorer l'état du curseur
	 */
	private void restoreCursor() {
		Cursor cur = lastCursor != null ? lastCursor : GuiCursor.NORMAL_CURSOR;
		setCursor(cur);
	}

	/**
	 * Changer le curseur pour le curseur de mouvement par défaut
	 */
	public void setMovementCursor() {
		setCursor(GuiCursor.MOVE_CURSOR);
	}

	/**
	 * Affecter la position de l'élément a dessiner, par rapport à l'ULC et dans
	 * l'espace de coordonnées de l'élément.
	 * 
	 * @param elementOrigin
	 */
	public void setDrawableElementOrigin(Point elementOrigin) {
		this.elementOrigin = new Point(elementOrigin);
	}

	/**
	 * Gestion des mouvements du panneau
	 * 
	 * @author remipassmoilesel
	 *
	 */
	private class ZoomAndMoveMapManager extends MouseAdapter {

		private Point lastMovePosition;

		/**
		 * Déplacement du composant
		 */
		@Override
		public void mouseDragged(MouseEvent e) {

			// vérfier le bouton de la souris
			if (RIGHT_BUTTON.equals(moveMouseButton)
					&& SwingUtilities.isRightMouseButton(e) == false) {
				return;
			}

			if (LEFT_BUTTON.equals(moveMouseButton)
					&& SwingUtilities.isLeftMouseButton(e) == false) {
				return;
			}

			// tout premier deplacement
			if (lastMovePosition == null) {

				// enregistrer la position de la souris
				lastMovePosition = e.getPoint();

				// changement de curseur
				saveCursor();
				setMovementCursor();

				return;
			}

			// calcul du déplacement
			Point presentPoint = e.getPoint();
			Point move = new Point();
			move.x = (presentPoint.x - lastMovePosition.x);
			move.y = (presentPoint.y - lastMovePosition.y);

			// mouvement
			move(move);

			// enregistrement de la dernière position
			lastMovePosition = e.getPoint();

			// rafraichir le composant
			repaint(getVisibleRect());
		}

		/**
		 * Fin de déplacement de composant
		 */
		@Override
		public void mouseReleased(MouseEvent e) {

			// deplacement uniquement par clic droit
			if (SwingUtilities.isRightMouseButton(e) == false)
				return;

			// retour au curseur normal
			restoreCursor();

			// reinitialisation
			// pour eviter les ecarts de deplacement au prochain mouvement
			lastMovePosition = null;

		}

		/**
		 * Zoom sur le composant
		 */
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {

			// recuperer l'orientation de la rotation
			float rotateValue = e.getWheelRotation() > 0 ? -rotateScaleStep
					: rotateScaleStep;

			// calcul de la variation du zoom
			float zd = getScale();
			float za = verifyScale(rotateValue + getScale());
			float var = (za - zd) / zd;

			// calcul du deplacement de compensation de zoom
			Point m = e.getPoint();
			int dx = (int) ((m.x - elementOrigin.x + 1) * var);
			int dy = (int) ((m.y - elementOrigin.y + 1) * var);

			move(-dx, -dy);

			// zoom
			addToScale(rotateValue);

			// rafraichir le composant
			repaint(getVisibleRect());
		}

	}

}
