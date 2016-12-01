package org.abcmap.core.tiles;

import org.abcmap.core.configuration.ConfigurationConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Store all queries needed by tile storage
 */
public class TileStorageQueries {

    public static final String MASTER_TABLE_NAME = ConfigurationConstants.SQL_TABLE_PREFIX + "TILES_MASTER_TABLE";
    public static final String SPATIAL_TABLE_PREFIX = ConfigurationConstants.SQL_TABLE_PREFIX + "TILE_SPATIAL_";
    public static final String DATA_TABLE_PREFIX = ConfigurationConstants.SQL_TABLE_PREFIX + "TILE_DATA_";

    public static final String COVERAGE_NAME_FIELD_NAME = "COVERAGE_NAME";
    public static final String SPATIAL_TABLE_NAME_FIELD_NAME = "SPATIAL_TABLE_NAME";
    public static final String TILE_TABLE_NAME_FIELD_NAME = "TILE_TABLE_NAME";
    public static final String RES_X_FIELD_NAME = "RES_X";
    public static final String RES_Y_FIELD_NAME = "RES_Y";
    public static final String MIN_X_FIELD_NAME = "MIN_X";
    public static final String MIN_Y_FIELD_NAME = "MIN_Y";
    public static final String MAX_X_FIELD_NAME = "MAX_X";
    public static final String MAX_Y_FIELD_NAME = "MAX_Y";
    public static final String TILE_ID_FIELD_NAME = "TILE_ID";
    public static final String TILE_DATA_FIELD_NAME = "TILE_DATA";


    /**
     * Create the master table where are stored references to coverages
     *
     * @param conn
     * @throws SQLException
     */
    public static PreparedStatement createMasterTable(Connection conn) throws SQLException {

        String req = "CREATE TABLE IF NOT EXISTS " + MASTER_TABLE_NAME + " ("
                + COVERAGE_NAME_FIELD_NAME + " VARCHAR(128) NOT NULL, "
                + SPATIAL_TABLE_NAME_FIELD_NAME + " VARCHAR(128)  NOT NULL, "
                + TILE_TABLE_NAME_FIELD_NAME + " VARCHAR(128)  NOT NULL, "
                + RES_X_FIELD_NAME + " DOUBLE, "
                + RES_Y_FIELD_NAME + " DOUBLE, "
                + MIN_X_FIELD_NAME + " DOUBLE, "
                + MIN_Y_FIELD_NAME + " DOUBLE, "
                + MAX_X_FIELD_NAME + " DOUBLE, "
                + MAX_Y_FIELD_NAME + " DOUBLE, "
                + "CONSTRAINT " + MASTER_TABLE_NAME + "_pk PRIMARY KEY("
                + COVERAGE_NAME_FIELD_NAME + ", "
                + SPATIAL_TABLE_NAME_FIELD_NAME + ", "
                + TILE_TABLE_NAME_FIELD_NAME + "));";

        //System.out.println(req);

        return conn.prepareStatement(req);
    }

