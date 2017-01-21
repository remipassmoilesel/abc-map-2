package org.abcmap.core.project.layers;

import com.vividsolutions.jts.geom.Geometry;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.map.Layer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.Identifier;

import java.io.IOException;
import java.util.*;

/**
 * Created by remipassmoilesel on 16/11/16.
 */
public class AbmFeatureLayer extends AbmAbstractLayer {

    protected SimpleFeatureStore featureStore;
    protected AbmSimpleFeatureBuilder featureBuilder;

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
     * @throws IOException
     */
    public AbmFeatureLayer(String layerId, String title, boolean visible, int zindex, Project owner) throws IOException {
        this(new LayerIndexEntry(layerId, title, visible, zindex, AbmLayerType.FEATURES), owner);
    }

    /**
     * @param entry
     * @param owner
     * @throws IOException
     */
    public AbmFeatureLayer(LayerIndexEntry entry, Project owner) throws IOException {
        super(owner, entry);

        setReadableType("Couche de dessin");

        JDBCDataStore datastore = SQLUtils.getGeotoolsDatastoreFromH2(project.getDatabasePath());

        // open an existing feature store or create a new one
        try {
            this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getLayerId());
        } catch (IOException e) {
            logger.debug(e);

            // create a simple feature type
            SimpleFeatureType type = AbmSimpleFeatureBuilder.getDefaultFeatureType(entry.getLayerId(), owner.getCrs());
            datastore.createSchema(type);

            this.featureStore = (SimpleFeatureStore) datastore.getFeatureSource(entry.getLayerId());
        }

        // create a feature builder associated with the layer
        this.featureBuilder = FeatureUtils.getDefaultFeatureBuilder(entry.getLayerId(), owner.getCrs());

        buildInternalLayer();

        addStartRectangle();
    }

    /**
     * Add a first rectangle on empty layer, to prevent errors
     */
    private void addStartRectangle() {
        addShape(JTS.toGeometry(new ReferencedEnvelope(-25.57984295771083, 28.51079979542488, 45.61156329232754, 72.48196247927211, project.getCrs())));
    }

    @Override
    public Layer buildGeotoolsLayer() {
        return new org.geotools.map.FeatureLayer(featureStore, layerStyle);
    }


    /**
     * Return calculated bounds of layer
     *
     * @return
     */
    @Override
    public ReferencedEnvelope getBounds() {
        try {
            return featureStore.getFeatures().getBounds();
        } catch (IOException e) {
            logger.error(e);
            throw new LayerIOException(e);
        }
    }

    public SimpleFeatureStore getFeatureSource() {
        return featureStore;
    }

    /**
     * Wrap a geometry in a DefaultSimpleFeature and add it to this layer
     * <p>
     * Return the feature added
     * <p>
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
        removeFeatures(Arrays.asList(features));
    }

    /**
     * Remove features from layer
     *
     * @param features
     */
    public void removeFeatures(List<SimpleFeature> features) {

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
     * Return collection of feature corresponding to this filter
     *
     * @param filter
     * @return
     */
    public FeatureCollection getFeatures(Filter filter) throws IOException {
        return getInternalLayer().getFeatureSource().getFeatures(filter);
    }

    /**
     * Return selected features or null if nothing found
     */
    public SimpleFeature getFeatureById(String id) {

        Filter filter = FeatureUtils.getIdFilter(id);

        try {
            SimpleFeatureIterator features = featureStore.getFeatures(filter).features();

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

    /**
     * Return feature builder used in this layer. This feature builder has a special type name, corresponding to layer ID.
     *
     * @return
     */
    public AbmSimpleFeatureBuilder getFeatureBuilder() {
        return featureBuilder;
    }
}
