package org.abcmap.core.tileanalyse;

import org.abcmap.core.tiles.TileContainer;

import java.io.IOException;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public interface TileSource {

    /**
     * Return a new image container.
     * <p>
     * The images must be served in reverse order of addition,
     *
     * @return
     */
    public TileContainer next() throws IOException;

    /**
     * Reset the image source
     *
     * @return
     */
    public void reset();
}
