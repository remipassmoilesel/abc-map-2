package org.abcmap.tests.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.managers.TempFilesManager;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.Assert.assertTrue;

public class TempFilesManagerTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.mainManagerInit();
    }

    @Test
    public void tests() throws IOException {

        TempFilesManager tempfiles = MainManager.getTempFilesManager();

        Path file = tempfiles.createTemporaryFile("testfile", null);
        assertTrue("Temporary file creation test", Files.exists(file));

        Path dir = tempfiles.createProjectTempDirectory();
        assertTrue("Project temporary directory creation test", Files.isDirectory(dir));

    }


}
