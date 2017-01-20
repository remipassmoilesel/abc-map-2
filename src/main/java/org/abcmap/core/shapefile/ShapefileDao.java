package org.abcmap.core.shapefile;

import org.abcmap.core.dao.AbstractOrmDAO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by remipassmoilesel on 24/11/16.
 */
public class ShapefileDao extends AbstractOrmDAO {
    public ShapefileDao(Path dbPath) throws IOException {
        super(dbPath, ShapefileLayerEntry.class);
    }
}
