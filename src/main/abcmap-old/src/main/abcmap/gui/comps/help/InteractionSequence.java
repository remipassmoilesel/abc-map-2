package abcmap.gui.comps.help;

/**
 * Sequences d'interactions pour effectuer une action.
 * 
 * @author remipassmoilesel
 *
 */
public class InteractionSequence {
	private Interaction[] sequence;
	private String description;

	public InteractionSequence(String description, Interaction[] sequence) {
		this.sequence = sequence;
		this.description = description;
	}

	public InteractionSequence(String description, Interaction inter) {
		this(description, new Interaction[] { inter });
	}

	public Interaction[] getSequence() {
		return sequence;
	}

	public String getDescription() {
		return description;
	}
}
