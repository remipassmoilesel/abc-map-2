package abcmap.gui.comps.buttons;

import javax.swing.JMenuItem;

/**
 * Composant pour menu haut avec texte au format HTML
 * 
 * @author remipassmoilesel
 *
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
