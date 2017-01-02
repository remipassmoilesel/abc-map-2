package org.abcmap.core.utils;

import org.abcmap.core.draw.builder.DefaultSimpleFeatureBuilder;
import org.abcmap.core.styles.StyleContainer;
import org.abcmap.core.tiles.TileFeatureBuilder;
import org.geotools.data.FeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.Identifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class FeatureUtils {

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    /**
     * Get the default feature builder
     *
     * @param name
     * @return
     */
    public static DefaultSimpleFeatureBuilder getDefaultFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new DefaultSimpleFeatureBuilder(name, crs);
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
    public static FilterFactory2 getFilterFactory() {
        return CommonFactoryFinder.getFilterFactory2();
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
            feature.setAttribute(DefaultSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME, style.getId());
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

        return (String) feature.getAttribute(DefaultSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME);
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

    public static Rule createRuleFor(Color foreground, Color background, double thick) {

        // create point symbolizer
        Stroke stroke = sf.stroke(ff.literal(foreground), null, null, null, null, null, null);
        Fill fill = sf.fill(null, ff.literal(background), ff.literal(1.0));

        Mark mark = sf.getCircleMark();
        mark.setFill(fill);
        mark.setStroke(stroke);

        Graphic graphic = sf.createDefaultGraphic();
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(mark);
        graphic.setSize(ff.literal(thick));

        // here we can specify name of geometry field. Set to null allow to not specify it
        PointSymbolizer pointSym = sf.createPointSymbolizer(graphic, null);

        // create line symbolizer
        LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);

        // create polygon symbolizer
        PolygonSymbolizer polygonSym = sf.createPolygonSymbolizer(stroke, fill, null);

        // create rule
        Rule r = sf.createRule();
        r.symbolizers().add(pointSym);
        r.symbolizers().add(lineSym);
        r.symbolizers().add(polygonSym);

        return r;

    }
}