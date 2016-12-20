package org.abcmap.core.project.layers;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.draw.feature.DefaultFeatureBuilder;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
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

    /**
     * Create a feature layer object.
     * <p>
     * If you want to create a new layer, set create to true to initialize a new database place.
     * <p>
     * If you want to read an existing layer, set create to false.
     *
     * @param layerId
     * @param title
     * @param visible
     * @param zindex
     * @param owner
     * @param create
     * @throws IOException
     */
    public FeatureLayer(String layerId, String title, boolean visible, int zindex, Project owner, boolean create) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, LayerType.FEATURES), owner, create);
    }

    public FeatureLayer(LayerIndexEntry entry, Project owner, boolean create) throws IOException {
        super(owner, entry);

        JDBCDataStore datastore = SQLUtils.getDatastoreFromH2(project.getDatabasePath());

        // if true, create a new database entry
        if (create) {
            // create a simple feature type
            SimpleFeatureType type = DefaultFeatureBuilder.getDefaultFeatureType(entry.getLayerId(), owner.getCrs());
            datastore.createSchema(type);
        }

        this.featureSource = datastore.getFeatureSource(entry.getLayerId());
        this.featureStore = (SimpleFeatureStore) featureSource;

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getDefaultFeatureBuilder(entry.getLayerId(), owner.getCrs());
        this.internalLayer = new org.geotools.map.FeatureLayer(featureSource, layerStyle);
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

    public DefaultFeatureBuilder getFeatureBuilder() {
        return featureBuilder;
    }
}