    /**
     * Insert a tuple in a tile data table
     *
     * @param conn
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static PreparedStatement insertIntoDataTable(Connection conn, String tableName) throws SQLException {

        String req = "INSERT INTO " + tableName + " ( "
                + " " + TILE_ID_FIELD_NAME + ", "
                + " " + TILE_DATA_FIELD_NAME + ") "
                + " VALUES  (?,?);";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    /**
     * Insert a tuple in a spatial data table
     *
     * @param conn
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static PreparedStatement insertIntoSpatialTable(Connection conn, String tableName) throws SQLException {

        // insert tuple in spatial table
        String req = "INSERT INTO " + tableName + " ("
                + TILE_ID_FIELD_NAME + ", "
                + MIN_X_FIELD_NAME + ", "
                + MIN_Y_FIELD_NAME + ", "
                + MAX_X_FIELD_NAME + ", "
                + MAX_Y_FIELD_NAME + ") "
                + " VALUES  (?,?,?,?,?);";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    /**
     * Insert a tuple in master table
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public static PreparedStatement insertIntoMasterTable(Connection conn) throws SQLException {

        // insert a tuple in master table
        String req = "INSERT INTO " + MASTER_TABLE_NAME + " ("
                + COVERAGE_NAME_FIELD_NAME + ", "
                + TILE_TABLE_NAME_FIELD_NAME + ", "
                + SPATIAL_TABLE_NAME_FIELD_NAME + ") "
                + "VALUES (?,?,?);";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    /**
     * Create a spatial table, where are stored tiles position and dimensions
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public static PreparedStatement createSpatialTable(Connection conn, String tableName) throws SQLException {

        // create spatial table
        String req = "CREATE TABLE " + tableName + " ("
                + TILE_ID_FIELD_NAME + " VARCHAR(128), "
                + MIN_X_FIELD_NAME + " DOUBLE NOT NULL, "
                + MIN_Y_FIELD_NAME + " DOUBLE NOT NULL, "
                + MAX_X_FIELD_NAME + " DOUBLE NOT NULL, "
                + MAX_Y_FIELD_NAME + " DOUBLE NOT NULL, "
                + "CONSTRAINT " + tableName + "_pk PRIMARY KEY(" + TILE_ID_FIELD_NAME + "));";

        //System.out.println(req);

        return conn.prepareStatement(req);
    }

    /**
     * Create a data table, where are stored tile images
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public static PreparedStatement createDataTable(Connection conn, String tableName) throws SQLException {

        // create tile data table
        String req = "CREATE TABLE " + tableName + " ("
                + TILE_ID_FIELD_NAME + " VARCHAR(128), "
                + TILE_DATA_FIELD_NAME + " BLOB, "
                + "CONSTRAINT " + tableName + "_pk PRIMARY KEY(" + TILE_ID_FIELD_NAME + "));";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    /**
     * Create indexes needed by tile storage
     *
     * @param conn
     * @return
     * @throws SQLException
     */
    public static PreparedStatement createIndexes(Connection conn, String spatialTableName) throws SQLException {

        // create an index on tiles
        String req = "CREATE INDEX " + spatialTableName + "_index ON " + spatialTableName + " " +
                "(" + MIN_X_FIELD_NAME + ", " + MIN_Y_FIELD_NAME + ");";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    public static PreparedStatement deleteFromSpatialTable(Connection conn, String tableName) throws SQLException {

        String req = "DELETE FROM " + tableName + " WHERE " + TILE_ID_FIELD_NAME + "=?;";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    public static PreparedStatement deleteFromDataTable(Connection conn, String tableName) throws SQLException {

        String req = "DELETE FROM " + tableName + " WHERE " + TILE_ID_FIELD_NAME + "=?;";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }

    public static PreparedStatement updateTilePosition(Connection conn, String tableName) throws SQLException {

        String req = "UPDATE " + tableName + " SET " + MIN_X_FIELD_NAME + "=?, " + MIN_Y_FIELD_NAME + "=? " +
                "WHERE " + TILE_ID_FIELD_NAME + "=?;";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }


    public static PreparedStatement selectLastTiles(Connection conn, String dataTn, String spaTn) throws SQLException {

        String dtid = dataTn + "." + TILE_ID_FIELD_NAME;
        String spid = spaTn + "." + TILE_ID_FIELD_NAME;
        String dtdt = dataTn + "." + TILE_DATA_FIELD_NAME;
        String spminx = spaTn + "." + MIN_X_FIELD_NAME;
        String spminy = spaTn + "." + MIN_Y_FIELD_NAME;

        String req = "SELECT " +
                dtid + ", " +
                dtdt + ", " +
                spminx + ", " +
                spminy + " " +
                " FROM " + dataTn + "," + spaTn +
                " WHERE " + dtid + " = " + spid +
                " ORDER BY " + dtid + " DESC LIMIT ?,?;";

        //System.out.println(req);

        return conn.prepareStatement(req);
    }


    public static PreparedStatement selectByTileId(Connection conn, String tileId, String dataTn, String spaTn) throws SQLException {

        String dtid = dataTn + "." + TILE_ID_FIELD_NAME;
        String spid = spaTn + "." + TILE_ID_FIELD_NAME;
        String dtdt = dataTn + "." + TILE_DATA_FIELD_NAME;
        String spminx = spaTn + "." + MIN_X_FIELD_NAME;
        String spminy = spaTn + "." + MIN_Y_FIELD_NAME;

        String req = "SELECT " +
                dtid + ", " +
                dtdt + ", " +
                spminx + ", " +
                spminy + " " +
                " FROM " + dataTn + "," + spaTn +
                " WHERE " + dtid + " = " + spid +
                " AND " + dtid + " = ? " +
                " ORDER BY " + dtid + " DESC LIMIT ?,?;";

        //System.out.println(req);

        return conn.prepareStatement(req);
    }

    public static PreparedStatement countTileData(Connection conn, String dataTn) throws SQLException {

        String req = "SELECT count(*) FROM " + dataTn + ";";

        //System.out.println(req);

        return conn.prepareStatement(req);
    }

    /**
     * Return the spatial table name associated with coverage
     *
     * @param coverageName
     * @return
     */
    public static String generateSpatialTableName(String coverageName) {
        return (SPATIAL_TABLE_PREFIX + coverageName).toUpperCase();
    }

    /**
     * Return the data table name associated with coverage
     *
     * @param coverageName
     * @return
     */
    public static String generateDataTableName(String coverageName) {
        return (DATA_TABLE_PREFIX + coverageName).toUpperCase();
    }


    public static PreparedStatement selectAllSpatialEntries(Connection conn, String spatialTableName) throws SQLException {

        String req = "SELECT "
                + MIN_X_FIELD_NAME + ", "
                + MIN_Y_FIELD_NAME + ", "
                + MAX_X_FIELD_NAME + ", "
                + MAX_Y_FIELD_NAME + " "
                + " FROM " + spatialTableName + ";";

        //System.out.println(req);

        return conn.prepareStatement(req);

    }
}
