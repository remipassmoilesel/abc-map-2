package org.abcmap.core.project.layers;

import org.abcmap.core.project.Project;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.SLD;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class ShapeFileLayer extends AbstractLayer {

    //private final SimpleFeatureStore featureStore;

    public ShapeFileLayer(String layerId, String title, boolean visible, int zindex, Project owner, Path shapeFile) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), owner, shapeFile);
    }

    public ShapeFileLayer(LayerIndexEntry entry, Project owner, Path shapeFile) throws IOException {

        super(owner, entry);

        // retrieve a shape file and add it to a mapcontent
        FileDataStore datastore = FileDataStoreFinder.getDataStore(shapeFile.toFile());
        SimpleFeatureSource shapeFileSource = datastore.getFeatureSource();

        this.internalLayer = new FeatureLayer(shapeFileSource, SLD.createLineStyle(Color.blue, 0.2f));

        //this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getLayerId());

    }

    @Override
    public ReferencedEnvelope getBounds() {
        return internalLayer.getBounds();
    }
}
