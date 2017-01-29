package org.abcmap.core.tileanalyse;

import org.abcmap.core.dao.AbstractOrmDAO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Store interest point of images
 */
public class InterestPointStorage extends AbstractOrmDAO{

    public InterestPointStorage(Path database) throws IOException {
        super(database, InterestPointContainer.class);
    }

}
