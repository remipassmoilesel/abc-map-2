package org.abcmap.core.project.tiles;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
import org.abcmap.core.utils.SQLUtils;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Transaction;
import org.geotools.jdbc.JDBCDataStore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Store tiles in a database.
 * <p>
 * TileStore can store several named coverage. Why do not use built in Geopackage tile management ? Because
 * we need to set position of tile with eventual overlap.
 */
public class TileStore {

    public static final String MASTER_TABLE_NAME = ConfigurationConstants.SQL_TABLE_PREFIX + "tiles_master_table";
    public static final String SPATIAL_TABLE_PREFIX = ConfigurationConstants.SQL_TABLE_PREFIX + "tile_spatial_";
    public static final String DATA_TABLE_PREFIX = ConfigurationConstants.SQL_TABLE_PREFIX + "tile_data_";

    public static final String COVERAGE_NAME_FIELD_NAME = "coverage_name";
    public static final String SPATIAL_TABLE_NAME_FIELD_NAME = "spatial_table_name";
    public static final String TILE_TABLE_NAME_FIELD_NAME = "tile_table_name";
    public static final String RES_X_FIELD_NAME = "res_x";
    public static final String RES_Y_FIELD_NAME = "res_y";
    public static final String MIN_X_FIELD_NAME = "min_x";
    public static final String MIN_Y_FIELD_NAME = "min_y";
    public static final String MAX_X_FIELD_NAME = "max_x";
    public static final String MAX_Y_FIELD_NAME = "max_y";
    public static final String TILE_ID_FIELD_NAME = "tile_id";
    public static final String TILE_DATA_FIELD_NAME = "tile_data";

    /**
     * Path of database file associated with tile store
     */
    private final Path databasePath;

    /**
     * List of available coverages in store
     */
    private final HashMap<String, TileCoverageEntry> coverages;

    /**
     * Create a new tile store
     *
     * @param p
     * @throws IOException
     */
    public TileStore(Path p) throws IOException {
        this.databasePath = p;
        this.coverages = new HashMap<>();
    }

    /**
     * Add a tile to specified coverage
     *
     * @param coverageName
     * @param position
     * @param img
     * @throws IOException
     */
    public String addTile(String coverageName, Coordinate position, Path img) throws IOException {
        return addTile(coverageName, img, position, null, null);
    }

    /**
     * Add a tile to specified coverage
     *
     * @param coverageName
     * @param imagePath
     * @param position
     * @param width
     * @param height
     * @throws IOException
     */
    public String addTile(String coverageName, Path imagePath, Coordinate position, Integer width, Integer height) throws IOException {

        // retrieve informations about coverage
        TileCoverageEntry coverageEntry = coverages.get(coverageName);
        if (coverageEntry == null) {
            throw new IllegalArgumentException("Invalid coverage name");
        }

        // get width and height if necessary
        BufferedImage bimg = ImageIO.read(imagePath.toFile());

        if (bimg == null) {
            throw new IllegalStateException("Unsupported image format");
        }

        if (width == null || height == null) {
            width = bimg.getWidth();
            height = bimg.getHeight();
        }

        String tileId = generateTileId();

        Integer finalWidth = width;
        Integer finalHeight = height;

        try {
            SQLUtils.processTransaction(getDatabaseConnection(), (conn) -> {

                // insert image in data table
                String req = "INSERT INTO " + coverageEntry.getDataTableName() + " ( "
                        + " " + TILE_ID_FIELD_NAME + ", "
                        + " " + TILE_DATA_FIELD_NAME + ") "
                        + " VALUES  (?,?);";

                //System.out.println(req);

                PreparedStatement imageStat = conn.prepareStatement(req);
                imageStat.setString(1, tileId);
                imageStat.setBytes(2, imageToByteArray(bimg));
                imageStat.execute();
                imageStat.close();

                // insert tuple in spatial table
                req = "INSERT INTO " + coverageEntry.getSpatialTableName() + " ("
                        + TILE_ID_FIELD_NAME + ", "
                        + MIN_X_FIELD_NAME + ", "
                        + MIN_Y_FIELD_NAME + ", "
                        + MAX_X_FIELD_NAME + ", "
                        + MAX_Y_FIELD_NAME + ") "
                        + " VALUES  (?,?,?,?,?);";

                //System.out.println(req);

                PreparedStatement spatialStat = conn.prepareStatement(req);
                spatialStat.setString(1, tileId);
                spatialStat.setDouble(2, position.x);
                spatialStat.setDouble(3, position.y);
                spatialStat.setDouble(4, finalWidth);
                spatialStat.setDouble(5, finalHeight);
                spatialStat.execute();
                spatialStat.close();

                return null;
            });
        } catch (Exception e) {
            throw new IOException("Error while adding tile: ", e);
        }

        return tileId;
    }

