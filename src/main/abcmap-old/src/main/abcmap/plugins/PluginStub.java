package abcmap.plugins;

import abcmap.gui.ie.InteractionElement;

/**
 * Squelette de plugin. Copier et modifier cette classe, puis utiliser un des
 * scripts de construction du projet.
 * 
 * @author remipassmoilesel
 *
 */
public class PluginStub extends InteractionElement {

	private static final String message = "Avis aux contributeurs: ce module ne fait rien encore mais "
			+ "peut faire quelque chose pour vous !";

	public PluginStub() {
		label = "Premier module !";
		help = message;
	}

	@Override
	public void run() {
		guim.showMessageInBox(message);
	}

}
