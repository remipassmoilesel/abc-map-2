package org.abcmap.core.project.layer;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 *
 */
public class TileLayerTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        ProjectManager pman = MainManager.getProjectManager();
        pman.getProject().addNewTileLayer("Tile layer 1", true, 2);

    }

}
