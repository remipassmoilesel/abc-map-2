package abcmap.cancel.memento;

import abcmap.project.properties.AcceptPropertiesContainer;
import abcmap.project.properties.PropertiesContainer;

/**
 * Extension de la classe MementoManager adaptée aux objets acceptant des
 * conteneurs de propriétés.
 * 
 * @author remipassmoilesel
 *
 */
public class PropertiesContainerCanceler extends
		MementoManager<PropertiesContainer> {

	private AcceptPropertiesContainer owner;

	public PropertiesContainerCanceler(AcceptPropertiesContainer owner) {
		this.owner = owner;
	}

	@Override
	public PropertiesContainer saveState() {
		return owner.getProperties();
	}

	@Override
	protected void setState(PropertiesContainer st) {
		owner.setProperties(st);
	}

}
