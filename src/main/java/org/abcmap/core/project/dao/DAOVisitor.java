package org.abcmap.core.project.dao;

/**
 * Used in lambdas to visit style collections
 */
public interface DAOVisitor {
    public boolean processObject(Object o);
}
