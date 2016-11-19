package org.abcmap.core.project;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.abcmap.TestUtils;
import org.abcmap.core.utils.FeatureUtils;
import org.abcmap.core.utils.GeomUtils;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.data.FeatureStore;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Small test about Geopackage and Datastore life cycle
 */
public class DatastoreTest {

    private static final GeometryFactory geom = GeomUtils.getGeometryFactory();

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException {

        Path tempDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("datastoreTest");
        Files.createDirectories(tempDir);

        // create a geopackage
        Path directory = TestUtils.PLAYGROUND_DIRECTORY.resolve("shapeIdIssue");
        Files.createDirectories(directory);

        Path db = directory.resolve("geopkg.db");
        Files.deleteIfExists(db);
        Files.createFile(db);

        GeoPackage geopkg = new GeoPackage(db.toFile());
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

        JDBCDataStore datastore = SQLUtils.getDatastoreFromGeopackage(geopkg.getFile().toPath());
        FeatureStore featureStore = (FeatureStore) datastore.getFeatureSource("feature1");

        for (int i = 0; i < 100; i++) {
            fbuilder.add(geom.createPoint(TestUtils.getRandomPoint()));
            fbuilder.buildFeature(null);

            featureStore.addFeatures(FeatureUtils.asList(fbuilder.buildFeature(null)));
        }

        // test reading  after geopackage closing
        geopkg.close();

        int count = 0;
        FeatureIterator it = featureStore.getFeatures().features();
        while (it.hasNext()) {
            Feature f = it.next();
            count++;
        }
        it.close();

        assertTrue("Use featurestore after closing geopackage", count == 100);

        // test reading after datastore closing
        datastore.dispose();

        boolean exception = false;
        try {
            featureStore.getFeatures().features();
        } catch (Exception e) {
            exception = true;
        }
        assertTrue("Datastore is unavailable after dispose() call", exception);


    }

}
