package org.abcmap.core.dao;

import org.abcmap.core.project.layouts.Layout;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 14/12/16.
 */
public class LayoutDAO extends AbstractOrmDAO {

    public LayoutDAO(Path dbPath) throws IOException {
        super(dbPath, Layout.class);
    }

    public ArrayList<Layout> readAll() {

        ArrayList<Layout> list = new ArrayList<>();

        visit((Object o) -> {
            Layout lay = (Layout) o;
            list.add(lay);
            return true;
        });

        return list;
    }

    public void writeLayouts(ArrayList<Layout> layouts) throws IOException {

        // remove old layouts
        deleteAll();

        for (Layout lay : layouts) {
            try {
                dao.create(lay);
            } catch (SQLException e) {
                throw new IOException("Error while creating layout: " + lay, e);
            }
        }

    }

}
