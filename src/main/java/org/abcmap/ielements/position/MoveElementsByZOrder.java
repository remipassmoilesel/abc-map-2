package org.abcmap.ielements.position;

import net.miginfocom.swing.MigLayout;
import org.abcmap.ielements.InteractionElement;

import javax.swing.*;
import java.awt.*;

public class MoveElementsByZOrder extends InteractionElement {

    public MoveElementsByZOrder() {
        this.label = "Changer la position en profondeur";
        this.help = "Utilisez les boutons ci-dessous pour changer la position des objets sélectionnés "
                + "en profondeur.";

        this.displaySimplyInSearch = false;
    }

    @Override
    protected Component createPrimaryGUI() {

        InteractionElement[] elements = new InteractionElement[]{new MoveElementsUp(),
                new MoveElementsDown(), new MoveElementsTop(), new MoveElementsBottom(),};

        JPanel panel = new JPanel(new MigLayout("insets 2, gap 2"));
        for (InteractionElement ie : elements) {

            JButton button = new JButton(ie.getMenuIcon());
            button.setToolTipText(ie.getLabel());
            button.addActionListener(ie);

            panel.add(button, "height 35px!, width 35px!");
        }

        return panel;
    }

}