    /**
     * Read an image and return a byte array
     * <p>
     * Here we read image instead of open an input stream to avoid null pointer exceptions
     * (sometimes services are available for reading, but not for input streams)
     *
     * @param img
     * @return
     * @throws IOException
     */
    private static byte[] imageToByteArray(BufferedImage img) throws IOException {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        }

    }

    /**
     * Generate a unique tile id
     *
     * @return
     */
    private String generateTileId() {
        return "tile_" + System.nanoTime();
    }

    /**
     * Return the database path associated with this storage
     *
     * @return
     */
    public Path getDatabasePath() {
        return databasePath;
    }

    /**
     * Return a connection to the storage
     *
     * @return
     * @throws IOException
     */
    public Connection getDatabaseConnection() throws IOException {

        Map params = new HashMap();
        params.put("dbtype", "geopkg");
        params.put("database", databasePath.toString());

        JDBCDataStore datastore = (JDBCDataStore) DataStoreFinder.getDataStore(params);
        Connection connection = datastore.getConnection(Transaction.AUTO_COMMIT);

        return connection;

    }

    /**
     * Initialize storage, by creating necessary tables, etc ...
     */
    public void initialize() throws IOException {

        try {
            SQLUtils.processTransaction(getDatabaseConnection(), (conn) -> {

                // create the master table where are stored references to coverages
                String req = "CREATE TABLE " + MASTER_TABLE_NAME + " ("
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

                PreparedStatement masterStat = conn.prepareStatement(req);
                masterStat.execute();
                masterStat.close();

                return null;
            });
        } catch (Exception e) {
            throw new IOException("Error while initializing tile: ", e);
        }
    }

    /**
     * Return the spatial table name associated with coverage
     *
     * @param coverageName
     * @return
     */
    public static String getSpatialTableName(String coverageName) {
        return SPATIAL_TABLE_PREFIX + coverageName;
    }

    /**
     * Return the data table name associated with coverage
     *
     * @param coverageName
     * @return
     */
    public static String getDataTableName(String coverageName) {
        return DATA_TABLE_PREFIX + coverageName;
    }

    /**
     * Add a coverage to the storage
     * <p>
     * /!\ Name must be database compliant, and will be trimmed and passed in lower case
     */
    public TileCoverageEntry addCoverage(String coverageName) throws IOException {

        // check name and format it
        coverageName = coverageName.trim().toLowerCase();
        if (coverageName.length() > 50) {
            throw new IllegalArgumentException("Coverage name too long: " + coverageName + " / " + coverageName.length());
        }

        // generate table names
        String spatialTableName = getSpatialTableName(coverageName);
        String dataTableName = getDataTableName(coverageName);

        try {
            String finalCoverageName = coverageName;
            SQLUtils.processTransaction(getDatabaseConnection(), (conn) -> {

                // insert a tuple in master table
                String req = "INSERT INTO " + MASTER_TABLE_NAME + " ("
                        + COVERAGE_NAME_FIELD_NAME + ", "
                        + TILE_TABLE_NAME_FIELD_NAME + ", "
                        + SPATIAL_TABLE_NAME_FIELD_NAME + ") "
                        + "VALUES (?,?,?);";

                //System.out.println(req);

                // insert a tuple in master table
                PreparedStatement masterEntryStat = conn.prepareStatement(req);

                masterEntryStat.setString(1, finalCoverageName);
                masterEntryStat.setString(2, spatialTableName);
                masterEntryStat.setString(3, dataTableName);

                masterEntryStat.execute();
                masterEntryStat.close();

                // create spatial table
                req = "CREATE TABLE " + spatialTableName + " ("
                        + TILE_ID_FIELD_NAME + " VARCHAR(128), "
                        + MIN_X_FIELD_NAME + " DOUBLE NOT NULL, "
                        + MIN_Y_FIELD_NAME + " DOUBLE NOT NULL, "
                        + MAX_X_FIELD_NAME + " DOUBLE NOT NULL, "
                        + MAX_Y_FIELD_NAME + " DOUBLE NOT NULL, "
                        + "CONSTRAINT " + spatialTableName + "_pk PRIMARY KEY(" + TILE_ID_FIELD_NAME + "));";

                //System.out.println(req);

                PreparedStatement spatStat = conn.prepareStatement(req);
                spatStat.execute();
                spatStat.close();

                // create tile data table
                req = "CREATE TABLE " + dataTableName + " ("
                        + TILE_ID_FIELD_NAME + " VARCHAR(128), "
                        + TILE_DATA_FIELD_NAME + " BLOB, "
                        + "CONSTRAINT " + spatialTableName + "_pk PRIMARY KEY(" + TILE_ID_FIELD_NAME + "));";

                //System.out.println(req);

                PreparedStatement dataStat = conn.prepareStatement(req);
                dataStat.execute();
                dataStat.close();

                // create an index on tiles
                req = "CREATE INDEX " + spatialTableName + "_index ON " + spatialTableName + "(" + MIN_X_FIELD_NAME + ", " + MIN_Y_FIELD_NAME + ");";
                PreparedStatement indexStat = conn.prepareStatement(req);
                indexStat.execute();
                indexStat.close();

                return null;
            });
        } catch (Exception e) {
            throw new IOException("Error while adding coverage: ", e);
        }

        // store table names
        TileCoverageEntry entry = new TileCoverageEntry(coverageName, spatialTableName, dataTableName);
        coverages.put(coverageName, entry);

        return entry;
    }

    /**
     * Delete specified tile from specified coverage. Return true if tile has been deleted corectly.
     *
     * @param coverageName
     * @param tileId
     * @return
     * @throws IOException
     */
    public boolean deleteTile(String coverageName, String tileId) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        list.add(tileId);
        return deleteTiles(coverageName, list);
    }

    /**
     * Delete specified tiles
     *
     * @param coverageName
     * @param ids
     * @return
     * @throws IOException
     */
    public boolean deleteTiles(String coverageName, List<String> ids) throws IOException {

        if (coverages.containsKey(coverageName) == false) {
            throw new IllegalArgumentException("Unknown coverage: " + coverageName);
        }

        String req1 = "DELETE FROM " + getDataTableName(coverageName) + " WHERE " + TILE_ID_FIELD_NAME + "=?;";
        String req2 = "DELETE FROM " + getSpatialTableName(coverageName) + " WHERE " + TILE_ID_FIELD_NAME + "=?;";

        try {
            Object result = SQLUtils.processTransaction(getDatabaseConnection(), (conn) -> {

                int totalDeleted = 0;

                for (String tileId : ids) {

                    // delete entry from data table
                    PreparedStatement deleteStat = conn.prepareStatement(req1);
                    deleteStat.setString(1, tileId);

                    int deleted = deleteStat.executeUpdate();
                    deleteStat.close();

                    // delete from spatial table
                    deleteStat = conn.prepareStatement(req2);
                    deleteStat.setString(1, tileId);

                    deleted += deleteStat.executeUpdate();
                    deleteStat.close();

                    if (deleted > 2) {
                        throw new IOException("Error while deleting tile, more than two tuples were deleted: " + deleted);
                    }

                    totalDeleted += deleted;
                }

                // return true if deletion OK
                return totalDeleted == ids.size() * 2;
            });

            return result != null && (boolean) result;

        } catch (Exception e) {
            throw new IOException("Error while deleting tile", e);
        }
    }

    /**
     * Move tile to the specified position
     *
     * @param coverageName
     * @param tileId
     * @param x
     * @param y
     * @return
     * @throws IOException
     */
    public boolean moveTile(String coverageName, String tileId, double x, double y) throws IOException {
        ArrayList<Object[]> toRemove = new ArrayList<Object[]>();
        toRemove.add(new Object[]{tileId, x, y});
        return moveTiles(coverageName, toRemove);
    }

    /**
     * Move tiles to the specified position
     * <p>
     * toRemove[0] -> tileId, toRemove[1] -> Double x, toRemove[2] -> Double y
     *
     * @param coverageName
     * @param toMove
     * @return
     * @throws IOException
     */
    public boolean moveTiles(String coverageName, List<Object[]> toMove) throws IOException {

        if (coverages.containsKey(coverageName) == false) {
            throw new IllegalArgumentException("Unknown coverage: " + coverageName);
        }

        String req1 = "UPDATE " + getSpatialTableName(coverageName) + " SET " + MIN_X_FIELD_NAME + "=?, " + MIN_Y_FIELD_NAME + "=? " +
                "WHERE " + TILE_ID_FIELD_NAME + "=?;";

        try {
            Object result = SQLUtils.processTransaction(getDatabaseConnection(), (conn) -> {

                Iterator<Object[]> it = toMove.iterator();
                int totalUpdated = 0;

                for (Object[] entry : toMove) {

                    String tileId = (String) entry[0];
                    Double x = (Double) entry[1];
                    Double y = (Double) entry[2];

                    // delete entry from data table
                    PreparedStatement updateStat = conn.prepareStatement(req1);
                    updateStat.setDouble(1, x);
                    updateStat.setDouble(2, y);
                    updateStat.setString(3, tileId);

                    int updated = updateStat.executeUpdate();
                    updateStat.close();

                    if (updated > 1) {
                        throw new IOException("Error while moving tile, more than one tuple were updated: " + updated);
                    }

                    totalUpdated += updated;
                }

                // return true if update OK
                return totalUpdated == toMove.size();
            });

            return result != null && (boolean) result;

        } catch (Exception e) {
            throw new IOException("Error while moving tile", e);
        }
    }


}
