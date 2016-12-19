package org.abcmap.ielements.program;

import org.abcmap.ielements.InteractionElement;

public class ShowSupportProjectDialog extends InteractionElement {

    public ShowSupportProjectDialog() {
        this.label = "Soutenez le projet !";
        this.help = "Cliquez ici pour soutenir le projet Abc-Map: votes, dons, communication, ...";
    }

    @Override
    public void run() {

		/*
        SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new SupportProjectDialog(guim.getMainWindow()).setVisible(true);
			}
		});
		*/

    }

}
