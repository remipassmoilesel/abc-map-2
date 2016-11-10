package org.abcmap.core.project.dao;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.LayerIndexEntry;
import org.abcmap.core.project.LayerType;
import org.abcmap.core.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


/**
 * This object allow to read and write layer index from project
 */
public class LayerIndexDAO extends AbstractDAO {

    private static final CustomLogger logger = LogManager.getLogger(LayerIndexDAO.class);
    private static final String TABLE_NAME = "abcmap_layer_index";

    public LayerIndexDAO(Connection connection) throws DAOException {
        super(connection);

        // store connection
        this.connection = connection;

        try {

            // try to find the properties table, or create it
            if (SqliteUtils.getTableList(connection).contains(TABLE_NAME) == false) {

                // create table
                SqliteUtils.runScript("/org/abcmap/sql/create_layer_index.sql", connection);

            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }
    }

    /**
     * Read index entries in database and return a list
     *
     * @return
     * @throws DAOException
     */
    public ArrayList<LayerIndexEntry> readLayerIndex() throws DAOException {

        ArrayList<LayerIndexEntry> layers = new ArrayList<>();
        PreparedStatement prepare = null;
        try {

            prepare = connection.prepareStatement("SELECT ly_id, ly_name, ly_visible, ly_zindex, ly_type  FROM " + TABLE_NAME + ";");
            ResultSet rs = prepare.executeQuery();

            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                boolean visible = rs.getBoolean(3);
                int zindex = rs.getInt(4);
                LayerType type = LayerType.safeValueOf(rs.getString(5));

                if(type == null){
                    logger.warning("Unknown type of layer: " + rs.getString(5));
                    continue;
                }

                layers.add(new LayerIndexEntry(id, name, visible, zindex, type));
            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }

        return layers;
    }


    public void writeLayerIndex(List<LayerIndexEntry> list) throws DAOException {

        PreparedStatement prepare = null;
        try {

            // delete all previous entries
            prepare = connection.prepareStatement("DELETE FROM " + TABLE_NAME + ";");
            prepare.execute();

            // insert index entries
            for(LayerIndexEntry entry : list) {

                prepare = connection.prepareStatement("INSERT INTO " + TABLE_NAME +
                        " (ly_id, ly_name, ly_visible, ly_zindex, ly_type) " +
                        "VALUES (?, ?, ?, ?, ?);");

                prepare.setString(1, entry.getLayerId());
                prepare.setString(2, entry.getName());
                prepare.setBoolean(3, entry.isVisible());
                prepare.setInt(4, entry.getZindex());
                prepare.setString(5, entry.getType().toString());

                prepare.executeUpdate();
            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }

    }
}
