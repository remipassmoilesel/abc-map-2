package abcmap.gui.iegroup;

import java.awt.Component;

import abcmap.gui.ie.InteractionElement;

/**
 * Element d'interaction spécial symbolique représentant un séparateur de groupe
 * d'interaction.
 * 
 * @author remipassmoilesel
 *
 */
public class IEGSeparator extends InteractionElement {
	public IEGSeparator() {
	}

	@Override
	public void run() {
		throw new IllegalStateException("Not a valid "
				+ super.getClass().getSimpleName() + ": "
				+ this.getClass().getSimpleName());
	}

	@Override
	public Component createPrimaryGUI() {
		throw new IllegalStateException("Not a valid "
				+ super.getClass().getSimpleName() + ": "
				+ this.getClass().getSimpleName());
	}
}
