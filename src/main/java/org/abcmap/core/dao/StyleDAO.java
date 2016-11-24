package org.abcmap.core.dao;

import org.abcmap.core.styles.StyleContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Read and write styles in database
 */
public class StyleDAO extends AbstractOrmDAO {

    public StyleDAO(Path p) throws IOException {
        super(p, StyleContainer.class);
    }

    /**
     * Delete all current styles and write the specified list in database.
     *
     * @param list
     * @throws IOException
     */
    public void writeAll(ArrayList<StyleContainer> list) throws IOException {

        deleteAll();

        for (StyleContainer ctr : list) {
            try {
                dao.create(ctr);
            } catch (SQLException e) {
                throw new IOException(e);
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
