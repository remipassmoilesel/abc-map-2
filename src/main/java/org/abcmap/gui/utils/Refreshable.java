package org.abcmap.gui.utils;

public interface Refreshable {

	/**
	 * Revalide et rafraichit un composant.
	 */
	public void refresh();

	/**
	 * Reconstruit un composant après une modification, et appelle refresh()
	 * ensuite.
	 */
	public void reconstruct();
}
