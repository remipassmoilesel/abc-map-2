package org.abcmap.tests.core.project;

import org.abcmap.Initialization;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.MapManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 13/12/16.
 */
public class ProjectTest {

    @Test
    public void test() throws IOException, InvocationTargetException, InterruptedException {

        // initialize managers
        Initialization.doInit(null);

        ProjectManager projectm = MainManager.getProjectManager();
        MapManager mapm = MainManager.getMapManager();

        // create a fake project
        projectm.createFakeProject();
        Project project = projectm.getProject();

        // compute maximum bounds
        ReferencedEnvelope expected = new ReferencedEnvelope(-1078.8829231262207, 796.0, -18.264129638671875, 910.7358703613281, project.getCrs());
        ReferencedEnvelope actual = project.getMaximumBounds();

        assertTrue("Maximum bounds computation", expected.equals(actual));

    }

}
