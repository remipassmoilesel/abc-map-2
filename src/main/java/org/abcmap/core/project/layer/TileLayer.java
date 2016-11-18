package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.Tile;
import org.geotools.geopkg.TileEntry;
import org.geotools.geopkg.TileMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TileLayer extends AbstractLayer {

    private final TileEntry geopkgEntry;
    private final TileMatrix tileMatrix;

    public TileLayer(String layerId, String title, boolean visible, int zindex, GeoPackage geopkg) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), geopkg);
    }

    public TileLayer(LayerIndexEntry layerIndexEntry, GeoPackage geopkg) throws IOException {

        super(layerIndexEntry);

        // create a geopackage entry
        geopkgEntry = new TileEntry();
        geopkgEntry.setTableName("tiles_" + layerIndexEntry.getLayerId());
        geopkgEntry.setBounds(new ReferencedEnvelope(0, 0, 800, 800, crs));

        tileMatrix = new TileMatrix(0, 1, 1, 256, 256, 1d, 1d);
        geopkgEntry.getTileMatricies().add(tileMatrix);
        geopkg.create(geopkgEntry);

    }

    public void addTile(InputStream stream, Coordinate position){

    }

    @Override
    public ReferencedEnvelope getBounds() {
        throw new IllegalStateException("Not implemented for now");
    }
}
