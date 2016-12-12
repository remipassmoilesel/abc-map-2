package org.abcmap.gui.ie.align;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.ie.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class AlignAndDistribute extends InteractionElement {

    public AlignAndDistribute() {
        label = "Aligner et distribuer des objets";
        help = "Choisissez un bouton ci dessous pour aligner et distribuer les formes sélectionnées.";

        displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        InteractionElement[] elements = new InteractionElement[]{new AlignTop(),
                new AlignBottom(), new AlignLeft(), new AlignRight(), new AlignMiddleHorizontal(),
                new AlignMiddleVertical(), new DistributeHorizontal(), new DistributeVertical(),};

        JPanel panel = new JPanel(new MigLayout("insets 2, gap 5"));
        int i = 1;
        for (InteractionElement ie : elements) {

            JButton button = new JButton(ie.getMenuIcon());
            button.setToolTipText(ie.getLabel());
            button.addActionListener(ie);

            String csts = i % 4 == 0 ? "wrap" : "";

            panel.add(button, "height 35px!, width 35px!, " + csts);

            i++;
        }

        return panel;
    }

}
