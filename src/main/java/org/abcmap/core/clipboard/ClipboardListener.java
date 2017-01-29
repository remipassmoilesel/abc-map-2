package org.abcmap.core.clipboard;

import org.abcmap.core.events.ClipboardEvent;

public interface ClipboardListener {

    /**
     * Clipboard have just changed
     *
     * @param event
     */
    public void clipboardChanged(ClipboardEvent event);
}
