package org.abcmap.core.imageanalyzer;

import org.abcmap.core.project.dao.AbstractOrmDAO;
import org.abcmap.core.project.dao.DAOException;

import java.nio.file.Path;

/**
 * Store interest point of tiles
 */
public class MatchablePointStorage extends AbstractOrmDAO{

    public MatchablePointStorage(Path database) throws DAOException {
        super(database, MatchablePointContainer.class);
    }

}
