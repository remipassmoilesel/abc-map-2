package org.abcmap.tests.core.project;

import org.abcmap.Initialization;
import org.abcmap.core.managers.Main;
import org.abcmap.core.managers.MapManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.project.layers.AbstractLayer;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 13/12/16.
 */
public class ProjectTest {

    @Test
    public void test() throws IOException, InvocationTargetException, InterruptedException {

        // initialize managers
        Initialization.doInit(null);

        ProjectManager projectm = Main.getProjectManager();
        MapManager mapm = Main.getMapManager();

        // create a fake project
        projectm.createFakeProject(null);
        Project project = projectm.getProject();

        // Remove all layers. Project should always have at least one layer
        for (int j = 0; j < 3; j++) {
            project.removeLayer(0);
            int size = project.getLayersList().size();
            assertTrue("Layer remove test: " + size, size > 0);
        }

        // layer list should be a new list each time, and do not affect project
        ArrayList<AbstractLayer> list1 = project.getLayersList();
        ArrayList<AbstractLayer> list2 = project.getLayersList();
        list1.remove(0);
        assertTrue("Layer list test 1: " + list2.size(), list2.size() > 0);
        assertTrue("Layer list test 2: " + project.getLayersList().size(), project.getLayersList().size() > 0);

        // test layers sort
        project.addNewFeatureLayer(String.valueOf(5), true, 5);
        project.addNewFeatureLayer(String.valueOf(10), true, 10);
        project.addNewFeatureLayer(String.valueOf(30), true, 30);

        int lastZindex = -1;
        for (AbstractLayer lay : project.getLayersList()) {

            if (lastZindex == -1) {
                lastZindex = lay.getZindex();
                continue;
            }

            assertTrue("Layer sort test: " + lastZindex + " / " + lay.getZindex(), lay.getZindex() > lastZindex);

            lastZindex = lay.getZindex();
        }

        // move layer to first element
        ArrayList<AbstractLayer> list4 = project.getLayersList();
        AbstractLayer toFirst = list4.get(list4.size() - 1);
        list4 = project.moveLayerToIndex(toFirst, 0);
        assertTrue("Layer move test 1: " + list4.indexOf(toFirst), list4.indexOf(toFirst) == 0);

        // move layer to last element
        AbstractLayer toLast = list4.get(0);
        list4 = project.moveLayerToIndex(toLast, list4.size() - 1);
        assertTrue("Layer move test 2: " + list4.indexOf(toLast), list4.indexOf(toLast) == list4.size() - 1);

        // move layer to third element
        AbstractLayer toThird = list4.get(0);
        list4 = project.moveLayerToIndex(toThird, 2);
        assertTrue("Layer move test 3: " + list4.indexOf(toThird), list4.indexOf(toThird) == 2);


    }

}
