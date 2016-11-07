package abcmap.draw.tools;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import abcmap.geo.Coordinate;
import abcmap.gui.GuiIcons;

/**
 * Outil de pointage de références géographiques
 * 
 * @author remipassmoilesel
 *
 */
public class GeorefTool extends MapTool {

	/**
	 * Dessiner les deux points de référence sur la carte
	 */
	@Override
	public void drawOnCanvas(Graphics2D g) {

		// projet non initialisé: retour
		if (projectm.isInitialized() == false)
			return;

		// mode geoloc non activé: retour
		if (mapm.isGeoreferencementEnabled() == false)
			return;

		// ameliorer le rendu
		Graphics2D g2 = (Graphics2D) g.create();

		// dessiner les références
		for (Coordinate ref : mapm.getGeoReferences()) {

			// récupérer l'image a dessiner
			ImageIcon mark = ref == mapm.getActiveReference() ? GuiIcons.GEOLOC_MARK_ACTIVE
					: GuiIcons.GEOLOC_MARK_INACTIVE;

			// largeur et hauteur de l'image à afficher
			Dimension dim = new Dimension();
			dim.width = mark.getImage().getWidth(null);
			dim.height = mark.getImage().getHeight(null);

			// position reelle de la ref
			Point2D pixRef = ref.getPixelPoint();

			// coordonnees de positionnement de l'image
			Double x = pixRef.getX() - (dim.width / 2);
			Double y = pixRef.getY() - (dim.height / 2);
			g2.drawImage(mark.getImage(), x.intValue(), y.intValue(), null);
		}

	}

	/**
	 * Pointage d'un point de référence sur la carte
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {

		// ne repondre qu'au bouton gauche de la souris
		if (SwingUtilities.isLeftMouseButton(arg0) == false)
			return;

		// projet non intialisé: retour
		if (projectm.isInitialized() == false)
			return;

		// mode georef inactif: arret
		if (mapm.isGeoreferencementEnabled() == false)
			return;

		// récupérer la référence active
		Coordinate ref = mapm.getActiveReference();
		if (ref == null)
			return;

		// position de la souris à l'echelle
		Point p = mapm.getScaledPoint(arg0.getPoint());

		// changement de la reference
		ref.setPixelPoint(p);

		// notifications et affichage
		mapm.notifyGeosystemChanged();
		mapm.refreshMapComponent();

	}

}
