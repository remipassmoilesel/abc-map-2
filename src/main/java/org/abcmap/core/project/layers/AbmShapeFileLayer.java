package org.abcmap.core.project.layers;

import org.abcmap.core.draw.AbmDefaultFeatureType;
import org.abcmap.core.project.Project;
import org.abcmap.core.shapefile.ShapefileDao;
import org.abcmap.core.shapefile.ShapefileLayerEntry;
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
import java.nio.file.Paths;

public class AbmShapeFileLayer extends AbmAbstractLayer {

    /**
     * Contain information about shapefile to display
     */
    private ShapefileLayerEntry shapefileEntry;

    private SimpleFeatureStore featureStore;

    public AbmShapeFileLayer(String layerId, String title, boolean visible, int zindex, Path shapeFile, Project owner) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, AbmLayerType.SHAPE_FILE), shapeFile, owner);
    }

    public AbmShapeFileLayer(LayerIndexEntry entry, Path shapefilePath, Project owner) throws IOException {
        super(owner, entry);

        ShapefileDao dao = new ShapefileDao(project.getDatabasePath());

        // path is null, this should be an existing layer
        if (shapefilePath == null) {
            shapefileEntry = (ShapefileLayerEntry) dao.readById(entry.getLayerId());
            if (shapefileEntry == null) {
                throw new IOException("Specified path is null, and no shapefile entry can be found. Specify a path or use another layer ID");
            }

            shapefilePath = Paths.get(shapefileEntry.getPath());
        }

        // url is not null, this is a new layer, create entry and save it
        else {
            shapefileEntry = new ShapefileLayerEntry(indexEntry.getLayerId(), shapefilePath.toString(), 1l);
            dao.create(shapefileEntry);
            dao.close();
        }

        // retrieve a shape file and add it to a map content
        FileDataStore datastore = FileDataStoreFinder.getDataStore(shapefilePath.toFile());
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
