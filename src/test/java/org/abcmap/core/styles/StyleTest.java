package org.abcmap.core.styles;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.shapes.feature.DefaultFeatureBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.FeatureTypeStyle;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 21/11/16.
 */
public class StyleTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        // feature util test
        SimpleFeatureTypeBuilder tbuilder = new SimpleFeatureTypeBuilder();
        tbuilder.setName("wrongFeatureType");
        tbuilder.add("hey no !", String.class);

        SimpleFeatureType wrongType = tbuilder.buildFeatureType();
        SimpleFeatureBuilder fb = new SimpleFeatureBuilder(wrongType);
        StyleContainer style = new StyleContainer(Color.black, Color.white, 5);

        boolean styleApplied = false;
        try {
            FeatureUtils.applyStyleToFeatures(style, fb.buildFeature(null));
            styleApplied = true;
        } catch (Exception e) {
            //e.printStackTrace();
        }

        assertTrue("Style util exception test", styleApplied == false);

        DefaultFeatureBuilder fbuilder = FeatureUtils.getDefaultFeatureBuilder("somename", DefaultGeographicCRS.WGS84);
        SimpleFeature feat = fbuilder.build(null);

        FeatureUtils.applyStyleToFeatures(style, feat);

        assertTrue("Style util test", feat.getAttribute(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME).equals(style.getId()));

        Project project = MainManager.getProjectManager().getProject();
        StyleLibrary styleLib = project.getStyleLibrary();

        // style cache test
        StyleContainer style1 = styleLib.getStyle(Color.black, Color.blue, 5);
        StyleContainer style2 = styleLib.getStyle(Color.black, Color.blue, 5);

        assertTrue("Style cache test", style1 == style2);

        // style application test
        FeatureTypeStyle ftsApplied = styleLib.applyStyle(style1, project.getActiveLayer(), feat);

        assertTrue("Style application test", feat.getAttribute(DefaultFeatureBuilder.STYLE_ID_ATTRIBUTE_NAME).equals(style1.getId()));

        for (FeatureTypeStyle fts : project.getActiveLayer().getLayerStyle().featureTypeStyles()) {
            assertTrue("Style application test 2", fts == ftsApplied);
        }
    }

}
