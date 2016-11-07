package abcmap.gui.comps.buttons;

import javax.swing.JButton;

import abcmap.gui.GuiStyle;

/**
 * Bouton avec texte entre balises HTML pour que le texte ne déborde jamais du
 * bouton.
 * 
 * @author remipassmoilesel
 *
 */
public class HtmlButton extends JButton {

	public HtmlButton() {
		super();
	}

	public HtmlButton(String htmlText) {
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
