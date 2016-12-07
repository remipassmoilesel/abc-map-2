package org.abcmap.core.partials;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.TableUtils;
import org.geotools.geometry.jts.ReferencedEnvelope;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Store partials in RAM and in database
 * <p>
 * Partials should contains only soft links to images, in order to free memory when needed
 */
public class RenderedPartialStore {

    /**
     * Precision used when search existing partials by area in database
     */
    private static final Double PRECISION = 0.0001d;

    /**
     * List of partials already used. They can be complete (with an image loaded) or not.
     */
    private ArrayList<RenderedPartial> loadedPartials;

    private final Dao<SerializableRenderedPartial, ?> dao;
    private final Path databasePath;
    private final JdbcPooledConnectionSource connectionSource;
    private static long addedInDatabase = 0;

    public RenderedPartialStore(Path databasePath) throws SQLException {

        this.loadedPartials = new ArrayList<>();
        this.databasePath = databasePath;

        this.connectionSource = new JdbcPooledConnectionSource("jdbc:h2:./" + databasePath, "", "");
        connectionSource.setMaxConnectionAgeMillis(5 * 60 * 1000);
        connectionSource.setTestBeforeGet(true);
        connectionSource.initialize();

        // create tables
        TableUtils.createTableIfNotExists(connectionSource, SerializableRenderedPartial.class);

        // create dao object
        this.dao = DaoManager.createDao(connectionSource, SerializableRenderedPartial.class);
    }

    /**
     * Return a corresponding rendered partial
     *
     * @param env
     * @return
     */
    public RenderedPartial searchInLoadedList(ReferencedEnvelope env) {
        for (RenderedPartial part : loadedPartials) {
            if (part.getEnvelope().equals(env) == false) {
                continue;
            }
            return part;
        }
        return null;
    }

    /**
     * Update a partial by adding rendered image. If a valid image is found, return true, if not return false.
     *
     * @param part
     * @return
     * @throws SQLException
     */
    public boolean updatePartialFromDatabase(RenderedPartial part) throws SQLException {

        // check if partial is in database
        ReferencedEnvelope area = part.getEnvelope();
        List<SerializableRenderedPartial> results = dao.queryBuilder().where().raw(
                "ABS(" + SerializableRenderedPartial.PARTIAL_X1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_X2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y1_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND ABS(" + SerializableRenderedPartial.PARTIAL_Y2_FIELD_NAME + " - ?) < " + PRECISION + " "
                        + "AND CRS=?;",

                new SelectArg(SqlType.DOUBLE, area.getMinX()),
                new SelectArg(SqlType.DOUBLE, area.getMaxX()),
                new SelectArg(SqlType.DOUBLE, area.getMinY()),
                new SelectArg(SqlType.DOUBLE, area.getMaxY()),
                new SelectArg(SqlType.STRING, SerializableRenderedPartial.crsToId(area.getCoordinateReferenceSystem()))).query();

        // no results found
        if (results.size() < 1) {
            return false;
        }

        // too much results, show error
        if (results.size() > 1) {
            new SQLException("More than one result found: " + results.size()).printStackTrace();
        }

        // one result found, prepare it and return it
        BufferedImage img = results.get(0).getImage();
        int w = img.getWidth();
        int h = img.getHeight();
        part.setImage(img, w, h);

        // update in memory partial
        loadedPartials.add(part);

        return true;

    }

    /**
     * Add partial only in loaded list (RAM)
     *
     * @param part
     */
    public void addInLoadedList(RenderedPartial part) {
        loadedPartials.add(part);
    }

    /**
     * Add partial in loaded list and in database
     *
     * @param part
     * @throws SQLException
     */
    public void addPartial(RenderedPartial part) throws SQLException {

        if (part.getImage() == null) {
            throw new NullPointerException("Image is null");
        }

        dao.create(new SerializableRenderedPartial(part));

        addInLoadedList(part);

        addedInDatabase++;
    }

    public static long getAddedInDatabase() {
        return addedInDatabase;
    }

    public ArrayList<RenderedPartial> getLoadedPartials() {
        return loadedPartials;
    }
}
