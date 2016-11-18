package org.abcmap.core.utils;

import org.geotools.data.DataStoreFinder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.sql.SqlUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by remipassmoilesel on 08/11/16.
 */
public class SQLiteUtils {

    /**
     * Get informations about tables
     *
     * @param connection
     */
    public static void showTableInformations(Connection connection) {

        String request = "SELECT * FROM sqlite_master;";

        try {

            Statement stmt = connection.createStatement();

            ResultSet results = stmt.executeQuery(request);
            ResultSetMetaData resultsMtdt = results.getMetaData();

            int j = 0;
            while (results.next()) {
                System.out.println("## " + j);
                for (int i = 1; i <= resultsMtdt.getColumnCount(); i++) {
                    Object obj = results.getObject(i);
                    System.out.println("    " + i + ": " + resultsMtdt.getColumnName(i) + "\t:\t " + obj);
                }

                j++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Return the list of tables
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    public static ArrayList<String> getTableList(Connection connection) throws SQLException {

        String request = "SELECT * FROM sqlite_master;";

        Statement stmt = connection.createStatement();

        ResultSet results = stmt.executeQuery(request);
        ResultSetMetaData resultsMtdt = results.getMetaData();

        ArrayList<String> tables = new ArrayList<>();

        while (results.next()) {

            Object obj = results.getObject(1);
            if (obj != null && obj instanceof String) {
                String type = obj.toString();
                if (type.indexOf("table") != -1) {
                    tables.add(results.getObject(2).toString());
                }
            }
        }

        return tables;
    }

    /**
     * Run a SQL script available in classpath
     *
     * @param name
     * @param connection
     * @throws SQLException
     */
    public static void runScript(String name, Connection connection) throws SQLException {
        InputStream script = SqlUtil.class.getResourceAsStream(name);
        if (script == null) {
            throw new IllegalArgumentException("Unable to find resource: " + name);
        }
        SqlUtil.runScript(script, connection);
    }

    /**
     * Return a JDBC datastore from a geopackage
     *
     * @param geopackage
     * @return
     * @throws IOException
     */
    public static JDBCDataStore getDatastoreFromGeopackage(Path geopackage) throws IOException {

        Map<String, String> params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", geopackage.toString());

        return (JDBCDataStore) DataStoreFinder.getDataStore(params);
    }


}
