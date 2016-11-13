package org.abcmap.core.utils;

import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.feature.simple.SimpleFeature;

public class FeatureUtils {

    /**
     * Get the default feature builder
     *
     * @param name
     * @return
     */
    public static DefaultFeatureBuilder getDefaultFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new DefaultFeatureBuilder(name, crs);
    }

    public static String generateFeatureId() {
        return SimpleFeatureBuilder.createDefaultFeatureId();
    }

    public static FeatureCollection asList(SimpleFeature... features) {
        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        for (SimpleFeature f :
                features) {
            coll.add(f);
        }
        return coll;
    }

    public static StyleFactory getStyleFactory() {
        return CommonFactoryFinder.getStyleFactory();
    }

    public static FilterFactory getFilterFactory() {
        return CommonFactoryFinder.getFilterFactory();
    }

}