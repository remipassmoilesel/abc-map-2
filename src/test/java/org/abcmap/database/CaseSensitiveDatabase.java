package org.abcmap.database;

import org.abcmap.TestUtils;
import org.abcmap.core.utils.SQLUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

import static org.junit.Assert.assertTrue;

/**
 * Simple case to demonstrate how H2 handle case
 *
 * Table names are case sensitive, column names are not
 */
public class CaseSensitiveDatabase {

    private static final String TEST_TABLE_NAME = "name_in_lower_case";
    private static final String COLUMN_A = "columnA";
    private static final String COLUMN_B = "columnB";
    private Path databasePath;


    public Connection getConnection() throws SQLException {
        //return DriverManager.getConnection("jdbc:h2:./" + databasePath, "", "");
        return DriverManager.getConnection("jdbc:h2:./" + databasePath, "", "");
    }

    @Test
    public void test() throws Exception {


        Path rootDir = TestUtils.PLAYGROUND_DIRECTORY.resolve("caseSensitiveDatabaseTest");
        FileUtils.deleteDirectory(rootDir.toFile());
        Files.createDirectories(rootDir);

        databasePath = rootDir.resolve("database.h2");

        // declaring without quotes
        SQLUtils.processTransaction(getConnection(), (conn) -> {

            PreparedStatement createStat = conn.prepareStatement("CREATE TABLE " + TEST_TABLE_NAME + " (" + COLUMN_A + " TEXT NOT NULL, " + COLUMN_B + " TEXT NOT NULL);");
            createStat.execute();

            for (int i = 0; i < 100; i++) {

                PreparedStatement stat = conn.prepareStatement("INSERT INTO " + TEST_TABLE_NAME + " (" + COLUMN_A + ", " + COLUMN_B + ") VALUES(?,?);");
                stat.setString(1, "fakeValue_" + System.nanoTime());
                stat.setString(2, "fakeValue_" + System.nanoTime());

                stat.execute();

            }

            return null;
        });

        // declaring WITHOUT quotes
        SQLUtils.processTransaction(getConnection(), (conn) -> {


            PreparedStatement select = conn.prepareStatement("SELECT * FROM " + TEST_TABLE_NAME + ";");
            ResultSet res = select.executeQuery();
            ResultSetMetaData rsmt = res.getMetaData();

            assertTrue("Table name equality", rsmt.getTableName(1).equals(TEST_TABLE_NAME) == false);
            assertTrue("Column name equality", rsmt.getColumnName(1).equals(COLUMN_A) == false);

            return null;
        });


        // declaring WITH quotes
        SQLUtils.processTransaction(getConnection(), (conn) -> {

            PreparedStatement createStat = conn.prepareStatement("CREATE TABLE \"" + TEST_TABLE_NAME + "\" (\"" + COLUMN_A + "\" TEXT NOT NULL, \"" + COLUMN_B + "\" TEXT NOT NULL);");
            createStat.execute();

            for (int i = 0; i < 100; i++) {

//                PreparedStatement stat = conn.prepareStatement("INSERT INTO \"" + TEST_TABLE_NAME + "\" (\"" + COLUMN_A + "\", \"" + COLUMN_B + "\") VALUES(?,?);");
                PreparedStatement stat = conn.prepareStatement("INSERT INTO " + TEST_TABLE_NAME + " (" + COLUMN_A + ", " + COLUMN_B + ") VALUES(?,?);");
                stat.setString(1, "fakeValue_" + System.nanoTime());
                stat.setString(2, "fakeValue_" + System.nanoTime());

                stat.execute();

            }

            return null;
        });

        SQLUtils.processTransaction(getConnection(), (conn) -> {

            PreparedStatement select = conn.prepareStatement("SELECT * FROM \"" + TEST_TABLE_NAME + "\";");
            ResultSet res = select.executeQuery();
            ResultSetMetaData rsmt = res.getMetaData();

            assertTrue("Table name equality", rsmt.getTableName(1).equals(TEST_TABLE_NAME));
            assertTrue("Column name equality", rsmt.getColumnName(1).equals(COLUMN_A));

            return null;
        });




    }



}
