package org.abcmap.core.utils;

import com.vividsolutions.jts.geom.Geometry;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class FeatureUtils {

    /**
     * Get a simple feature builder, building single field features with only geometry
     *
     * @param name
     * @return
     */
    public static SimpleFeatureBuilder getSimpleFeatureBuilder(String name, CoordinateReferenceSystem crs) {
        return new org.geotools.feature.simple.SimpleFeatureBuilder(getSimpleFeatureType(name, crs));
    }

    /**
     * Get a simple feature type, with single field with only geometry
     *
     * @param name
     * @return
     */
    public static SimpleFeatureType getSimpleFeatureType(String name, CoordinateReferenceSystem crs) {

        // create a feature type
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(name);
        tbuilder.setCRS(crs);
        tbuilder.add("geometry", Geometry.class);

        return tbuilder.buildFeatureType();
    }


    public static String getFeatureId(String prefix) {
        return prefix + "_" + System.nanoTime();
    }

    public static FeatureCollection asList(SimpleFeature... features) {
        DefaultFeatureCollection coll = new DefaultFeatureCollection();
        for (SimpleFeature f :
                features) {
            coll.add(f);
        }
        return coll;
    }

    public static StyleFactory getStyleFactory(){
        return CommonFactoryFinder.getStyleFactory();
    }

    public static FilterFactory getFilterFactory(){
        return CommonFactoryFinder.getFilterFactory();
    }

}