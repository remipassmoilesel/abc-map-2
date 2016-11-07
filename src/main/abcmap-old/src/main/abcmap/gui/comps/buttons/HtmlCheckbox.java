package abcmap.gui.comps.buttons;

import javax.swing.JCheckBox;

import abcmap.gui.GuiStyle;

/**
 * Case à cocher avec texte entre balises HTML pour que le texte ne déborde jamais du
 * bouton.
 * 
 * @author remipassmoilesel
 *
 */
public class HtmlCheckbox extends JCheckBox {

	public HtmlCheckbox() {
		super();
	}

	public HtmlCheckbox(String htmlText) {
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
