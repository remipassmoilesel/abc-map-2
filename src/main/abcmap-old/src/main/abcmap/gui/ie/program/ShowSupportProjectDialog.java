package abcmap.gui.ie.program;

import javax.swing.SwingUtilities;

import abcmap.gui.dialogs.AboutProjectDialog;
import abcmap.gui.dialogs.SupportProjectDialog;
import abcmap.gui.ie.InteractionElement;

public class ShowSupportProjectDialog extends InteractionElement {

	public ShowSupportProjectDialog() {
		this.label = "Soutenez le projet !";
		this.help = "Cliquez ici pour soutenir le projet Abc-Map: votes, dons, communication, ...";
	}

	@Override
	public void run() {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SupportProjectDialog(guim.getMainWindow()).setVisible(true);
			}
		});

	}

}
