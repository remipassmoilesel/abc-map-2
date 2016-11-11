package org.abcmap.core.project;

import org.abcmap.TestConstants;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertTrue;

/**
 * Basic test opening, closing, ... with project manager
 */
public class ProjectManagerTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        MainManager.init();
    }

    @Test
    public void tests() throws IOException {

        Path tempDir = TestConstants.PLAYGROUND_DIRECTORY.resolve("projectManagerTest");
        Files.createDirectories(tempDir);

        Path savedPath = tempDir.resolve("saved.abm");
        ProjectManager pman = MainManager.getProjectManager();

        // create a new project
        boolean created = false;
        Project newproj = null;
        try {
            pman.createNewProject();
            newproj = pman.getProject();
            created = Files.exists(newproj.getDatabasePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Creation test", created);

        // save the project at another location
        boolean saved = false;
        newproj.setFinalPath(savedPath);
        try {
            pman.saveProject();
            saved = Files.exists(newproj.getFinalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Save test", saved);

        // close the project
        boolean closed = false;
        try {
            pman.closeProjet();
            closed = Files.exists(newproj.getDatabasePath()) == false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Close test", closed);

        // open the project
        boolean opened = false;
        Project openedProj = null;
        try {
            pman.openProject(savedPath);
            openedProj = pman.getProject();
            opened = Files.exists(openedProj.getDatabasePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Open test", opened);

//        // last close, optionnal
//        try {
//            pman.closeProjet();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
