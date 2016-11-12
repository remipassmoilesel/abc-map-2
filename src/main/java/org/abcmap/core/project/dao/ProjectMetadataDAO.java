package org.abcmap.core.project.dao;

import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.PMConstants;
import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.core.utils.SqliteUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;


/**
 * This object allow to read and write metadata from project
 */
public class ProjectMetadataDAO extends AbstractDAO {

    private static final CustomLogger logger = LogManager.getLogger(ProjectMetadataDAO.class);
    private static final String TABLE_NAME = "abcmap_project_metadata";

    public ProjectMetadataDAO(Connection connection) throws DAOException {
        super(connection);

        // store connection
        this.connection = connection;

        try {

            // try to find the properties table, or create it
            if (SqliteUtils.getTableList(connection).contains(TABLE_NAME) == false) {

                // create table
                SqliteUtils.runScript("/org/abcmap/sql/create_project_metadata.sql", connection);

                // add default values
                writeMetadata(new ProjectMetadata());
            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }
    }

    /**
     * Read metadata in database and return a metadata container
     * @return
     * @throws DAOException
     */
    public ProjectMetadata readMetadata() throws DAOException {

        ProjectMetadata data = new ProjectMetadata();
        try {

            PreparedStatement prepare = connection.prepareStatement("SELECT md_key, md_value FROM " + TABLE_NAME + ";");
            ResultSet rs = prepare.executeQuery();

            while (rs.next()) {

                PMConstants key = PMConstants.safeValueOf(rs.getObject(1).toString());
                String value = rs.getObject(2).toString();

                if (key != null) {
                    data.updateValue(key, value);
                } else {
                    logger.warning("Unknown metadata name: " + rs.getObject(1).toString() + " / " + rs.getObject(2).toString());
                }
            }

        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }

        return data;
    }

    /**
     * Delete all metadata stored in database and write the specified container.
     */
    public void writeMetadata(ProjectMetadata pm) throws DAOException {

        try {

            // delete all previous metadata
            PreparedStatement prepare = connection.prepareStatement("DELETE FROM " + TABLE_NAME + ";");
            prepare.execute();

            // insert metadata
            HashMap<PMConstants, String> mts = pm.getMetadata();
            Iterator<PMConstants> it = mts.keySet().iterator();
            while(it.hasNext()){

                PMConstants key = it.next();
                String value = mts.get(key);

                prepare = connection.prepareStatement(
                        "INSERT INTO  " + TABLE_NAME + " (md_key, md_value) " +
                                "VALUES (?, ?);");
                prepare.setString(1, key.toString());
                prepare.setString(2, value);

                prepare.executeUpdate();

            }


        } catch (Exception e) {
            throw new DAOException("Error while accessing database", e);
        }

    }
}
