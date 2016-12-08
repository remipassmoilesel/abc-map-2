package org.abcmap.gui.components.buttons;

import org.abcmap.gui.GuiStyle;

import javax.swing.*;

/**
 * Label with HTML text
 * <p>
 * HTML tags prevents text overflow
 *
 * @author remipassmoilesel
 */
public class HtmlToggleButton extends JButton {

	public HtmlToggleButton() {
		super();
	}

	public HtmlToggleButton(String htmlText) {
		super(htmlText);
	}

	@Override
	public void setText(String htmlText) {
		super.setText("<html>" + htmlText + "</html>");
	}

	public void setStyle(GuiStyle style) {
		GuiStyle.applyStyleTo(style, this);
	}

}
