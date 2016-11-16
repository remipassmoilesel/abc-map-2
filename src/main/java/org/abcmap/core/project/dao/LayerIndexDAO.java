package org.abcmap.core.project.dao;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import org.abcmap.core.log.CustomLogger;
import org.abcmap.core.managers.LogManager;
import org.abcmap.core.project.layer.LayerIndexEntry;
import org.abcmap.core.project.layer.LayerType;
import org.abcmap.core.utils.SqliteUtils;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * This object allow to read and write layer index from project
 */
public class LayerIndexDAO extends AbstractOrmDAO {
    public LayerIndexDAO(Path dbPath) throws DAOException {
        super(dbPath, LayerIndexEntry.class);
    }

    public ArrayList<LayerIndexEntry> readAllEntries() {
        ArrayList<LayerIndexEntry> entries = new ArrayList<>();
        visit((Object o) -> {
            entries.add((LayerIndexEntry) o);
            return true;
        });

        return entries;
    }

    public void writeAllEntries(ArrayList<LayerIndexEntry> entries) throws DAOException {

        deleteAll();

        for (LayerIndexEntry entry : entries) {
            try {
                dao.create(entry);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

    }

}
