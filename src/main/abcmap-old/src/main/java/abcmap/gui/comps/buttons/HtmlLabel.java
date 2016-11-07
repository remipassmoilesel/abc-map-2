package abcmap.gui.comps.buttons;

import javax.swing.JLabel;

import abcmap.gui.GuiStyle;

/**
 * Etiquette de texte avec balises HTML. Fait en sorte que le texte revienne à
 * la ligne en cas de changement.
 * 
 * @author remipassmoilesel
 *
 */
public class HtmlLabel extends JLabel {

	public HtmlLabel() {
		super();
	}

	public HtmlLabel(String htmlText) {
		super(htmlText);
	}

	/**
	 * Ajouter du texte avec balises HTML
	 */
	@Override
	public void setText(String htmlText) {
		super.setText("<html>" + htmlText + "</html>");
	}

	public void setStyle(GuiStyle style) {
		GuiStyle.applyStyleTo(style, this);
	}

}
