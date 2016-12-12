package org.abcmap.gui.components.help;

/**
 * List several sequences in order to provde a simple help
 */
public class InteractionSequence {
    private Interaction[] sequence;
    private String description;

    public InteractionSequence(String description, Interaction[] sequence) {
        this.sequence = sequence;
        this.description = description;
    }

    public InteractionSequence(String description, Interaction inter) {
        this(description, new Interaction[]{inter});
    }

    public Interaction[] getSequence() {
        return sequence;
    }

    public String getDescription() {
        return description;
    }
}
