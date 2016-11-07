package abcmap.gui.ie.program;

import javax.swing.SwingUtilities;

import abcmap.gui.dialogs.AboutProjectDialog;
import abcmap.gui.ie.InteractionElement;

public class ShowAboutDialog extends InteractionElement {

	public ShowAboutDialog() {
		this.label = "A propos d'Abc-Map...";
		this.help = "Cliquez ici pour en savoir plus sur Abc-Map.";
	}

	@Override
	public void run() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AboutProjectDialog(guim.getMainWindow()).setVisible(true);
			}
		});

	}

}
