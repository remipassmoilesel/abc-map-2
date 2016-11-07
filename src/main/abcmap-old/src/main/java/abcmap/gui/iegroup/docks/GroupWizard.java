package abcmap.gui.iegroup.docks;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import abcmap.gui.GuiIcons;
import abcmap.gui.GuiStyle;
import abcmap.gui.dock.comps.Dock;
import abcmap.gui.ie.InteractionElement;
import abcmap.gui.iegroup.InteractionElementGroup;
import abcmap.gui.wizards.ExportWorkWizard;
import abcmap.gui.wizards.GeolocWizard;
import abcmap.gui.wizards.GetHelpWizard;
import abcmap.gui.wizards.ListImportWizard;
import abcmap.gui.wizards.PresentationWizard;
import abcmap.gui.wizards.PrintWorkWizard;
import abcmap.gui.wizards.AutoScreenCaptureWizard;
import abcmap.gui.wizards.Wizard;
import abcmap.utils.gui.GuiUtils;
import abcmap.utils.gui.Lng;
import net.miginfocom.swing.MigLayout;

public class GroupWizard extends InteractionElementGroup {

	private Wizard[] wizards = new Wizard[] { new PresentationWizard(),
			new AutoScreenCaptureWizard(), new GetHelpWizard(),
			new GeolocWizard(), new PrintWorkWizard(), new ExportWorkWizard(),
			new ListImportWizard() };

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

				// titre et description de l'assistant
				GuiUtils.addLabel(wizard.getTitle(), p1, "wrap",
						GuiStyle.SIMPLE_BLOCK_ITEM_LABEL);
				GuiUtils.addLabel(wizard.getDescription(), p1, "wrap");

				// bouton de lancement
				final JButton bt = new JButton("Démarrer l'assistant");
				bt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						wizard.setDisplayableSpace(Dock
								.getDockParentForComponent(bt));
						wizard.showStep(0);
					}
				});
				p1.add(bt, "wrap, gaptop 15, gapbottom 25");

			}

			return p1;
		}
	}
}
