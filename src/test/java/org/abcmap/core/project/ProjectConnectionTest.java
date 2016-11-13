package org.abcmap.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.utils.SqliteUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

/**
 * Test project connection use
 * <p>
 * This kind of access avoid to have too many unclosed connections
 */
public class ProjectConnectionTest {

    @BeforeClass
    public static void beforeTests() throws IOException {
        TestUtils.createNewProject();
    }

    @Test
    public void tests() throws IOException {

        Project project = MainManager.getProjectManager().getProject();

        boolean executed = (boolean) project.executeWithDatabaseConnection((connection) -> {
            try {
                return SqliteUtils.getTableList(connection).size() > 0;
            } catch (SQLException e) {
                return false;
            }
        });

        assertTrue("Connection test", executed);

    }


}
