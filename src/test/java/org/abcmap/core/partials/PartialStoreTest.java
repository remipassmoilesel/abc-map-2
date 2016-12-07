package org.abcmap.core.partials;


import org.abcmap.TestUtils;
import org.abcmap.core.managers.DrawManager;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.layer.FeatureLayer;
import org.abcmap.core.shapes.DrawManagerException;
import org.abcmap.core.shapes.LineBuilder;
import org.abcmap.core.shapes.PointBuilder;
import org.abcmap.core.shapes.PolygonBuilder;
import org.abcmap.core.utils.FeatureUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.awt.*;
import java.io.IOException;
import java.util.PrimitiveIterator;
import java.util.Random;

import static org.junit.Assert.assertTrue;

public class PartialStoreTest {

    @BeforeClass
    public static void beforeTests() throws IOException, InterruptedException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() {



    }


}
