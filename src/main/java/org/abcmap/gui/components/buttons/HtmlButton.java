package org.abcmap.gui.components.buttons;

import org.abcmap.gui.GuiStyle;

import javax.swing.*;

/**
 * Button with HTML text.
 *
 * HTML tags prevents text overflow
 *
 * @author remipassmoilesel
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
