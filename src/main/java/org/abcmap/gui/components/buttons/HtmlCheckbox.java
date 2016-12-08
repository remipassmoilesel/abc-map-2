package org.abcmap.gui.components.buttons;

import org.abcmap.gui.GuiStyle;

import javax.swing.*;

/**
 * Checkbox with HTML text
 * <p>
 * HTML tags prevents text overflow
 *
 * @author remipassmoilesel
 */
public class HtmlCheckbox extends JCheckBox {

    public HtmlCheckbox() {
        super();
    }

    public HtmlCheckbox(String htmlText) {
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
