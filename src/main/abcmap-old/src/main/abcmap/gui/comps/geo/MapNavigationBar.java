package abcmap.gui.comps.geo;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JPanel;

import abcmap.gui.GuiCursor;
import abcmap.gui.comps.display.DynamicDisplayPanel;
import abcmap.gui.ie.display.zoom.ResetZoom;
import abcmap.gui.ie.display.zoom.ZoomIn;
import abcmap.gui.ie.display.zoom.ZoomOut;
import abcmap.utils.Utils;
import net.miginfocom.swing.MigLayout;

public class MapNavigationBar extends JPanel {

	private DynamicDisplayPanel dpanel;
	private Rectangle lastParentBounds;

	public MapNavigationBar() {
		
		// construction
		super(new MigLayout("insets 2, gap 2px"));

		// panneau transparent
		this.setOpaque(false);

		// bouton zoomer
		ZoomIn zi = new ZoomIn();
		JButton zoomin = new JButton(zi.getMenuIcon());
		zoomin.addActionListener(zi);
		add(zoomin, "wrap");

		// bouton dezoomer
		ZoomOut zo = new ZoomOut();
		JButton zoomout = new JButton(zo.getMenuIcon());
		zoomout.addActionListener(zo);
		add(zoomout, "wrap");

		// bouton Centre
		ResetZoom rz = new ResetZoom();
		JButton center = new JButton(rz.getMenuIcon());
		center.addActionListener(rz);
		add(center, "right");

		zoomin.setCursor(GuiCursor.HAND_CURSOR);
		zoomout.setCursor(GuiCursor.HAND_CURSOR);
		center.setCursor(GuiCursor.HAND_CURSOR);
	}

	public void setPanelToControl(DynamicDisplayPanel dpanel) {
		this.dpanel = dpanel;
	}

	/**
	 * Rafraichir la position du composant en fonction de la taille du composant
	 * parent.
	 * 
	 * @param visibleRect
	 */
	public void refreshBoundsFrom(Rectangle parentBounds) {

		// eviter les appels inutiles, provoquent des boucles infinies avec
		// repaint()
		if (Utils.safeEquals(parentBounds, lastParentBounds))
			return;
		else
			lastParentBounds = parentBounds;

		Dimension navbarDims = getPreferredSize();

		int x = parentBounds.x + parentBounds.width - navbarDims.width;
		int y = parentBounds.y + parentBounds.height - navbarDims.height;
		int width = navbarDims.width;
		int height = navbarDims.height;
		setBounds(x, y, width, height);

		// rafraichissement de l'affichage
		repaint();
	}

}
