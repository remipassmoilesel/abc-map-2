package org.abcmap.iegroup.docks;

import net.miginfocom.swing.MigLayout;
import org.abcmap.gui.GuiIcons;
import org.abcmap.gui.GuiStyle;
import org.abcmap.gui.components.dock.Dock;
import org.abcmap.ielements.InteractionElement;
import org.abcmap.ielements.GroupOfInteractionElements;
import org.abcmap.gui.utils.GuiUtils;
import org.abcmap.gui.wizards.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GroupWizard extends GroupOfInteractionElements {

    private Wizard[] wizards = new Wizard[]{new PresentationWizard(),
            new AutoScreenCaptureWizard(), new GetHelpWizard(),
            new GeolocWizard(), new PrintWorkWizard(), new ExportWorkWizard(),
            new ListImportWizard()};

    public GroupWizard() {
        label = "Assistants de création";
        blockIcon = GuiIcons.GROUP_WIZARD;
        help = "Les assistants ci-dessous permettent de découvrir les "
                + "fonctionnalités du logiciel étape par étape.";

        addInteractionElement(new WizardLauncher());
    }

    public class WizardLauncher extends InteractionElement {

        public WizardLauncher() {
            label = "Assistants disponibles";
            help = "Les assistants ci-dessous permettent de découvrir les "
                    + "fonctionnalités du logiciel étape par étape.";

            displayInHideableElement = false;
            displaySimplyInSearch = false;
        }

        @Override
        protected Component createPrimaryGUI() {

            JPanel p1 = new JPanel(new MigLayout("insets 5 8 5 5"));

            for (final Wizard wizard : wizards) {

                GuiUtils.addLabel(wizard.getTitle(), p1, "wrap", GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);
                GuiUtils.addLabel(wizard.getDescription(), p1, "wrap");

                final JButton bt = new JButton("Démarrer l'assistant");
                bt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        wizard.setDisplayableSpace(Dock.getDockParentForComponent(bt));
                        wizard.showStep(0);
                    }
                });
                p1.add(bt, "wrap, gaptop 15, gapbottom 25");

            }

            return p1;
        }
    }
}
