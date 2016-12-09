package org.abcmap.gui.components.data;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.components.SelectedObjectPanel;
import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

/**
 * Panel to set up a label creation in terms of import
 */
public class DataImportCreateLabels extends JPanel {

    private DataImportOptionsPanel parent;
    private String gapleft;
    private String wrap15;

    public DataImportCreateLabels(DataImportOptionsPanel parent) {
        super(new MigLayout("insets 0"));

        this.parent = parent;
        this.wrap15 = "wrap 15, ";
        this.gapleft = "gapleft 15px, ";

        // show shape to duplicate
        GuiUtils.addLabel("Sélectionnez l'étiquette à dupliquer: ", this, "wrap");
        SelectedObjectPanel sop = new SelectedObjectPanel();

        // filter shapes
        //sop.addFilter(Label.class);

        add(sop, wrap15 + gapleft);

        // options
        GuiUtils.addLabel("Options: ", this, "wrap");

        // display coordinates
        JRadioButton rdDisplayCoords = new JRadioButton("<html>Afficher les coordonnées du point</html>");
        rdDisplayCoords.setSelected(true);
        add(rdDisplayCoords, "wrap");

        // change text by field
        JRadioButton rdChangeText = new JRadioButton("<html>Modifier le texte en fonction du champs:</html>");
        add(rdChangeText, "wrap");

        ButtonGroup bg1 = new ButtonGroup();
        bg1.add(rdDisplayCoords);
        bg1.add(rdChangeText);

        DataHeadersCombo cbChangeText = new DataHeadersCombo();
        add(cbChangeText, gapleft + wrap15);

        // change color by field
        HtmlCheckbox chkChangeColor = new HtmlCheckbox("<html>Changer la couleur des etiquettes en fonction du champs:</html>");
        add(chkChangeColor, "wrap");

        DataHeadersCombo cbChangeColor = new DataHeadersCombo();
        add(cbChangeColor, gapleft + wrap15);

    }
}
