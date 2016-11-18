package org.abcmap.core.project.dao;

import org.abcmap.core.project.layer.LayerIndexEntry;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;


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
