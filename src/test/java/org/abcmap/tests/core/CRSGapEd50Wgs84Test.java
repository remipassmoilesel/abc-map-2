package org.abcmap.tests.core;

import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeoUtils;
import org.abcmap.core.utils.Utils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.filter.FilterFactory2;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This example show weird behavior with Geotools. If a converted shape file from WGS84 to ED50 is displayed on the original file, a gap will be displayed between shape files.
 * <p>
 * To reproduce this: download a shape file, transform it with QGis 2.6, modify the code below and launch it
 */
public class CRSGapEd50Wgs84Test {

    private static final FilterFactory2 ff = FeatureUtils.getFilterFactory();
    private static final StyleFactory sf = FeatureUtils.getStyleFactory();

    public static void main(String[] args) throws IOException {

        MapContent content = new MapContent();

        Path shapeFileWgs84 = Paths.get("data/france-communes/communes-20160119.shp");
        Path shapeFileEd50 = Paths.get("data/france-communes-ed50/france-communes-ed50.shp");

        // file 1
        FileDataStore datastore1 = FileDataStoreFinder.getDataStore(shapeFileWgs84.toFile());
        SimpleFeatureStore featureStore1 = (SimpleFeatureStore) datastore1.getFeatureSource();

        Rule rule = FeatureUtils.createRuleFor(Utils.randColor(), null, 0.5f);
        Style style = sf.createStyle();
        style.featureTypeStyles().add(sf.createFeatureTypeStyle(new Rule[]{rule}));

        FeatureLayer layer1 = new FeatureLayer(featureStore1, style);

        // file 2
        FileDataStore datastore2 = FileDataStoreFinder.getDataStore(shapeFileEd50.toFile());
        SimpleFeatureStore featureStore2 = (SimpleFeatureStore) datastore2.getFeatureSource();

        Rule rule2 = FeatureUtils.createRuleFor(Utils.randColor(), null, 0.5f);
        Style style2 = sf.createStyle();
        style.featureTypeStyles().add(sf.createFeatureTypeStyle(new Rule[]{rule2}));

        FeatureLayer layer2 = new FeatureLayer(featureStore2, style2);

        content.addLayer(layer1);
        content.addLayer(layer2);

        // show in window
        GeoUtils.showInDebugWindow("", content);
    }

}
