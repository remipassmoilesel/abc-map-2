package org.abcmap.core.project.dao;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.LayerIndexEntry;
import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.gui.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT.value;


/**
 * This object allow to read and write layer index from project
 */
public class LayerIndexDAO extends AbstractDAO {

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

            prepare = connection.prepareStatement("SELECT ly_id, ly_name, ly_visible, ly_zindex  FROM " + TABLE_NAME + ";");
            ResultSet rs = prepare.executeQuery();

            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                boolean visible = rs.getBoolean(3);
                int zindex = rs.getInt(4);

                layers.add(new LayerIndexEntry(id, name, visible, zindex));
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
                        " (ly_id, ly_name, ly_visible, ly_zindex) " +
                        "VALUES (?,?,?,?);");

                prepare.setString(1, entry.getId());
                prepare.setString(2, entry.getName());
                prepare.setBoolean(3, entry.isVisible());
                prepare.setInt(4, entry.getZindex());

                prepare.executeUpdate();
            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }

    }
}
