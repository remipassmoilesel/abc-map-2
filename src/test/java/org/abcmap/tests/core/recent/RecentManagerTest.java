package org.abcmap.tests.core.recent;

import org.abcmap.TestUtils;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.managers.ManagerTreeAccessUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by remipassmoilesel on 20/01/17.
 */
public class RecentManagerTest extends ManagerTreeAccessUtil {

    private static final CustomLogger logger = LogManager.getLogger(RecentManagerTest.class);

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        // clear previous history
        recentm().clearAllHistory();
        recentm().saveHistory();

        // add project without final path, this should fail
        try {
            recentm().addCurrentProject();
            fail("Project without final path must not be saved as recent project");
        } catch (NullPointerException e) {
            logger.debug(e);
        }

        // add final path to project
        Path firstProjectPath = Paths.get("/path/to/real/project");
        projectm().getProject().setFinalPath(firstProjectPath);

        // add current project to history
        recentm().addCurrentProject();

        // check history
        assertTrue("Recent project history size", recentm().getProjectHistory().size() == 1);

        // add more paths
        int nbrOfPath = 20;
        for (int i = 0; i < nbrOfPath; i++) {
            if (i % 2 == 0) {
                recentm().addProfilePath("/path/to/profile_" + i);
            } else {
                recentm().addProjectPath("/path/to/project_" + i);
            }
        }

        // save history
        recentm().saveHistory();
        assertTrue("History serialization test", Files.isRegularFile(ConfigurationConstants.HISTORY_PATH) == true);

        // clear in memory history and reload it from disk
        recentm().clearProjectHistory();
        recentm().loadHistory();

        assertTrue("Recent project history size test", recentm().getProjectHistory().size() == 11);
        assertTrue("Recent profile history size test", recentm().getProfileHistory().size() == 10);

        // add again first path, history size and element position should be the same
        recentm().addProjectPath(firstProjectPath.toString());
        assertTrue("Recent project history size test 2", recentm().getProjectHistory().size() == 11);
        assertTrue("Recent project history size test 2", recentm().getProjectHistory().indexOf(firstProjectPath.toString()) == 0);

    }

}
