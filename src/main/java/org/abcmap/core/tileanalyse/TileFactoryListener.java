package org.abcmap.core.tileanalyse;

import org.abcmap.core.events.ImportManagerEvent;

/**
 * Listen a tile factory
 */
public interface TileFactoryListener {
    public void eventHappened(ImportManagerEvent event);
}
