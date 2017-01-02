package org.abcmap.tests.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.ProjectManager;
import org.abcmap.core.project.Project;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

/**
 * Basic test opening, closing, ... with project manager
 */
public class ProjectManagerTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.softwareInit();
    }

    @Test
    public void tests() throws IOException {

        // create temp files
        Path tempDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("projectManagerTest");
        FileUtils.deleteDirectory(tempDir.toFile());
        Files.createDirectories(tempDir);

        Path savedPath = tempDir.resolve("saved.abm");
        ProjectManager pman = MainManager.getProjectManager();

        // create a new project
        boolean created = false;
        Project newproj = null;
        try {
            pman.createNewProject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        newproj = pman.getProject();
        created = Files.exists(Paths.get(newproj.getDatabasePath().toString() + ".data.db"));

        assertTrue("Creation test", created && newproj != null);

        // save the project at another location
        boolean saved = false;
        newproj.setFinalPath(savedPath);
        try {
            pman.saveProject();
            saved =  Files.exists(Paths.get(newproj.getFinalPath().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Save test", saved);

        // close the project
        boolean closed = false;
        try {
            pman.closeProject();
            closed = Files.exists(Paths.get(newproj.getDatabasePath().toString() + ".data.db")) == false;
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
            opened = Files.exists(Paths.get(openedProj.getDatabasePath().toString() + ".data.db"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue("Open test", opened);

    }
}
