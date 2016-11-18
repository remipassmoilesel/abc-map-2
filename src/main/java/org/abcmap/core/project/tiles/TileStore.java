package org.abcmap.core.project.tiles;

import com.vividsolutions.jts.geom.Coordinate;
import org.abcmap.core.configuration.ConfigurationConstants;
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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Store tiles in a database.
 * <p>
 * TileStore can store several named coverage.
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

        if(bimg == null){
            throw new IllegalStateException("Unsupported image format");
        }

        if (width == null || height == null) {
            width = bimg.getWidth();
            height = bimg.getHeight();
        }

        String tileId = generateTileId();

        try (Connection conn = getDatabaseConnection()) {

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
            spatialStat.setDouble(4, width);
            spatialStat.setDouble(5, height);
            spatialStat.execute();

        } catch (SQLException e) {
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

        // create the master table where are stored references to coverages
        try (Connection conn = getDatabaseConnection()) {

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

        } catch (SQLException e) {
            throw new IOException("Error while creating master table: ", e);
        }
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
        String spatialTableName = SPATIAL_TABLE_PREFIX + coverageName;
        String dataTableName = DATA_TABLE_PREFIX + coverageName;

        try (Connection conn = getDatabaseConnection()) {

            // insert a tuple in master table
            String req = "INSERT INTO " + MASTER_TABLE_NAME + " ("
                    + COVERAGE_NAME_FIELD_NAME + ", "
                    + TILE_TABLE_NAME_FIELD_NAME + ", "
                    + SPATIAL_TABLE_NAME_FIELD_NAME + ") "
                    + "VALUES (?,?,?);";

            //System.out.println(req);

            // insert a tuple in master table
            PreparedStatement masterEntryStat = conn.prepareStatement(req);

            masterEntryStat.setString(1, coverageName);
            masterEntryStat.setString(2, spatialTableName);
            masterEntryStat.setString(3, dataTableName);

            masterEntryStat.execute();

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

            // create tile data table
            req = "CREATE TABLE " + dataTableName + " ("
                    + TILE_ID_FIELD_NAME + " VARCHAR(128), "
                    + TILE_DATA_FIELD_NAME + " BLOB, "
                    + "CONSTRAINT " + spatialTableName + "_pk PRIMARY KEY(" + TILE_ID_FIELD_NAME + "));";

            //System.out.println(req);

            PreparedStatement dataStat = conn.prepareStatement(req);
            dataStat.execute();

            // create an index on tiles
            req = "CREATE INDEX " + spatialTableName + "_index ON " + spatialTableName + "(" + MIN_X_FIELD_NAME + ", " + MIN_Y_FIELD_NAME + ");";
            PreparedStatement indexStat = conn.prepareStatement(req);

            //System.out.println(req);

            indexStat.execute();

        } catch (SQLException e) {
            throw new IOException("Error while creating master table: ", e);
        }

        // store table names
        TileCoverageEntry entry = new TileCoverageEntry(coverageName, spatialTableName, dataTableName);
        coverages.put(coverageName, entry);

        return entry;
    }


}
