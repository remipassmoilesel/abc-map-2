package org.abcmap.core.utils;

import org.abcmap.core.draw.AbmGeometryType;
import org.abcmap.core.draw.builder.AbmSimpleFeatureBuilder;
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
import java.util.*;
import java.util.List;

public class FeatureUtils {

    private final static StyleFactory sf = FeatureUtils.getStyleFactory();
    private final static FilterFactory ff = FeatureUtils.getFilterFactory();

    /**
     * Get the default feature builder
     *
     * @param name
     * @return
     */
    public static AbmSimpleFeatureBuilder getDefaultFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new AbmSimpleFeatureBuilder(name, crs);
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
     * Return a list of features which can be added to a feature store
     *
     * @param features
     * @return
     */
    public static FeatureCollection asList(SimpleFeature... features) {
        return asFeatureCollection(Arrays.asList(features));
    }

    /**
     * Return a list of features which can be added to a feature store
     *
     * @param features
     * @return
     */
    public static FeatureCollection asFeatureCollection(List<SimpleFeature> features) {
        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        for (SimpleFeature f : features) {
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
     * Return a filter factory (FilterFactory2)
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
            feature.setAttribute(AbmSimpleFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME, style.getId());
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

        return AbmSimpleFeatureBuilder.getStyleId(feature);
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

    /**
     * Create a simple style for specified geometry class
     *
     * @param type
     * @param foreground
     * @param background
     * @param thick
     * @return
     */
    public static Style createStyleFor(AbmGeometryType type, Color foreground, Color background, double thick) {

        Rule r = createRuleFor(type, foreground, background, thick);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(r);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Create a simple rule for specified geometry class
     *
     * @param foreground
     * @param background
     * @param thick
     * @param type
     * @return
     */
    public static Rule createRuleFor(AbmGeometryType type, Color foreground, Color background, double thick) {

        // create stroke and optional fill
        Stroke stroke = sf.stroke(ff.literal(foreground), null, null, null, null, null, null);
        Fill fill = null;
        if (background != null) {
            fill = sf.fill(null, ff.literal(background), ff.literal(1.0));
        }

        // create rule for points
        if (AbmGeometryType.POINT.equals(type)) {

            // create point symbolizer
            Mark mark = sf.getCircleMark();
            mark.setStroke(stroke);
            mark.setFill(fill);

            Graphic graphic = sf.createDefaultGraphic();
            graphic.graphicalSymbols().clear();
            graphic.graphicalSymbols().add(mark);
            graphic.setSize(ff.literal(thick));

            PointSymbolizer pointSym = sf.createPointSymbolizer(graphic, null);
            Rule r = sf.createRule();
            r.symbolizers().add(pointSym);

            return r;
        }

        // create rule for lines
        else if (AbmGeometryType.LINE.equals(type)) {
            LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);
            Rule r = sf.createRule();
            r.symbolizers().add(lineSym);
            return r;
        }

        // create rule for polygon
        else if (AbmGeometryType.POLYGON.equals(type)) {

            // create polygon symbolizer
            PolygonSymbolizer polygonSym = sf.createPolygonSymbolizer(stroke, fill, null);

            // create rule
            Rule r = sf.createRule();
            r.symbolizers().add(polygonSym);

            return r;
        }

        // generic rule
        else if (type == null) {

            Rule r = sf.createRule();

            // create point symbolizer
            Mark mark = sf.getCircleMark();
            mark.setStroke(stroke);
            mark.setFill(fill);

            Graphic graphic = sf.createDefaultGraphic();
            graphic.graphicalSymbols().clear();
            graphic.graphicalSymbols().add(mark);
            graphic.setSize(ff.literal(thick));

            PointSymbolizer pointSym = sf.createPointSymbolizer(graphic, null);
            r.symbolizers().add(pointSym);

            // line symbolizer
            LineSymbolizer lineSym = sf.createLineSymbolizer(stroke, null);
            r.symbolizers().add(lineSym);

            // polygon symbolizer
            PolygonSymbolizer polygonSym = sf.createPolygonSymbolizer(stroke, fill, null);
            r.symbolizers().add(polygonSym);

            return r;
        }

        // unknown style
        else {
            throw new IllegalArgumentException("Unknown geometry: " + type);
        }

    }
}