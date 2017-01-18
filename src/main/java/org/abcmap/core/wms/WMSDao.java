package org.abcmap.core.wms;

import org.abcmap.core.dao.AbstractOrmDAO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by remipassmoilesel on 24/11/16.
 */
public class WMSDao extends AbstractOrmDAO {
    public WMSDao(Path dbPath) throws IOException {
        super(dbPath, WmsLayerEntry.class);
    }
}
