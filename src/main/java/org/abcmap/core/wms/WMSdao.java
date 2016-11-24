package org.abcmap.core.wms;

import org.abcmap.core.dao.AbstractOrmDAO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by remipassmoilesel on 24/11/16.
 */
public class WMSdao extends AbstractOrmDAO {
    public WMSdao(Path dbPath) throws IOException {
        super(dbPath, WMSEntry.class);
    }
}
