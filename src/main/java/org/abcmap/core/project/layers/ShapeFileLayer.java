package org.abcmap.core.project.layers;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public class ShapeFileLayer extends AbstractLayer {

    private SimpleFeatureStore featureStore;

    //private final SimpleFeatureStore featureStore;

    public ShapeFileLayer(String layerId, String title, boolean visible, int zindex, Project owner, Path shapeFile) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.TILES), owner, shapeFile);
    }

    public ShapeFileLayer(LayerIndexEntry entry, Project owner, Path shapeFile) throws IOException {

        super(owner, entry);

        // retrieve a shape file and add it to a mapcontent
        FileDataStore datastore = FileDataStoreFinder.getDataStore(shapeFile.toFile());
        this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource();

        // add random style
        Rule rule = FeatureUtils.createRuleFor(Utils.randColor(), null, 0.5f);
        layerStyle.featureTypeStyles().add(sf.createFeatureTypeStyle(new Rule[]{rule}));

        // create internal layer
        this.internalLayer = new FeatureLayer(featureStore, layerStyle);

    }

    @Override
    public ReferencedEnvelope getBounds() {
        return internalLayer.getBounds();
    }

    public SimpleFeatureStore getFeatureStore() {
        return featureStore;
    }
}
