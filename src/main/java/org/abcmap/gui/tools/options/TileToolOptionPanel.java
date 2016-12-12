package org.abcmap.gui.tools.options;

import org.abcmap.gui.components.buttons.HtmlCheckbox;
import org.abcmap.gui.utils.GuiUtils;

import javax.swing.*;

public class TileToolOptionPanel extends ToolOptionPanel {

    public TileToolOptionPanel() {

        GuiUtils.throwIfNotOnEDT();

        GuiUtils.addLabel("Analyse de tuiles: ", this, "wrap");

        JButton btnAnalyseTiles = new JButton(
                "Analyser les tuiles sélectionnées");
        add(btnAnalyseTiles, gapLeft + largeWrap);


        HtmlCheckbox chkDisplayIpts = new HtmlCheckbox("Afficher les points d'interet");
        add(chkDisplayIpts, largeWrap);

    }

}
