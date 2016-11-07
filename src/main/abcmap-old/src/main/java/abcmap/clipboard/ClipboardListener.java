package abcmap.clipboard;

import abcmap.events.ClipboardEvent;

public interface ClipboardListener {
	/**
	 * Une image vient arrive en provenance du presse papier.
	 * 
	 * @param image
	 */
	public void clipboardChanged(ClipboardEvent event);
}
