package org.abcmap.core.utils;

import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.geotools.data.DataStoreFinder;
import org.geotools.jdbc.JDBCDataStore;
import org.geotools.sql.SqlUtil;
import org.h2.jdbcx.JdbcConnectionPool;

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
public class SQLUtils {

    private static final CustomLogger logger = LogManager.getLogger(SQLUtils.class);
    private static HashMap<String, JdbcConnectionPool> currentH2Pools;

    /**
     * Get informations about tables
     * <p>
     * /!\ Do not close connection after
     *
     * @param connection
     */
    public static void showSqliteTableInformations(Connection connection) throws SQLException {

        String request = "SELECT * FROM sqlite_master;";

        try (Statement stmt = connection.createStatement()) {

            try (ResultSet results = stmt.executeQuery(request)) {
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
            }
        }

    }

    /**
     * Return the list of tables
     * <p>
     * /!\ Do not close connection after
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    public static ArrayList<String> getSqliteTableList(Connection connection) throws SQLException {

        String request = "SELECT * FROM sqlite_master;";

        try (Statement stmt = connection.createStatement()) {

            ResultSet results = stmt.executeQuery(request);
            //ResultSetMetaData resultsMtdt = results.getMetaData();

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

    }

    /**
     * Run a SQL script available in classpath
     * <p>
     * /!\ Do not close connection after
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
    public static JDBCDataStore getGeotoolsDatastoreFromGeopackage(Path geopackage) throws IOException {

        Map<String, String> params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", geopackage.toString());

        return (JDBCDataStore) DataStoreFinder.getDataStore(params);
    }

    /**
     * Return a JDBC datastore from a h2 embedded database
     *
     * @param database
     * @return
     * @throws IOException
     */
    public static JDBCDataStore getGeotoolsDatastoreFromH2(Path database) throws IOException {

        Map<String, String> params = new HashMap();
        params.put("dbtype", "h2");
        params.put("database", database.toAbsolutePath().toString());

        return (JDBCDataStore) DataStoreFinder.getDataStore(params);
    }

    /**
     * Process a transaction and close connection after if specified.
     * <p>
     * Allow to reduce the code length
     *
     * @param conn
     * @param processor
     * @throws SQLException
     */
    public static Object processTransaction(Connection conn, SQLProcessor processor) throws Exception {
        return processTransaction(conn, processor, true);
    }

    /**
     * Process a transaction and close connection after if specified.
     * <p>
     * Allow to reduce the code length
     *
     * @param conn
     * @param processor
     * @throws SQLException
     */
    public static Object processTransaction(Connection conn, SQLProcessor processor, boolean closeConnectionAfter) throws Exception {

        try {

            // beginnning transaction
            conn.setAutoCommit(false);

            Object result = processor.process(conn);

            // stp transaction
            conn.commit();

            return result;

        } catch (Exception e) {

            // error, cancel transaction
            try {
                conn.rollback();
            } catch (Exception e1) {
                throw new SQLException("Error while performing transaction: ", e1);
            }

            throw new SQLException("Error while performing transaction: ", e);

        } finally {

            // if necessary, close connection
            if (closeConnectionAfter == true) {
                if (conn != null) {
                    conn.close();
                }
            }

        }

    }

    /**
     * Return list of available tables in database.
     * <p>
     * /!\ Table names are in lower case
     *
     * @param connection
     * @return
     * @throws SQLException
     */
    public static ArrayList<String> getH2TableList(Connection connection) throws SQLException {

        String request = "SHOW TABLES;";

        Statement stmt = connection.createStatement();
        ResultSet results = stmt.executeQuery(request);

        ArrayList<String> tables = new ArrayList<>();

        while (results.next()) {
            tables.add(results.getString(1));
        }

        results.close();

        return tables;
    }

    /**
     * Return a H2 connection from an internal pool
     *
     * @param databasePath
     * @return
     * @throws SQLException
     */
    public static Connection getH2Connection(Path databasePath) throws SQLException {

        if (currentH2Pools == null) {
            currentH2Pools = new HashMap<>();
        }

        String key = databasePath.toAbsolutePath().toString();
        if (currentH2Pools.get(key) == null) {
            currentH2Pools.put(key, JdbcConnectionPool.create(getJdbcUrlForH2(databasePath), "", ""));
        }

        return currentH2Pools.get(key).getConnection();
    }

    /**
     * Shutdown a H2 database
     *
     * @param databasePath
     * @throws SQLException
     */
    public static void shutdownH2Database(Path databasePath) throws SQLException {
        try (Connection conn = getH2Connection(databasePath);
             PreparedStatement stat = conn.prepareStatement("SHUTDOWN")) {
            stat.execute();
        }
    }

    /**
     * /!\ Do not work with all versions of H2 database
     *
     * @param conn
     * @throws SQLException
     */
    public static void printH2DatabaseVersion(Connection conn) throws SQLException {

        try (PreparedStatement stat = conn.prepareStatement("SELECT H2VERSION() FROM DUAL")) {
            ResultSet res = stat.executeQuery();

            System.out.println();
            System.out.println("H2 Database version: ");
            while (res.next()) {
                System.out.println(res.getObject(1));
            }
        }
    }

    /**
     * Return a ORM lite connection pool
     *
     * @param database
     * @return
     * @throws SQLException
     */
    public static JdbcPooledConnectionSource getH2OrmliteConnectionPool(Path database) throws SQLException {

        String databaseUrl = getJdbcUrlForH2(database);

        JdbcPooledConnectionSource connectionSource = new JdbcPooledConnectionSource(databaseUrl);
        connectionSource.setMaxConnectionAgeMillis(Long.MAX_VALUE);
        connectionSource.setCheckConnectionsEveryMillis(-1);
        connectionSource.setTestBeforeGet(false);

        connectionSource.initialize();

        return connectionSource;
    }

    public static String getJdbcUrlForH2(Path databasePath) {
        // TODO: close database on VM exit ?
        // return "jdbc:h2:file:" + databasePath.toAbsolutePath().toString() + ";DB_CLOSE_ON_EXIT=FALSE";
        return "jdbc:h2:file:" + databasePath.toAbsolutePath().toString();
    }
}
