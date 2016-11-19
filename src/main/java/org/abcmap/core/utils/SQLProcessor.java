package org.abcmap.core.utils;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface used with SQL operations
 * <p>
 * "Function" is not used here because it throw SQLException
 */
public interface SQLProcessor {
    public Object process(Connection conn) throws Exception;
}
