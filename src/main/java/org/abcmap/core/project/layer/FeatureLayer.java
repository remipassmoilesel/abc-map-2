package org.abcmap.core.project.layer;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by remipassmoilesel on 16/11/16.
 */
public class FeatureLayer extends AbstractLayer {

    protected SimpleFeatureStore featureStore;
    protected DefaultFeatureBuilder featureBuilder;
    protected SimpleFeatureSource featureSource;

    public FeatureLayer(String layerId, String title, boolean visible, int zindex, FeatureSource source) {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.FEATURES), source);
    }

    public FeatureLayer(LayerIndexEntry entry, FeatureSource source) {
        super(entry);

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getDefaultFeatureBuilder(entry.getLayerId(), crs);

        if (source == null) {
            throw new NullPointerException("Feature source cannot be null");
        }

        this.featureSource = (SimpleFeatureSource) source;
        this.featureStore = (SimpleFeatureStore) featureSource;
        this.internalLayer = new org.geotools.map.FeatureLayer(featureSource, style);

    }

    /**
     * Return calculated bounds of layer
     *
     * @return
     */
    @Override
    public ReferencedEnvelope getBounds() {
        try {
            return featureSource.getFeatures().getBounds();
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    /**
     * Wrap a geometry in a DefaultSimpleFeature and add it to this layer
     * <p>
     * Return the feature added
     * <p>
     * TODO: find a way to remove synchronize attribute ?
     *
     * @param geom
     */
    public SimpleFeature addShape(Geometry geom) {
        SimpleFeature feature = featureBuilder.build(geom);
        return addFeature(feature);
    }

    /**
     * Add a feature to the layer. Can be used to write modifications on feature.
     *
     * @param feature
     * @return
     */
    public SimpleFeature addFeature(SimpleFeature feature) {

        try {
            featureStore.addFeatures(FeatureUtils.asList(feature));
            return feature;
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }

    }

    /**
     * Update specified feature
     *
     * @param feature
     */
    public SimpleFeature updateFeature(SimpleFeature feature) {

        try {
            HashMap<Name, Object> attributes = FeatureUtils.getAttributes(feature);
            Set<Name> keyset = attributes.keySet();
            Name[] names = keyset.toArray(new Name[keyset.size()]);
            Object[] values = attributes.values().toArray();

            featureStore.modifyFeatures(names, values, FeatureUtils.getIdFilter(feature.getID()));
            return feature;
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    /**
     * Remove features from layer
     *
     * @param features
     */
    public void removeFeatures(SimpleFeature... features) {

        // create a set
        HashSet<Identifier> ids = new HashSet<>();
        for (SimpleFeature feature : features) {
            ids.add(feature.getIdentifier());
        }

        // try to remove
        try {
            featureStore.removeFeatures(ff.id(ids));
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    /**
     * Execute a visit on this layer.
     *
     * @param visitor
     */
    public void executeVisit(LayerVisitor visitor) {
        LayerVisitExecutor executor = new LayerVisitExecutor(this);
        executor.execute(visitor, null);
    }

    /**
     * Execute a visit on this layer. Filter can be null.
     *
     * @param visitor
     * @param filter
     */
    public void executeVisit(LayerVisitor visitor, Filter filter) {
        LayerVisitExecutor executor = new LayerVisitExecutor(this);
        executor.execute(visitor, filter);
    }

    /**
     * Return selected features or null if nothing found
     */
    public SimpleFeature getFeatureById(String id) {

        Filter filter = FeatureUtils.getIdFilter(id);

        try {
            SimpleFeatureIterator features = featureSource.getFeatures(filter).features();

            if (features.hasNext() == false) {
                return null;
            } else {

                SimpleFeature feat = features.next();

                while (features.hasNext()) {
                    logger.warning("Double id value found: " + features.next());
                }

                features.close();

                return feat;
            }

        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

}
