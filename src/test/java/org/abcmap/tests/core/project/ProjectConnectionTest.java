package org.abcmap.tests.core.project;

import org.abcmap.TestUtils;
import org.abcmap.core.managers.MainManager;
import org.abcmap.core.project.Project;
import org.abcmap.core.utils.SQLUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        String tableName = ("DATA_" + System.nanoTime()).toUpperCase();

        Boolean executed = (Boolean) project.executeWithDatabaseConnection((connection) -> {

            PreparedStatement stat = connection.prepareStatement("CREATE TABLE " + tableName + " (COLUMN_A TEXT, COLUMN_B TEXT);");
            stat.execute();

            for (int i = 0; i < 10; i++) {
                stat = connection.prepareStatement("INSERT INTO " + tableName + " (COLUMN_A, COLUMN_B) VALUES(?,?)");
                stat.setString(1, "aaaa" + i);
                stat.setString(2, "bbbb" + i);
                stat.executeUpdate();
            }

            if (true) {
                throw new Exception("You shall not pass !");
            }

            return true;

        });

        // code above must fail, table will be created but data are not inserted
        assertTrue("Connection test 1", executed == null);

        executed = (Boolean) project.executeWithDatabaseConnection((connection) -> {


            PreparedStatement stat = connection.prepareStatement("SELECT count(*) FROM " + tableName + ";");
            ResultSet rs = stat.executeQuery();
            rs.next();

            // nothing in table normally
            assertTrue("Transaction test", rs.getInt(1) == 0);

            int inserted = 0;
            for (int i = 0; i < 10; i++) {
                stat = connection.prepareStatement("INSERT INTO " + tableName + " (COLUMN_A, COLUMN_B) VALUES(?,?)");
                stat.setString(1, "aaaa" + i);
                stat.setString(2, "bbbb" + i);
                inserted += stat.executeUpdate();
            }

            return inserted == 10;
        });

        assertTrue("Connection test 2", executed == true);

        project.executeWithDatabaseConnection((connection) -> {

            PreparedStatement stat = connection.prepareStatement("SELECT count(*) FROM " + tableName + ";");
            ResultSet res = stat.executeQuery();
            res.next();

            assertTrue("Transaction test", res.getInt(1) == 10);

            return null;
        });

    }


}
