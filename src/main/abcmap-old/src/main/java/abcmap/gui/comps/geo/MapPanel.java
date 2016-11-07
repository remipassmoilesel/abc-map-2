package abcmap.gui.comps.geo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import abcmap.draw.basicshapes.Drawable;
import abcmap.gui.comps.display.DynamicDisplayPanel;
import abcmap.managers.MapManager;
import abcmap.managers.stub.MainManager;
import abcmap.project.utils.ProjectRenderer;

/**
 * Classe d'affichage de la carte. <br>
 * Rafraichie par MapControl.
 * 
 * @author remipassmoilesel
 *
 */
public class MapPanel extends DynamicDisplayPanel {

	private MapNavigationBar navbar;
	private MapManager mapm;

	public MapPanel() {
		super();

		mapm = MainManager.getMapManager();

		// barre de navigation
		navbar = new MapNavigationBar();
		navbar.setPanelToControl(this);
		add(navbar);

		// valeurs min et max de zoom, adapté à la qualité de rendue médiocre
		// des images
		setMinScaleValue(0.5f);
		setMaxScaleValue(2.5f);

		// affecter le gestionnaire de projet comme element a dessiner
		setDrawableElement(new ProjectRenderer(Drawable.RENDER_FOR_DISPLAYING));

		// demander le focus lors d'un clic
		addMouseListener(new FocusRequester());

	}

	private void refreshNavBarPosition() {

		if (navbar == null)
			return;

		navbar.refreshBoundsFrom(this.getVisibleRect());
	}

	public boolean isPointOnMap(Point p) {
		return isPointOnDrawableElement(p);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// maj de la position de la barre de navigation
		refreshNavBarPosition();
	}

	@Override
	public Dimension getPreferredSize() {
		// maj de la position de la barre de navigation
		refreshNavBarPosition();
		return super.getPreferredSize();
	}

	/**
	 * Repaint la partie visible de la carte.
	 */
	public void refresh() {
		revalidate();
		repaint(getVisibleRect());
	}

	/**
	 * Utiliser plutot refresh pour ne repeindre que la partie visible.
	 */
	@Deprecated
	@Override
	public void repaint() {
		super.repaint();
	}

	private class FocusRequester extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			requestFocusInWindow();
		}
	}

	/**
	 * Le changment d'echelle notifie des observateurs.
	 */
	@Override
	public void setDisplayScale(float value) {
		super.setDisplayScale(value);

		mapm.notifyDisplayScaleChanged();
	}

}
