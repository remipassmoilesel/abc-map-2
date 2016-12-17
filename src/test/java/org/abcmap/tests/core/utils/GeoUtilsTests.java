package org.abcmap.tests.core.utils;

import org.abcmap.core.utils.GeoUtils;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 17/12/16.
 */
public class GeoUtilsTests {

    @Test
    public void tests() throws Exception {

        List<CoordinateReferenceSystem> crsList = Arrays.asList(
                DefaultGeocentricCRS.CARTESIAN,
                DefaultGeographicCRS.WGS84,
                DefaultEngineeringCRS.CARTESIAN_2D,
                DefaultEngineeringCRS.GENERIC_2D,
                CRS.decode("EPSG:4230"),
                CRS.decode("EPSG:2099"),
                CRS.decode("EPSG:2057"),
                CRS.decode("EPSG:2111")
        );

        // test serialization and reverse
        for (CoordinateReferenceSystem crs : crsList) {
            String s = GeoUtils.crsToString(crs);
            CoordinateReferenceSystem crs2 = GeoUtils.stringToCrs(s);
            assertTrue("CRS serialization test ", crs.equals(crs2));
        }

        // check null pointer ex
        boolean error = false;
        try {
            GeoUtils.crsToString(null);
        } catch (Exception e) {
            error = true;
        }
        assertTrue("CRS serialization test 2", error);

        // check fail id ex
        error = false;
        try {
            GeoUtils.stringToCrs("non existing identifier");
        } catch (Exception e) {
            error = true;
        }
        assertTrue("CRS serialization test 3", error);

    }

}
