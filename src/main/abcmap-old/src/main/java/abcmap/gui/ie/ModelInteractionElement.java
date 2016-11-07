package abcmap.gui.ie;

/**
 * Classe utilitaire permettant la création de maquettes de GUI. Permet
 * seulement d'instancier un element d'interaction temporairement, ne doit pas
 * être utilisé en production.
 * 
 * @author remipassmoilesel
 *
 */
@Deprecated
public class ModelInteractionElement extends InteractionElement {

	public ModelInteractionElement() {
		super();
	}

	public ModelInteractionElement(String label, String help) {
		super();
		this.label = label;
		this.help = help;
	}

}
