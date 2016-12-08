package org.abcmap.core.utils;

import org.abcmap.core.draw.feature.DefaultFeatureBuilder;
import org.abcmap.core.draw.feature.TileFeatureBuilder;
import org.abcmap.core.styles.StyleContainer;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.styling.StyleFactory;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class FeatureUtils {

    private static final FilterFactory ff = getFilterFactory();

    /**
     * Get the default feature builder
     *
     * @param name
     * @return
     */
    public static DefaultFeatureBuilder getDefaultFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new DefaultFeatureBuilder(name, crs);
    }

    /**
     * Return a tile outline feature builder
     *
     * @param name
     * @param crs
     * @return
     */
    public static TileFeatureBuilder getTileFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new TileFeatureBuilder(name, crs);
    }

    /**
     * Return the ID of feature or null.
     * <p>
     * Can be usefull to change the default ID field
     *
     * @param feature
     * @return
     */
    public static String getId(SimpleFeature feature) {
        return feature.getID();
    }

    /**
     * Return a list of features
     *
     * @param features
     * @return
     */
    public static FeatureCollection asList(SimpleFeature... features) {
        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        for (SimpleFeature f :
                features) {
            coll.add(f);
        }
        return coll;
    }

    /**
     * Return a style factory
     *
     * @return
     */
    public static StyleFactory getStyleFactory() {
        return CommonFactoryFinder.getStyleFactory();
    }

    /**
     * Return a filter factory
     *
     * @return
     */
    public static FilterFactory getFilterFactory() {
        return CommonFactoryFinder.getFilterFactory();
    }

    /**
     * Return a filter for specified ids
     *
     * @param ids
     * @return
     */
    public static Filter getIdFilter(String... ids) {
        HashSet<Identifier> set = new HashSet<>();
        for (String id : ids) {
            set.add(new FeatureIdImpl(id));
        }
        return ff.id(set);
    }

    /**
     * Apply a style to specified feature
     * <p>
     * To apply this style, this method insert style id in "style_id" field.
     * <p>
     * If the are no appropriate field, a IllegalAttributeException is raised
     *
     * @param features
     * @param style
     */
    public static void applyStyleToFeatures(StyleContainer style, SimpleFeature... features) {

        if (features == null) {
            throw new NullPointerException("Feature cannot be null");
        }

        if (style == null) {
            throw new NullPointerException("Style cannot be null");
        }

        // apply style to feature
        for (SimpleFeature feature : features) {
            feature.setAttribute(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME, style.getId());
        }

    }

    /**
     * Get the style id or null of a DefaultSimpleFeature
     *
     * @param feature
     * @return
     */
    public static String getStyleId(SimpleFeature feature) {

        if (feature == null) {
            throw new NullPointerException("Feature cannot be null");
        }

        return (String) feature.getAttribute(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME);
    }

    /**
     * Return a map containing all attributes and values
     *
     * @param feature
     */
    public static HashMap<Name, Object> getAttributes(SimpleFeature feature) {

        HashMap<Name, Object> attrs = new HashMap<>();
        Collection<Property> properties = feature.getProperties();

        for (Property p : properties) {
            attrs.put(p.getName(), p.getValue());
        }

        return attrs;
    }

    /**
     * Close the datastore associated with a feature source
     * <p>
     * /!\ Be careful, it will close other feature source too.
     *
     * @param store
     */
    public static void closeFeatureStore(FeatureStore store) {
        store.getDataStore().dispose();
    }

}