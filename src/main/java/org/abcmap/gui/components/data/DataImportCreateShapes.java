package org.abcmap.gui.components.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.SelectedObjectPanel;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

public class DataImportCreateShapes extends JPanel {

    private DataImportOptionsPanel parent;
    private String gapleft;
    private String wrap15;

    public DataImportCreateShapes(DataImportOptionsPanel parent) {
        super(new MigLayout("insets 0"));

        this.parent = parent;
        this.wrap15 = "wrap 15, ";
        this.gapleft = "gapleft 15px, ";

        // show form to duplicate
        GuiUtils.addLabel("Sélectionnez la forme à dupliquer: ", this, "wrap");

        SelectedObjectPanel sop = new SelectedObjectPanel();

        // filtrer les formes
//		sop.addFilter(Rectangle.class);
//		sop.addFilter(Ellipse.class);
//		sop.addFilter(Image.class);
//		sop.addFilter(Symbol.class);

        add(sop, wrap15 + gapleft);


        GuiUtils.addLabel("Options: ", this, "wrap");

        // change size by fields
        HtmlCheckbox chkChangeSize = new HtmlCheckbox("<html>Changer la taille de l'objet en fonction du champs:</html>");
        add(chkChangeSize, "wrap");

        DataHeadersCombo cbChangeSize = new DataHeadersCombo();
        add(cbChangeSize, gapleft + wrap15);

        // change color by fields
        HtmlCheckbox chkChangeColor = new HtmlCheckbox("<html>Changer la couleur de l'objet en fonction du champs:</html>");
        add(chkChangeColor, "wrap");

        DataHeadersCombo cbChangeColor = new DataHeadersCombo();
        add(cbChangeColor, gapleft + wrap15);

    }

}
