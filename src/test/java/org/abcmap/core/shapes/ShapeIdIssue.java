package org.abcmap.core.shapes;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple demonstration of shape id issue with geotools and geopackage.
 * <p>
 * Here generated ids are all the same:
 * <p>
 * <p>
 * Before adding to feature source:
 * fid-6c609a7e_1585d46582c_-7ff9
 * fid-6c609a7e_1585d46582c_-7ffa
 * fid-6c609a7e_1585d46582c_-7ffb
 * fid-6c609a7e_1585d46582c_-7ffc
 * fid-6c609a7e_1585d46582c_-7ffd
 * fid-6c609a7e_1585d46582c_-7ffe
 * fid-6c609a7e_1585d46582c_-7fff
 * fid-6c609a7e_1585d46582c_-8000
 * <p>
 * After adding:
 * feature1.null
 * feature1.null
 * feature1.null
 * feature1.null
 * feature1.null
 * feature1.null
 */
public class ShapeIdIssue {

    public static final GeometryFactory geomBuilder = JTSFactoryFinder.getGeometryFactory();

    @Test
    public void test() throws IOException {

        // create a geopackage
        Path directory = TestUtils.PLAYGROUND_DIRECTORY.resolve("shapeIdIssue");
        Files.createDirectories(directory);

        Path db = directory.resolve("geopkg.db");
        Files.deleteIfExists(db);
        Files.createFile(db);

        GeoPackage geopkg = new GeoPackage(db.toFile());

        // create a feature type
        String featureId = "feature1";

        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName(featureId);
        tbuilder.setCRS(DefaultGeographicCRS.WGS84);
        tbuilder.add("geometry", Geometry.class);

        SimpleFeatureType type = tbuilder.buildFeatureType();
        SimpleFeatureBuilder fbuilder = new SimpleFeatureBuilder(type);

        FeatureEntry fe = new FeatureEntry();
        fe.setBounds(new ReferencedEnvelope());
        fe.setSrid(null);

        // get feature store from geopackage
        geopkg.create(fe, type);

        Map<String, String> params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", db.toString());

        JDBCDataStore datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
        FeatureStore featurestore = (FeatureStore) datastore.getFeatureSource(featureId);

        // add points to datastore
        DefaultFeatureCollection features = new DefaultFeatureCollection();

        for (int i = 100; i < 150; i++) {
            fbuilder.add(geomBuilder.createPoint(new Coordinate(i, i)));
            SimpleFeature feature = fbuilder.buildFeature(null);
            features.add(feature);
        }

        System.out.println("Before adding: ");
        SimpleFeatureIterator it = features.features();
        while (it.hasNext()) {
            System.out.println(it.next().getID());
        }

        featurestore.addFeatures(features);

        System.out.println();
        System.out.println("After adding: ");
        it = features.features();
        while (it.hasNext()) {
            System.out.println(it.next().getID());
        }

        datastore.dispose();
    }

}
