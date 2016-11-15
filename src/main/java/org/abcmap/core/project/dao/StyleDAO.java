package org.abcmap.core.project.dao;

import org.abcmap.core.styles.StyleContainer;

import java.nio.file.Path;

/**
 *
 */
public class StyleDAO extends AbstractOrmDAO {

    public StyleDAO(Path p) throws DAOException {
        super(p, StyleContainer.class);
    }

}
