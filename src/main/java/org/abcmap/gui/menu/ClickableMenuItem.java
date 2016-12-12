package org.abcmap.gui.menu;


import org.abcmap.gui.components.buttons.HtmlMenuItem;
import org.abcmap.gui.ie.InteractionElement;

public class ClickableMenuItem extends HtmlMenuItem {

	public ClickableMenuItem(InteractionElement elmt) {
		super(elmt.getLabel());
		setIcon(elmt.getMenuIcon());
		setAccelerator(elmt.getAccelerator());
		addActionListener(elmt);
	}

}
