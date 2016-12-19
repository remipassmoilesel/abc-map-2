package org.abcmap.ielements.program;

import org.abcmap.ielements.InteractionElement;

public class ShowAboutDialog extends InteractionElement {

    public ShowAboutDialog() {
        this.label = "A propos d'Abc-Map...";
        this.help = "Cliquez ici pour en savoir plus sur Abc-Map.";
    }

    @Override
    public void run() {
        /*

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AboutProjectDialog(guim.getMainWindow()).setVisible(true);
			}
		});

		*/
    }

}
