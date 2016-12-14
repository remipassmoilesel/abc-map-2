package org.abcmap.core.dao;

import org.abcmap.core.project.layouts.LayoutSheet;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by remipassmoilesel on 14/12/16.
 */
public class LayoutDAO extends AbstractOrmDAO {

    public LayoutDAO(Path dbPath) throws IOException {
        super(dbPath, LayoutSheet.class);
    }

    public ArrayList<LayoutSheet> readAll() {

        ArrayList<LayoutSheet> list = new ArrayList<>();

        visit((Object o) -> {
            LayoutSheet lay = (LayoutSheet) o;
            list.add(lay);
            return true;
        });

        return list;
    }

    public void writeLayouts(ArrayList<LayoutSheet> layouts) throws IOException {

        // remove old layouts
        deleteAll();

        for (LayoutSheet lay : layouts) {
            try {
                dao.create(lay);
            } catch (SQLException e) {
                throw new IOException("Error while creating layout: " + lay, e);
            }
        }

    }

}
