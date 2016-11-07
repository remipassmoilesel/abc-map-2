package abcmap.gui.comps.display;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface DrawablePanelElement {

	/**
	 * Peint l'objet avec l'objet graphic en parametre.
	 * 
	 * @param g2d
	 */
	public void render(Graphics2D g2d);

	/**
	 * Retourne les dimensions ou null.
	 * 
	 * @return
	 */
	public Dimension getDimensions();
}
