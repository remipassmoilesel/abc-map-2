package abcmap.importation.tile;

import abcmap.events.ImportEvent;

/**
 * Interface d'écoute d'un objet d'import
 * 
 * @author remipassmoilesel
 *
 */
public interface ImportEventListener {
	public void importEventHapened(ImportEvent event);
}
