package org.abcmap.core.dao;

/**
 * Used in lambdas to visit style collections
 */
public interface DAOVisitor {
    public boolean processObject(Object o);
}
