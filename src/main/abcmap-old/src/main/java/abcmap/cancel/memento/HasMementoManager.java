package abcmap.cancel.memento;

/**
 * Interface d'accés au conteneur d'états.
 * 
 * @author remipassmoilesel
 *
 * @param <E>
 */
public interface HasMementoManager<E> {
	public MementoManager<E> getMementoManager();
}
