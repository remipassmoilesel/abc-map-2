package abcmap.gui.menu;

import abcmap.gui.comps.buttons.HtmlMenuItem;
import abcmap.gui.ie.InteractionElement;

/**
 * Element de menu simple adapté aux barre de menu Swing. Lors d'un clic,
 * execute l'action de l'element d'interaction pasé en paramêtre.
 * 
 * @author remipassmoilesel
 *
 */
public class ClickableMenuItem extends HtmlMenuItem {

	public ClickableMenuItem(InteractionElement elmt) {
		super(elmt.getLabel());
		setIcon(elmt.getMenuIcon());
		setAccelerator(elmt.getAccelerator());
		addActionListener(elmt);

	}

}
