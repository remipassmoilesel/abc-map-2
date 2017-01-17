package org.abcmap.core.project.layers;

import org.abcmap.core.draw.AbmDefaultFeatureType;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.styling.Rule;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class AbmShapeFileLayer extends AbmAbstractLayer {

    private SimpleFeatureStore featureStore;

    //private final SimpleFeatureStore featureStore;

    public AbmShapeFileLayer(String layerId, String title, boolean visible, int zindex, Project owner, Path shapeFile) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, AbmLayerType.SHAPE_FILE), owner, shapeFile);
    }

    public AbmShapeFileLayer(LayerIndexEntry entry, Project owner, Path shapeFile) throws IOException {

        super(owner, entry);

        // retrieve a shape file and add it to a map content
        FileDataStore datastore = FileDataStoreFinder.getDataStore(shapeFile.toFile());
        this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource();

        // add random color style
        // TODO: detect type of feature and make corresponding style
        Color color = Utils.randColor();
        Rule rule1 = FeatureUtils.createRuleFor(AbmDefaultFeatureType.LINE, color, null, 0.5f);
        Rule rule2 = FeatureUtils.createRuleFor(AbmDefaultFeatureType.POINT, color, null, 0.5f);
        Rule rule3 = FeatureUtils.createRuleFor(AbmDefaultFeatureType.POLYGON, color, null, 0.5f);
        layerStyle.featureTypeStyles().add(sf.createFeatureTypeStyle(new Rule[]{rule1, rule2, rule3}));

        buildInternalLayer();
    }

    @Override
    protected void buildInternalLayer() {

        // dispose previous layer if needed
        if (this.internalLayer != null) {
            internalLayer.dispose();
        }

        // create internal layer
        this.internalLayer = new FeatureLayer(featureStore, layerStyle);

    }

    @Override
    public Layer buildGeotoolsLayer() {
        return new org.geotools.map.FeatureLayer(featureStore, layerStyle);
    }

    @Override
    public ReferencedEnvelope getBounds() {
        return internalLayer.getBounds();
    }

    public SimpleFeatureStore getFeatureStore() {
        return featureStore;
    }
}
