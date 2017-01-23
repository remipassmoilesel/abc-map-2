package org.abcmap.tests.core.resources;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbmShapefileLayer;
import org.abcmap.core.resources.DistantResource;
import org.abcmap.core.resources.ShapefileResource;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Try to load locale list of WMS servers
 */
public class DistantShapefileLayerTest extends ManagerTreeAccessUtil {

    @BeforeClass
    public static void beforeClass() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws Exception {

        //
        // Test local list
        //

        ArrayList<DistantResource> index = mapm().getMainResourceIndex();

        for (DistantResource res : index) {

            System.out.println(res);

            if (res instanceof ShapefileResource) {
                ShapefileResource shapeRes = (ShapefileResource) res;
                if (shapeRes.getResourcePath().equals("world_simplified_land_polygons.zip")) {

                    Project project = Main.getProjectManager().getProject();
                    AbmShapefileLayer layer = shapeRes.getDistantLayer(project, (path) -> {
                        System.out.println();
                        System.out.println(path);
                    }, 50);

                    break;
                }
            }
        }

    }

}
