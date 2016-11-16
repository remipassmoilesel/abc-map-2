package org.abcmap.core.project.dao;

import org.abcmap.core.project.ProjectMetadata;
import org.abcmap.core.project.ProjectMetadataPeer;
import org.abcmap.core.styles.StyleContainer;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Read and write styles in database
 */
public class StyleDAO extends AbstractOrmDAO {

    public StyleDAO(Path p) throws DAOException {
        super(p, StyleContainer.class);
    }

    /**
     * Delete all current styles and write the specified list in database.
     *
     * @param list
     * @throws DAOException
     */
    public void writeAll(ArrayList<StyleContainer> list) throws DAOException {

        deleteAll();

        for (StyleContainer ctr : list) {
            try {
                dao.create(ctr);
            } catch (SQLException e) {
                throw new DAOException(e);
            }
        }

    }

    /**
     * Read all styles in database and return a list
     *
     * @return
     */
    public ArrayList<StyleContainer> readStyles() {

        ArrayList<StyleContainer> list = new ArrayList<>();

        visit((Object o) -> {
            StyleContainer p = (StyleContainer) o;
            list.add(p);
            return true;
        });

        return list;
    }
}
