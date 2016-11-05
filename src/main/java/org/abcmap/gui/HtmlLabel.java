package org.abcmap.gui;

import javax.swing.*;

/**
 * Custom label with default text in HTML
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