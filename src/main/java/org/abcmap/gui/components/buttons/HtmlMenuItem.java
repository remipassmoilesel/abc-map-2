package org.abcmap.gui.components.buttons;

import javax.swing.JMenuItem;

/**
 * Label with HTML text
 * <p>
 * HTML tags prevents text overflow
 *
 * @author remipassmoilesel
 */
public class HtmlMenuItem extends JMenuItem {

	public HtmlMenuItem(String text) {
		super(text);
	}

	@Override
	public void setText(String htmlText) {
		super.setText("<html>" + htmlText + "</html>");
	}

}
