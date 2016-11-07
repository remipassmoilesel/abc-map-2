package abcmap.gui;

import javax.swing.JComponent;

import abcmap.utils.Refreshable;

/**
 * Interface garantissant que l'objet possède un espace ou peuvent être affichés
 * des composantns Swing
 * 
 * @author remipassmoilesel
 *
 */
public interface HasDisplayableSpace extends Refreshable{
	public void displayComponent(JComponent comp);
}
