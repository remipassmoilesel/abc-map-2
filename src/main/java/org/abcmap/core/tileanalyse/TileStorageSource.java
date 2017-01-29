package org.abcmap.core.tileanalyse;

import org.abcmap.core.tiles.TileContainer;
import org.abcmap.core.tiles.TileStorage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 22/11/16.
 */
public class TileStorageSource implements TileSource {

    private TileStorage storage;
    private String coverageName;
    private int offset;

    public TileStorageSource(TileStorage storage, String coverageName) {
        this.storage = storage;
        this.coverageName = coverageName;

        reset();
    }

    @Override
    public TileContainer next() throws IOException {

        offset ++;

        ArrayList<TileContainer> tiles = storage.getLastTiles(coverageName, offset, 1);

        // nothing more
        if (tiles == null || tiles.size() < 1) {
            return null;
        }

        // fake tile, get next
        if(tiles.get(0).getTileId().equals(TileStorage.FAKE_TILE_ID)){
            return next();
        }

        return tiles.get(0);
    }

    @Override
    public void reset() {
        offset = -1;
    }

}
