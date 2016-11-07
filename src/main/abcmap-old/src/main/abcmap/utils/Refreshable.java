package abcmap.utils;

/**
 * Composant graphique reconstruisable. Un tel composant doit pouvoir se
 * reconstruire et se revalider/repeindre à l'appel des méthodes reconstruct et
 * refresh.
 * <p>
 * Normalement le composant doit appeler refresh() à la fin de reconstruct();
 * 
 * @author remipassmoilesel
 *
 */
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
