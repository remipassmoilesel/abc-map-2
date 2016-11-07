package abcmap.cancel.memento;

/**
 * Conteneur d'état
 * 
 * @author remipassmoilesel
 *
 * @param <E>
 */
class HistoryElement<E> {

	/** Le nombre de fois que cet état a été restoré */
	public int restored = 0;

	/** Le nombre de fois que cet état a été refait */
	public int redone = 0;

	/** L'état a refaire */
	public E toRedo = null;

	/** L'état a restorer */
	public E toRestore = null;

	public HistoryElement(E toRestore, E toRedo) {
		this.toRestore = toRestore;
		this.toRedo = toRedo;
	}

}